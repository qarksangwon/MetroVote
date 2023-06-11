package com.inhatc.metrovote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inhatc.metrovote.api.SubwayArriveAPI;
import com.inhatc.metrovote.api.SubwayArriveDTO;
import com.inhatc.metrovote.firebase.StationVO;

import java.util.ArrayList;

public class SubwayFindByGpsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private double nowLongitude;
    private double nowLatitude;

    private double minDifValue = Double.MAX_VALUE;

    private SupportMapFragment mapFragment; //맵 정보

    private GoogleMap googleMap;
    private Bitmap markerBitmap;

    private Marker depStationMarker;    //출발역
    private Marker desStationMarker;    //목적지역

    private StationVO depStation = null;
    private StationVO desStation;

    private Marker playerMarker;

    private SubwayArriveAPI subwayArriveAPI;

    private ArrayList<StationVO> stationVOList;
    private ArrayList<StationVO> resultStationList;
    private ArrayList<SubwayArriveDTO> subwayArriveDTOList;

    private boolean isUP;
    private boolean isFirstDataRetrieved = false;
    private boolean isSecondDataRetrieved = false;

    private boolean isDepStationAvailable = false;

    final int MAX_PRINT_LIST_SIZE = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subway_find_by_gps);

        FirebaseApp.initializeApp(this);
        stationVOList = new ArrayList<StationVO>();
        resultStationList = new ArrayList<StationVO>(); //파이어베이스 및 리스트 초기화

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference stationsRef = rootRef.child("stations");

        // "4호선" 경로의 참조
        DatabaseReference line4Ref = stationsRef.child("4호선");

        // "수인분당선" 경로의 참조
        DatabaseReference suinBundangRef = stationsRef.child("수인분당선");

        // "4호선" 경로 조회
        fetchLine4Data(line4Ref);
        // "수인분당선" 경로 조회
        fetchSBData(suinBundangRef);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) { //현재 위치가 바뀔 떄 호출

                // 위치 정보 변경 시 호출됨
                nowLatitude = (float) location.getLatitude(); // 현재 위도
                nowLongitude = (float) location.getLongitude(); // 현재 경도

                if(isDepStationAvailable){
                    desStation = checkMoreNearStations();
                    Log.d("desStation", desStation.getStatnName());
                    updateMarker();
                }

                while(isFirstDataRetrieved && isSecondDataRetrieved) { // 도착역 찾기
                    depStation = checkNearStations();
                    Log.d("depStation", depStation.getStatnName());
                    isFirstDataRetrieved = false;
                    isSecondDataRetrieved = false;
                    isDepStationAvailable = true;
                }



                // 위치 정보를 활용한 작업 수행
                Log.d("Location", "Latitude: " + nowLatitude + ", Longitude: " + nowLongitude);

                // 위치 정보 사용 후 리스너 등록 해제
                //locationManager.removeUpdates(this);

            }

            @Override
            public void onProviderEnabled(String provider) {
                // 위치 공급자 사용 가능 상태로 변경 시 호출됨
            }

            @Override
            public void onProviderDisabled(String provider) {
                // 위치 공급자 사용 불가능 상태로 변경 시 호출됨
                Toast.makeText(SubwayFindByGpsActivity.this, "위치공급자가 사용 불가능합니다.", Toast.LENGTH_SHORT).show();
            }
        };


        // 위치 권한 확인
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            if (currentLocation != null) {
                nowLatitude = (float) currentLocation.getLatitude(); // 현재 위도
                nowLongitude = (float) currentLocation.getLongitude(); // 현재 경도
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 100, locationListener);

                if(isFirstDataRetrieved && isSecondDataRetrieved) {
                    depStation = checkNearStations(); //출발 역 찾기
                    isDepStationAvailable = true;
                }
                // 위치 정보를 활용한 작업 수행
                Log.d("Location", "Latitude: " + nowLatitude + ", Longitude: " + nowLongitude);
            } else {
                String provider = LocationManager.GPS_PROVIDER;
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 100, locationListener);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 100, locationListener);
            }
        } else {
            // 위치 권한이 거부된 경우 권한 요청 필요
            // (권한 요청 코드 및 처리는 생략되어 있음)
        }

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng depLocation = new LatLng(depStation.getStatnLat(), depStation.getStatnLong());
        LatLng desLocation = new LatLng(desStation.getStatnLat(), desStation.getStatnLong());
        LatLng nowLocation = new LatLng(nowLatitude, nowLongitude);

        // 마커 초기화
        if (depStationMarker != null) {
            depStationMarker.remove();
            depStationMarker = null;
        }
        if (playerMarker != null) {
            playerMarker.remove();
            playerMarker = null;
        }

        if (desStationMarker != null) {
            desStationMarker.remove();
            desStationMarker = null;
        }

        // 마커를 추가합니다.
        depStationMarker = googleMap.addMarker(new MarkerOptions()
                .position(depLocation)
                .title(depStation.getStatnName()));

        desStationMarker = googleMap.addMarker(new MarkerOptions()
                .position(desLocation)
                .title(desStation.getStatnName()));

        playerMarker = googleMap.addMarker(new MarkerOptions()
                .position(nowLocation)
                .title("현재위치"));

        // 마커들이 포함된 LatLngBounds 객체 생성
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(depStationMarker.getPosition()); // 첫 번째 마커
        builder.include(desStationMarker.getPosition());
        builder.include(playerMarker.getPosition()); // 두 번째 마커
        LatLngBounds bounds = builder.build();

        // 카메라 이동 및 줌 조정
        int padding = 200; // 마커와 맵 경계 간의 여백 (픽셀 단위)
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        googleMap.moveCamera(cameraUpdate);

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);


    }

    private void fetchLine4Data(DatabaseReference line4Ref) {
        line4Ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // 데이터 조회 성공 시 호출되는 콜백 메서드
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    StationVO vo = new StationVO();
                    vo.setStatnID(snapshot.child("STATN_ID").getValue(Integer.class).toString());
                    vo.setLineName(snapshot.child("호선이름").getValue(String.class));
                    vo.setStatnLat(snapshot.child("STATN_LAT").getValue(Double.class));
                    vo.setStatnLong(snapshot.child("STATN_LONG").getValue(Double.class));
                    vo.setStatnName(snapshot.child("STATN_NM").getValue(String.class));
                    stationVOList.add(vo);
                }
                isFirstDataRetrieved = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 조회 실패 시 처리
            }
        });
    }

    private void fetchSBData(DatabaseReference sbRef) {
        sbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // 데이터 조회 성공 시 호출되는 콜백 메서드
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    StationVO vo = new StationVO();
                    vo.setStatnID(snapshot.child("STATN_ID").getValue(Integer.class).toString());
                    vo.setLineName(snapshot.child("호선이름").getValue(String.class));
                    vo.setStatnLat(snapshot.child("STATN_LAT").getValue(Double.class));
                    vo.setStatnLong(snapshot.child("STATN_LONG").getValue(Double.class));
                    vo.setStatnName(snapshot.child("STATN_NM").getValue(String.class));
                    stationVOList.add(vo);
                }
                isSecondDataRetrieved = true;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 조회 실패 시 처리
            }
        });
    }

    private StationVO checkNearStations() {
        minDifValue = Double.MAX_VALUE;
        StationVO nearStation = new StationVO();
        for (StationVO vo: stationVOList) {
            double difValue = Math.abs(nowLatitude - vo.getStatnLat()) + Math.abs(nowLongitude - vo.getStatnLong());
            vo.setDistance(difValue); //처음 역과의 거리 저장

            if(minDifValue > difValue) {
                nearStation = vo;
               minDifValue = difValue;
            }
        }
        return nearStation;

    }

    private StationVO checkMoreNearStations() {
        StationVO nearStation = null;
        for (StationVO vo: stationVOList) {

            Double oriDifValue = vo.getDistance(); //처음 역과의 거리 불러오기
            double difValue = Math.abs(nowLatitude - vo.getStatnLat()) + Math.abs(nowLongitude - vo.getStatnLong());

            if(oriDifValue > difValue) {
                nearStation = new StationVO();
                nearStation = vo;
                minDifValue = difValue;
            }

        }
        if(nearStation == null) { //값이 달라지지 않았으면 출발역 반환
            return depStation;
        } else {
            return nearStation;
        }


    }

    private void updateMarker() {
        mapFragment.getMapAsync(this);
    }


}