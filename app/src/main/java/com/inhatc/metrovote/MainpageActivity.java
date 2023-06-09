package com.inhatc.metrovote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainpageActivity extends AppCompatActivity {

    private Button inToStationInfoBtn;
    private Button inToMyPageBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        inToStationInfoBtn = (Button) findViewById(R.id.inToStationInfoBtn);
        inToStationInfoBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainpageActivity.this, StationInfoActivity.class);
                startActivity(intent);
            }
        });

        inToMyPageBtn = (Button) findViewById(R.id.inToMyPageBtn);
        inToMyPageBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainpageActivity.this, MyPageActivity.class);
                startActivity(intent);
            }
        });
    }
}