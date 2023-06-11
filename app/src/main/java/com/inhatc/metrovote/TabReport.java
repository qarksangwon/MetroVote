package com.inhatc.metrovote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

public class TabReport extends AppCompatActivity {


    private static LocalDate nowDay;
    private static LocalTime nowTime;

    private static DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_report);

        // Firebase 데이터베이스 연결 및 초기화
        database = FirebaseDatabase.getInstance().getReference();
    }

    public static void saveReportData(String reportType, String trainID) {
        if (database  == null) {
            database = FirebaseDatabase.getInstance().getReference();
        }

        nowDay = LocalDate.now();
        nowTime = LocalTime.now();
        int month = nowDay.getMonthValue();
        int day = nowDay.getDayOfMonth();
        int hour = nowTime.getHour();
        int min = nowTime.getMinute();
        String result = month + "/" + day + "/" + hour + "/" + min;

        // 필드와 값으로 구성된 데이터 추가
        Map<String, Object> reportData = new HashMap<>();
        reportData.put("열차번호", trainID);
        reportData.put("신고내용", reportType);
        reportData.put("신고시간", result);


        database.child(reportType).push().setValue(reportData)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("Firebase", "Data added successfully");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull  Exception e) {
                        Log.e("Firebase", "Error adding data", e);
                    }
                });

    }
}
