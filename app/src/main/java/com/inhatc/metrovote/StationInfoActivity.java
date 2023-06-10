package com.inhatc.metrovote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inhatc.metrovote.api.SubwayArriveAPI;
import com.inhatc.metrovote.api.SubwayArriveDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class StationInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView txtStationInfo;
    private TextView txtLineInfo;
    private TextView txtToiletInfo;

    private TextView txtDistanceInfo;

    private String stationName;
    private String lineName;

    private String statn_num;
    private float nowLongitude;
    private float nowLatitude;

    private float minDifValue = Float.MAX_VALUE;

    private double latitude = 37.361773, longitude = 126.738437;

    private Boolean isToiletAvailable;

    private boolean isFirstDataRetrieved = false;
    private boolean isSecondDataRetrieved = false;

    private SupportMapFragment mapFragment; //맵 정보

    private GoogleMap googleMap;
    private Bitmap markerBitmap;

    private Marker stationMarker;
    private Marker playerMarker;
    private Polyline polyline;

    private SubwayArriveAPI subwayArriveAPI;

    private ArrayList<SubwayArriveDTO> subwayArriveDTOList;
    private RecyclerView recyclerView;
    private SubwayArriveAdapter adapter;

    private CheckBox chkIsUp;

    private boolean isUP;

    final int MAX_PRINT_LIST_SIZE = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_info);
        FirebaseApp.initializeApp(this);


        subwayArriveAPI = new SubwayArriveAPI();
        subwayArriveAPI.setOnFetchDataListener(new SubwayArriveAPI.OnFetchDataListener() {
            @Override
            public void onFetchDataSuccess(ArrayList<SubwayArriveDTO> subwayArriveList) {

                subwayArriveDTOList = subwayArriveList;

                // 정렬 작업
                ArrayList<SubwayArriveDTO> sortedList = new ArrayList<>(subwayArriveDTOList);

                Comparator<SubwayArriveDTO> comparator = (dto1, dto2) -> {
                    Date currentTime = new Date();

                    // dto1과 dto2의 ArriveTime과 LeftTime 가져오기
                    Date arriveTime1 = dto1.getArriveTime();
                    Date leftTime1 = dto1.getLeftTime();
                    Date arriveTime2 = dto2.getArriveTime();
                    Date leftTime2 = dto2.getLeftTime();

                    // dto1과 dto2의 ArriveTime과 현재 시간과의 차이 계산
                    long difference1 = arriveTime1.getTime() - currentTime.getTime();
                    long difference2 = arriveTime2.getTime() - currentTime.getTime();

                    // dto1과 dto2의 LeftTime과 현재 시간과의 차이 계산
                    long leftDifference1 = leftTime1.getTime() - currentTime.getTime();
                    long leftDifference2 = leftTime2.getTime() - currentTime.getTime();

                    // ArriveTime이 0인 경우 해당 LeftTime으로 정렬
                    if (arriveTime1.getTime() == 0) {
                        return Long.compare(leftDifference1, leftDifference2);
                    } else if (arriveTime2.getTime() == 0) {
                        return Long.compare(leftDifference2, leftDifference1);
                    }

                    // LeftTime이 0인 경우 해당 ArriveTime으로 정렬
                    if (leftTime1.getTime() == 0) {
                        return Long.compare(difference1, difference2);
                    } else if (leftTime2.getTime() == 0) {
                        return Long.compare(difference2, difference1);
                    }

                    // 둘 다 0인 경우 다른 시간으로 정렬 (예: ArriveTime 기준)
                    return Long.compare(difference1, difference2);
                    };

                Collections.sort(sortedList, comparator);

                // 최대 리스트 크기 조정
                while (sortedList.size() > MAX_PRINT_LIST_SIZE) {
                    sortedList.remove(sortedList.size() - 1);
                }

                // UI 업데이트
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView = findViewById(R.id.recyclerView);
                        recyclerView.setLayoutManager(new LinearLayoutManager(StationInfoActivity.this));
                        adapter = new SubwayArriveAdapter(sortedList);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }

            @Override
            public void onFetchDataError(String errorMessage) {
                subwayArriveDTOList = new ArrayList<SubwayArriveDTO>();
                subwayArriveDTOList.add(new SubwayArriveDTO());
                Toast.makeText(StationInfoActivity.this, "실시간 정보를 요청하는데 오류가 발생하였습니다.", Toast.LENGTH_SHORT).show();
            }
        });



        txtStationInfo = findViewById(R.id.txtStationInfo);
        txtLineInfo = findViewById(R.id.txtLineInfo);
        txtToiletInfo = findViewById(R.id.txtIsToiletInfo);
        txtDistanceInfo = findViewById(R.id.txtDistanceInfo);
        chkIsUp = findViewById(R.id.chkIsUp);
        chkIsUp.setSelected(true);
        
        chkIsUp.setOnCheckedChangeListener((buttonView, isChecked) -> { //상행하행 변경 이벤트
            isUP = isChecked;
                subwayArriveAPI.fetchDataFromAPI(getApplicationContext(), statn_num, isUP);
        });


        // 원하는 크기로 이미지 조정
        int width = 200; // 변경할 너비
        int height = 200; // 변경할 높이
        markerBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.marker_icon), width, height, false);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference stationsRef = rootRef.child("stations");

        // "4호선" 경로의 참조
        DatabaseReference line4Ref = stationsRef.child("4호선");

        // "수인분당선" 경로의 참조
        DatabaseReference suinBundangRef = stationsRef.child("수인분당선");

        // "4호선" 경로 조회
        fetchLine4Data(line4Ref);
        // "수인분당선" 경로 조회
        fetchSBLineData(suinBundangRef);

        // 위치 관리자 객체 생성
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        // 위치 정보 수신을 위한 리스너 정의
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) { //현재 위치가 바뀔 떄 호출

                // 위치 정보 변경 시 호출됨
                nowLatitude = (float) location.getLatitude(); // 현재 위도
                nowLongitude = (float) location.getLongitude(); // 현재 경도

                // 위치 정보를 활용한 작업 수행
                Log.d("Location", "Latitude: " + nowLatitude + ", Longitude: " + nowLongitude);

                // 위치 정보 사용 후 리스너 등록 해제
                //locationManager.removeUpdates(this);

                fetchLine4Data(line4Ref);
                fetchSBLineData(suinBundangRef);
                if (isFirstDataRetrieved && isSecondDataRetrieved) {
                    updateMarker();
                    subwayArriveAPI.fetchDataFromAPI(getApplicationContext(), statn_num, isUP);
                }
            }


            @Override
            public void onProviderEnabled(String provider) {
                // 위치 공급자 사용 가능 상태로 변경 시 호출됨
            }

            @Override
            public void onProviderDisabled(String provider) {
                // 위치 공급자 사용 불가능 상태로 변경 시 호출됨
                Toast.makeText(StationInfoActivity.this, "위치공급자가 사용 불가능합니다.", Toast.LENGTH_SHORT).show();
            }
        };


        // 위치 권한 확인
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (lastKnownLocation != null) {
                nowLatitude = (float) lastKnownLocation.getLatitude(); // 현재 위도
                nowLongitude = (float) lastKnownLocation.getLongitude(); // 현재 경도
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, locationListener);

                // 위치 정보를 활용한 작업 수행
                Log.d("Location", "Latitude: " + nowLatitude + ", Longitude: " + nowLongitude);
            } else {
                // 위치 권한이 허용된 경우 위치 업데이트 요청
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, locationListener);
            }
        } else {
            // 위치 권한이 거부된 경우 권한 요청 필요
            // (권한 요청 코드 및 처리는 생략되어 있음)
        }

    }
    private void updateMarker() {
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng location = new LatLng(latitude, longitude);
        LatLng nowLocation = new LatLng(nowLatitude, nowLongitude);

        // 마커 초기화
        if (stationMarker != null) {
            stationMarker.remove();
            stationMarker = null;
        }
        if (playerMarker != null) {
            playerMarker.remove();
            playerMarker = null;
        }

        // 선 초기화
        if (polyline != null) {
            polyline.remove();
            polyline = null;
        }

        // 마커를 추가합니다.
        stationMarker = googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title(stationName));

        playerMarker = googleMap.addMarker(new MarkerOptions()
                .position(nowLocation)
                .title("현재위치")
                .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap)));

        // 마커들이 포함된 LatLngBounds 객체 생성
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(stationMarker.getPosition()); // 첫 번째 마커
        builder.include(playerMarker.getPosition()); // 두 번째 마커
        LatLngBounds bounds = builder.build();

        // 카메라 이동 및 줌 조정
        int padding = 200; // 마커와 맵 경계 간의 여백 (픽셀 단위)
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.moveCamera(cameraUpdate);

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        /* 선 그리기 */
        Location stationLocation = new Location("Station");
        stationLocation.setLatitude(stationMarker.getPosition().latitude);
        stationLocation.setLongitude(stationMarker.getPosition().longitude);

        Location playerLocation = new Location("Location 2");
        playerLocation.setLatitude(playerMarker.getPosition().latitude);
        playerLocation.setLongitude(playerMarker.getPosition().longitude);

        PolylineOptions polylineOptions = new PolylineOptions()
                .add(stationMarker.getPosition())
                .add(playerMarker.getPosition())
                .color(Color.BLUE);

        polyline = googleMap.addPolyline(polylineOptions);
        float distance = stationLocation.distanceTo(playerLocation);

        txtDistanceInfo.setText("약 " + String.format("%.2f", distance) + " 미터");
    }

    private void fetchLine4Data(DatabaseReference line4Ref) {
        line4Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // 데이터 조회 성공 시 호출되는 콜백 메서드
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Float stationLong = snapshot.child("STATN_LONG").getValue(Float.class);
                    Float stationLat = snapshot.child("STATN_LAT").getValue(Float.class);

                    float difValue = Math.abs(nowLongitude - stationLong) + Math.abs(nowLatitude - stationLat);

                    if (difValue < minDifValue) {
                        stationName = snapshot.child("STATN_NM").getValue(String.class);
                        statn_num = snapshot.child("STATN_ID").getValue(Integer.class).toString();
                        statn_num = statn_num.substring(statn_num.length() - 3);
                        lineName = snapshot.child("호선이름").getValue(String.class);
                        latitude = snapshot.child("STATN_LAT").getValue(Double.class);
                        longitude = snapshot.child("STATN_LONG").getValue(Double.class);
                        isToiletAvailable = snapshot.child("Is_Toilet_in").getValue(Boolean.class);
                        minDifValue = difValue;
                    }
                }
                isFirstDataRetrieved = true;

                // 두 개의 데이터 조회가 모두 완료되었는지 확인
                if (isFirstDataRetrieved && isSecondDataRetrieved) {
                    if (isToiletAvailable) {
                        txtToiletInfo.setText("있음");
                        txtToiletInfo.setTextColor(Color.BLACK);
                    } else {
                        txtToiletInfo.setText("없음");
                        txtToiletInfo.setTextColor(Color.GRAY);
                    }

                    txtStationInfo.setText(stationName);
                    txtLineInfo.setText(lineName);
                    mapFragment.getMapAsync(StationInfoActivity.this);
                    subwayArriveAPI.fetchDataFromAPI(getApplicationContext(), statn_num, isUP);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 조회 실패 시 처리
            }
        });
    }

    private void fetchSBLineData(DatabaseReference SBRef) {
        SBRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // 데이터 조회 성공 시 호출되는 콜백 메서드
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Float stationLong = snapshot.child("STATN_LONG").getValue(Float.class);
                    Float stationLat = snapshot.child("STATN_LAT").getValue(Float.class);

                    float difValue = Math.abs(nowLongitude - stationLong) + Math.abs(nowLatitude - stationLat);

                    if (difValue < minDifValue) {
                        stationName = snapshot.child("STATN_NM").getValue(String.class);
                        statn_num = snapshot.child("STATN_ID").getValue(Integer.class).toString();
                        statn_num = statn_num.substring(statn_num.length() - 3);
                        lineName = snapshot.child("호선이름").getValue(String.class);
                        latitude = snapshot.child("STATN_LAT").getValue(Double.class);
                        longitude = snapshot.child("STATN_LONG").getValue(Double.class);
                        isToiletAvailable = snapshot.child("Is_Toilet_in").getValue(Boolean.class);
                        minDifValue = difValue;
                    }
                }
                isSecondDataRetrieved = true;

                // 두 개의 데이터 조회가 모두 완료되었는지 확인
                if (isFirstDataRetrieved && isSecondDataRetrieved) {
                    if (isToiletAvailable) {
                        txtToiletInfo.setText("있음");
                        txtToiletInfo.setTextColor(Color.BLACK);
                    } else {
                        txtToiletInfo.setText("없음");
                        txtToiletInfo.setTextColor(Color.GRAY);
                    }
                        txtStationInfo.setText(stationName);
                        txtLineInfo.setText(lineName);
                    mapFragment.getMapAsync(StationInfoActivity.this);
                    subwayArriveAPI.fetchDataFromAPI(getApplicationContext(), statn_num, isUP);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 조회 실패 시 처리
            }
        });
    }




}