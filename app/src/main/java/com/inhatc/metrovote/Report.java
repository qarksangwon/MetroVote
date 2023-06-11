package com.inhatc.metrovote;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

public class Report extends AppCompatActivity {

    private LinearLayout layoutReport;
    private LinearLayout layoutTempDown;
    private LinearLayout layoutTempUp;
    private String receviedTrainID;
    private TextView txtTrainID;
    private Button btnSaveReport;
    private Button btnSavePseudo;
    private Button btnSaveShopkeeper;
    RadioGroup radioGroup;
    private FirebaseFirestore db;


    private Handler handler = new Handler();  // Handler 객체 사용

    private void showReportLayout() {
        layoutReport.setVisibility(View.VISIBLE);
        layoutTempDown.setVisibility(View.GONE);
        layoutTempUp.setVisibility(View.GONE);
        TabReport tabReport = new TabReport();

        btnSaveReport = (Button) findViewById(R.id.btnNoise);
        btnSavePseudo = (Button) findViewById(R.id.btnPseudo);
        btnSaveShopkeeper = (Button) findViewById(R.id.btnShopkeeper);

        btnSaveReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    tabReport.saveReportData("Noise", receviedTrainID);
                    setButtonClickable(false,1);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            setButtonClickable(true,1);
                        }
                    }, 5 * 60 * 1000);

            }
        });

        btnSavePseudo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabReport.saveReportData("Pseudo", receviedTrainID);
                setButtonClickable(false,2);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setButtonClickable(true,2);
                    }
                }, 5 * 60 * 1000);
            }
        });

        btnSaveShopkeeper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tabReport.saveReportData("Shopkeeper", receviedTrainID);
                setButtonClickable(false,3);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        setButtonClickable(true,3);
                    }
                }, 5 * 60 * 1000);
            }
        });
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
    // 버튼 딜레이 주기용
    private void setButtonClickable(boolean clickable,int i) {
        if(i==1)
            btnSaveReport.setEnabled(clickable);
        else if(i ==2)
            btnSavePseudo.setEnabled(clickable);
        else if(i==3)
            btnSaveShopkeeper.setEnabled(clickable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        radioGroup = findViewById(R.id.radioGroup);
        receviedTrainID = getIntent().getStringExtra("trainID").toString(); // 이전 layout에서 전달받은 tarinID
        txtTrainID = findViewById(R.id.tabReportTrainID);
        txtTrainID.setText("열차번호 : " + receviedTrainID);

        layoutReport = findViewById(R.id.layoutReport);
        layoutTempDown = findViewById(R.id.layoutTempDown);
        layoutTempUp = findViewById(R.id.layoutTempUp);

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