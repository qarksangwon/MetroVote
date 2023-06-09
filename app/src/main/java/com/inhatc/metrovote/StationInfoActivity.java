package com.inhatc.metrovote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

public class StationInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView txtStationInfo;
    private TextView txtLineInfo;
    private TextView txtToiletInfo;
    private MapView mapView;

    private String stationName;
    private String lineName;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_info);
        FirebaseApp.initializeApp(this);

        txtStationInfo = findViewById(R.id.txtStationInfo);
        txtLineInfo = findViewById(R.id.txtLineInfo);
        txtToiletInfo = findViewById(R.id.txtIsToiletInfo);

        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromResource(R.drawable.marker_icon);

        // 원하는 크기로 이미지 조정
        int width = 200; // 변경할 너비
        int height = 200; // 변경할 높이
        markerBitmap = Bitmap.createScaledBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.marker_icon), width, height, false);

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
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
                locationManager.removeUpdates(this);

                if (isFirstDataRetrieved && isSecondDataRetrieved) {
                    updateMarker();
                }
            }


            @Override
            public void onProviderEnabled(String provider) {
                // 위치 공급자 사용 가능 상태로 변경 시 호출됨
            }

            @Override
            public void onProviderDisabled(String provider) {
                // 위치 공급자 사용 불가능 상태로 변경 시 호출됨
            }
        };

        // 위치 권한 확인
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            if (lastKnownLocation != null) {
                nowLatitude = (float) lastKnownLocation.getLatitude(); // 현재 위도
                nowLongitude = (float) lastKnownLocation.getLongitude(); // 현재 경도

                // 위치 정보를 활용한 작업 수행
                Log.d("Location", "Latitude: " + nowLatitude + ", Longitude: " + nowLongitude);
            } else {
                // 위치 권한이 허용된 경우 위치 업데이트 요청
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        } else {
            // 위치 권한이 거부된 경우 권한 요청 필요
            // (권한 요청 코드 및 처리는 생략되어 있음)
        }

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference stationsRef = rootRef.child("stations");

        // "4호선" 경로의 참조
        DatabaseReference line4Ref = stationsRef.child("4호선");

        // "수인분당선" 경로의 참조
        DatabaseReference suinBundangRef = stationsRef.child("수인분당선");


// "4호선" 경로 조회
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
                                if(isToiletAvailable) {
                                    txtToiletInfo.setText("있음");
                                } else {
                                    txtToiletInfo.setText("없음");
                                }
                                mapFragment.getMapAsync(StationInfoActivity.this);

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 조회 실패 시 처리
            }
        });

// "수인분당선" 경로 조회
        suinBundangRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // 데이터 조회 성공 시 호출되는 콜백 메서드
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Float stationLong = snapshot.child("STATN_LONG").getValue(Float.class);
                    Float stationLat = snapshot.child("STATN_LAT").getValue(Float.class);

                    float difValue = Math.abs(nowLongitude - stationLong) + Math.abs(nowLatitude - stationLat);

                    if (difValue < minDifValue) {
                        stationName = snapshot.child("STATN_NM").getValue(String.class);
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
                    // 모든 데이터 조회가 완료되었으므로 코드를 진행

                                if(isToiletAvailable) {
                                    txtToiletInfo.setText("있음");
                                } else {
                                    txtToiletInfo.setText("없음");
                                }
                                txtStationInfo.setText(stationName);
                                txtLineInfo.setText(lineName);
                                mapFragment.getMapAsync(StationInfoActivity.this);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // 조회 실패 시 처리
            }
        });

    }
    private void updateMarker() {
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        LatLng location = new LatLng(latitude, longitude);
        LatLng nowLocation = new LatLng(nowLatitude, nowLongitude);

        // 카메라를 위치로 이동시킵니다.
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(location));

        // 줌 레벨을 설정합니다.
        float zoomLevel = 15.0f;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, zoomLevel));

        // 마커를 추가합니다.
        googleMap.addMarker(new MarkerOptions()
                .position(location)
                .title(stationName));

        // 이미지 리소스를 Bitmap으로 변환


        googleMap.addMarker(new MarkerOptions()
                .position(nowLocation)
                .title("현재위치")
                .icon(BitmapDescriptorFactory.fromBitmap(markerBitmap)));

        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
    }


}