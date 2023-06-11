package com.inhatc.metrovote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainpageActivity extends AppCompatActivity {

    private Button intoQrInfoBtn;

    private Button intoStaionInfoBtn;
    private Button inToMyPageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        intoQrInfoBtn = (Button) findViewById(R.id.inToSubwaySelectPageBtn);
        intoStaionInfoBtn = (Button) findViewById(R.id.inToStationInfoBtn);
        inToMyPageBtn = (Button) findViewById(R.id.inToMyPageBtn);

        intoQrInfoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainpageActivity.this, SelectSubwayActivity.class);
                startActivity(intent);
            }
        });

        intoStaionInfoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainpageActivity.this, StationInfoActivity.class);
                startActivity(intent);
            }
        });


        inToMyPageBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainpageActivity.this, MyPageActivity.class);
                startActivity(intent);
            }
        });
    }
}