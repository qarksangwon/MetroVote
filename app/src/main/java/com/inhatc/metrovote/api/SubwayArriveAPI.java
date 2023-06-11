package com.inhatc.metrovote.api;

import android.content.Context;

import com.inhatc.metrovote.R;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SubwayArriveAPI {

    private ArrayList<SubwayArriveDTO> subwayArriveList;
    private OnFetchDataListener listener;

    final int MAX_RESPONSE_SIZE = 300;

    final int DIR_UP = 1;
    final int DIR_DOWN = 2;  //상행, 하행

    final int WEEK_DAY = 1;
    final int SAT_DAY = 2;
    final int SUN_DAY = 3; // 요일 정보

    public SubwayArriveAPI() {
        subwayArriveList = new ArrayList<>();
    }

    public ArrayList<SubwayArriveDTO> getSubwayArriveList() {
        return subwayArriveList;
    }

    public void setOnFetchDataListener(OnFetchDataListener listener) {
        this.listener = listener;
    }

    // 비동기로 데이터를 가져오는 함수
    public void fetchDataFromAPI(Context context, String stationNum, boolean isUP ) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("http://openAPI.seoul.go.kr:8088/");
        stringBuilder.append(context.getString(R.string.subway_key));
        stringBuilder.append("json/SearchSTNTimeTableByFRCodeService/1/");
        stringBuilder.append(MAX_RESPONSE_SIZE);
        stringBuilder.append("/");
        stringBuilder.append(stationNum);
        stringBuilder.append("/");
        stringBuilder.append(isWeekDAy());
        stringBuilder.append("/");
        if(isUP) {
            stringBuilder.append(DIR_UP);
        } else {
            stringBuilder.append(DIR_DOWN);
        }
        stringBuilder.append("/");
        String url = stringBuilder.toString();

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                // 네트워크 요청 실패 처리
                e.printStackTrace();
                if (listener != null) {
                    listener.onFetchDataError(e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseData);
                        JSONObject searchService = jsonObject.getJSONObject("SearchSTNTimeTableByFRCodeService");
                        JSONArray rows = searchService.getJSONArray("row");

                        for (int i = 0; i < rows.length(); i++) {
                            JSONObject row = rows.getJSONObject(i);
                            SubwayArriveDTO subwayArriveDTO = new SubwayArriveDTO();
                            subwayArriveDTO.setLineNum(row.getString("LINE_NUM"));
                            subwayArriveDTO.setFrCode(row.getString("FR_CODE"));
                            subwayArriveDTO.setStationCd(row.getString("STATION_CD"));
                            subwayArriveDTO.setStationNm(row.getString("STATION_NM"));
                            subwayArriveDTO.setTrainNo(row.getString("TRAIN_NO"));


                            String arriveTimeString = row.getString("ARRIVETIME");
                            String leftTimeString = row.getString("LEFTTIME");

                            SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

                            Date arriveTime = null;
                            Date leftTime = null;

                            try {
                                arriveTime = format.parse(arriveTimeString);
                                leftTime = format.parse(leftTimeString);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            subwayArriveDTO.setArriveTime(arriveTime);
                            subwayArriveDTO.setLeftTime(leftTime);


                            subwayArriveDTO.setOriginStation(row.getString("ORIGINSTATION"));
                            subwayArriveDTO.setDestStation(row.getString("DESTSTATION"));
                            subwayArriveDTO.setSubwaysName(row.getString("SUBWAYSNAME"));
                            subwayArriveDTO.setSubwayEName(row.getString("SUBWAYENAME"));
                            subwayArriveDTO.setWeekTag(row.getString("WEEK_TAG"));
                            subwayArriveDTO.setInoutTag(row.getString("INOUT_TAG"));
                            subwayArriveDTO.setFlFlag(row.getString("FL_FLAG"));
                            subwayArriveDTO.setExpressYN(row.getString("EXPRESS_YN"));
                            subwayArriveDTO.setBranchLine(row.getString("BRANCH_LINE"));

                            subwayArriveList.add(subwayArriveDTO);
                        }

                        if (listener != null) {
                            listener.onFetchDataSuccess(subwayArriveList);
                            subwayArriveList = new ArrayList<SubwayArriveDTO>();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        if (listener != null) {
                            listener.onFetchDataError(e.getMessage());
                        }
                    }
                } else {
                    if (listener != null) {
                        listener.onFetchDataError("Response not successful");
                    }
                }
            }
        });
    }

    public interface OnFetchDataListener {
        void onFetchDataSuccess(ArrayList<SubwayArriveDTO> subwayArriveList);
        void onFetchDataError(String errorMessage);
    }

    public int isWeekDAy() {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        // 요일을 확인하여 출력
        if (dayOfWeek == Calendar.SATURDAY) {
            return SAT_DAY;
        } else if (dayOfWeek == Calendar.SUNDAY) {
            return SUN_DAY;
        } else {
            return WEEK_DAY;
        }
    }
}
