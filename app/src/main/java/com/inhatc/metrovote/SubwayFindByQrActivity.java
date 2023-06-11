package com.inhatc.metrovote;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class SubwayFindByQrActivity extends AppCompatActivity {


    private Button btnScan;
    private Button btnVote;
    private Button btnGps;

    private TextView txtQrResult;
    private String qrResult; // QR결과값


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        btnScan = (Button) findViewById(R.id.btnScan);
        btnVote = (Button) findViewById(R.id.btnReport);
        //     btnVote.setEnabled(false);

        txtQrResult =findViewById(R.id.txtQrResult);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // QR 스캐너 실행
                IntentIntegrator integrator = new IntentIntegrator(SubwayFindByQrActivity.this);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();
            }
        });

        btnVote.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // 불편사항 신고버튼 실행 => 불편사항 신고 레이아웃 이동
                Intent intent = new Intent(getApplicationContext(), Report.class);
                intent.putExtra("trainID",qrResult ); // trainID로 qr결과값 전달
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "취소되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                qrResult = result.getContents();
                txtQrResult.setText("현재 열차 번호 : " + qrResult);
                btnVote.setEnabled(true);

                /*btnTest.setEnabled(true);*/
            }
        }
    }


}