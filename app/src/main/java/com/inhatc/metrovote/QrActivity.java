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

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class QrActivity extends AppCompatActivity {


    private Button btnTest ;
    private Button btnScan;
    private TextView txtTest ;
    private TextView txtQrResult;
    // 역 정보를 받았다 치고, 내가 타고 있는 열차 코드를 입력 받았다 가정.(현재는 알 수 없으니 list로 모든 차 적용)

    private String statnNm = "오이도";
    private String[] btrainNo = {"K6000","K6008","K6024","K6500","K6402","K6502","K6802"};

    // api와 키

    XmlPullParser xpp;

    //출력해서 보여줄 변수
    private String bstatnNm; //종착지 명
    private String subwayId; //호선ID
    
    private String btrainMe; // 내가타고있는 열차번호

    private String qrResult; // QR결과값





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qr);

        btnTest =findViewById(R.id.btnTest);
        btnScan =findViewById(R.id.btnScan);
        txtTest =findViewById(R.id.txtTest);
        btnTest.setEnabled(false);
        txtQrResult =findViewById(R.id.txtQrResult);

        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // QR 스캐너 실행
                IntentIntegrator integrator = new IntentIntegrator(QrActivity.this);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();

            }

        });


        btnTest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                StringBuffer buffer = new StringBuffer();
                String location = URLEncoder.encode(statnNm);
                String query = "";

                String queryUrl= "https://api.metrovote.com/subway/6642417264646c643438445851766e/xml/realtimeStationArrival/0/4/"+location;
                try{
                    URL url = new URL(queryUrl); //URL생성
                    InputStream is = url.openStream(); //url 위치로 입렵 스트림 연결

                    XmlPullParserFactory factory = XmlPullParserFactory.newInstance(); //xml 파싱을 위해서 팩토리 생성
                    XmlPullParser xpp = factory.newPullParser();
                    xpp.setInput(new InputStreamReader(is, "UTF-8")); //inputstream으로 xml 입력 받기.

                    String tag;

                    xpp.next();
                    int eventType = xpp.getEventType();
                    boolean tnoCheck = false; // 호선 ID 찾을용도
                    while(eventType != XmlPullParser.END_DOCUMENT){
                        switch(eventType){
                            case XmlPullParser.START_DOCUMENT:
                                System.out.println("파싱 시작");
                                break;

                            case XmlPullParser.START_TAG:
                                tag = xpp.getName(); // 태그 이름 가져오기
                                // 호선 ID
                                if(tag.equals("subwayId")) {
                                    xpp.next();
                                    subwayId = xpp.getText();

                                }
                                else if(tag.equals("btrainNo")){
                                    xpp.next();
                                    btrainMe = xpp.getText();
                                    for(int i =0; i<btrainNo.length; i++) {
                                        if (btrainNo[i].equals(btrainMe)) {
                                            tnoCheck = true;
                                            subwayId = subWayCheck(subwayId);
                                        }
                                    }
                                }
                                // 종착지 지하철명
                                else if(tag.equals("bstatnNm")){
                                    xpp.next();
                                    bstatnNm = xpp.getText();
                                    if(tnoCheck)
                                        break;
                                }
                        }
                        eventType = xpp.next();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

                subwayId = subWayCheck(subwayId);
                txtTest.setText("호선 :" + subwayId + "\n"
                            + "종착역 : " + bstatnNm + "\n"
                            + "내가 타고 있는 차 번호 : " + btrainMe);

            }
        });

    }

    //호선 구별용 메소드
    private String subWayCheck(String subwayId){
        String result = "비내리는호남선";
        if(subwayId != null){
            if(subwayId.equals("1004")){
            result = "4호선";
            }else if(subwayId.equals("1075")){
            result = "수인분당선";
            }
        }
        return result;
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
                txtQrResult.setText("스캔 결과: " + qrResult);
                btnTest.setEnabled(true);
            }
        }
    }

}
