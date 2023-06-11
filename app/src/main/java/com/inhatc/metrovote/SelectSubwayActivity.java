package com.inhatc.metrovote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class SelectSubwayActivity extends AppCompatActivity {

    private Button btnGPS;
    private Button btnQR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_subway);

        btnGPS = (Button) findViewById(R.id.btnGPS);
        btnQR  = (Button)  findViewById(R.id.btnQR);

        btnGPS.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectSubwayActivity.this,  SubwayFindByGpsActivity.class);
                startActivity(intent);
            }
        });

        btnQR.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SelectSubwayActivity.this,  SubwayFindByQrActivity.class);
                startActivity(intent);
            }
        });
    }
}