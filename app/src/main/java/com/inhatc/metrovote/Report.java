package com.inhatc.metrovote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TextView;

public class Report extends AppCompatActivity {

    private LinearLayout layoutReport;
    private LinearLayout layoutTempDown;
    private LinearLayout layoutTempUp;
    private String receviedTrainID;
    private TextView txtTrainID;
    RadioGroup radioGroup;
    private void showReportLayout() {
        layoutReport.setVisibility(View.VISIBLE);
        layoutTempDown.setVisibility(View.GONE);
        layoutTempUp.setVisibility(View.GONE);
    }

    private void showTempDownLayout() {
        layoutReport.setVisibility(View.GONE);
        layoutTempDown.setVisibility(View.VISIBLE);
        layoutTempUp.setVisibility(View.GONE);
    }

    private void showTempUpLayout() {
        layoutReport.setVisibility(View.GONE);
        layoutTempDown.setVisibility(View.GONE);
        layoutTempUp.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        radioGroup = findViewById(R.id.radioGroup);
        receviedTrainID = getIntent().getStringExtra("trainID"); // 이전 layout에서 전달받은 tarinID
        txtTrainID = findViewById(R.id.txtTrainID);
        txtTrainID.setText("열차번호 : " + receviedTrainID);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rbtnReport) {
                    showReportLayout();
                } else if (checkedId == R.id.rbtnTempDown) {
                    showTempDownLayout();
                } else if (checkedId == R.id.rbtnTempUP) {
                    showTempUpLayout();
                }
            }
        });

    }
}