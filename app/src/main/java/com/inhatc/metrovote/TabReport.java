package com.inhatc.metrovote;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class TabReport extends AppCompatActivity {

    private Button btnNoise;
    private Button btnPseudo;
    private Button btnShopKeeper;

    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_report);
        btnNoise = findViewById(R.id.btnNoise);
        btnPseudo = findViewById(R.id.btnPseudo);
        btnShopKeeper = findViewById(R.id.btnShopkeeper);

        btnNoise.setOnClickListener(v -> saveReportData("noise"));
        btnPseudo.setOnClickListener(v -> saveReportData("pseudo"));
        btnShopKeeper.setOnClickListener(v -> saveReportData("shopkeeper"));
    }

    private void saveReportData(String reportType) {
        // 특정 버튼을 눌렀을 때 해당 데이터를 Firebase 데이터베이스에 저장합니다.
        databaseReference.child(reportType).push().setValue("Data to be saved");
    }
}