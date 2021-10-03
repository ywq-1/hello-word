package com.example.agriculture;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cn.com.newland.nle_sdk.responseEntity.SensorDataPageDTO;
import cn.com.newland.nle_sdk.responseEntity.base.BaseResponseEntity;
import cn.com.newland.nle_sdk.util.NCallBack;
import cn.com.newland.nle_sdk.util.NetWorkBusiness;
import retrofit2.Call;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {
    private LineChart lineChart; //声明图标控件
    private String accessToken;
    private EditText Stime;         //开始时间
    private EditText Etime;         //结束时间
    private Button history_temperature; //历史温度
    private Button history_humidity;   //历史湿度
    private Button history_illumination; //历史光照

    private String S_type;          //类型
    private String S_time;          //开始时间
    private String E_time;          //结束时间

    private NetWorkBusiness netWorkBusiness;
    private String DeviceID = "310656";        //设备ID
    private String Data;
    private String Time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actiyity_history);

        history_temperature = findViewById(R.id.history_temperature); //历史温度
        history_humidity = findViewById(R.id.history_humidity);   //历史湿度
        history_illumination = findViewById(R.id.history_illumination); //历史光照

        Stime = findViewById(R.id.S_time);                      //开始时间
        Etime = findViewById(R.id.E_time);                     //结束时间

        S_time = Stime.getText().toString();
        E_time = Etime.getText().toString();

        Bundle bundle = getIntent().getExtras();
        String accessToken = bundle.getString("accessToken");//获得传输秘钥
        netWorkBusiness = new NetWorkBusiness(accessToken, "http://api.nlecloud.com:80/");

        lineChart = findViewById(R.id.pstastic);
        showChart(lineChart);

        history_temperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this,History_Temperation.class);
                Bundle bundle = new Bundle();
                bundle.putString("accessToken",accessToken);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        history_humidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this,History_Humidity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString("accessToken",accessToken);
                intent.putExtras(bundle1);
                startActivity(intent);
            }
        });
        history_illumination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this,History_illumination.class);
                Bundle bundle2 = new Bundle();
                bundle2.putString("accessToken",accessToken);
                intent.putExtras(bundle2);
                startActivity(intent);
            }
        });
    }


    private void showChart(LineChart lineChart) {
        lineChart.setDrawBorders(false);//是否在折线图上添加边框
        lineChart.setNoDataText("暂无数据"); //没有数据时显示
        lineChart.getAxisRight().setEnabled(false);//不显示y轴右边的值
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setDrawAxisLine(false);//不显示x轴
        //设置x轴数据位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(Color.BLACK);
        xAxis.setTextSize(11);
    }

    private void getData() {
        netWorkBusiness.getSensorData(DeviceID, "Temperature", "6", "30","2021-09-10 00:00:00",
                "2021-09-30 00:00:00", "DESC", "20", "0",
                new NCallBack<BaseResponseEntity<SensorDataPageDTO>>() {
                    @Override
                    protected void onResponse(BaseResponseEntity<SensorDataPageDTO> response) {
                    }
                    public void onResponse(final Call<BaseResponseEntity<SensorDataPageDTO>> call, final Response<BaseResponseEntity<SensorDataPageDTO>> response){
                        BaseResponseEntity baseResponseEntity = response.body();
                        if(baseResponseEntity != null){
                            final Gson gson = new Gson();
                            try {
                                JSONObject jsonObject = null;
                                String msg = gson.toJson(response);
                                jsonObject = new JSONObject(msg);
                                JSONObject resultobj = jsonObject.getJSONObject("ResultObj");
                                int count = Integer.parseInt(resultobj.get("Count").toString());
                                JSONArray jsonArray = resultobj.getJSONArray("DataPoints");
                                JSONObject jsonObject1 = jsonArray.getJSONObject(0);
                                JSONArray jsonArray1 = jsonObject1.getJSONArray("PointDTO");
                                List<HashMap<String, Object>> data = new ArrayList<>(100);
                                List<Entry> entries = new ArrayList<>();
                                List<String> list = new ArrayList<>();

                                for (int i = 0; i < count; i++) {
                                    JSONObject resultObj = jsonArray1.getJSONObject(i);
                                    Data = resultObj.get("Value").toString();
                                    Time = resultObj.get("RecordTime").toString();
                                    entries.add(new Entry(Float.parseFloat(Data), i));
                                    list.add(Time);
                                }
                                LineDataSet lineDataSet = new LineDataSet(entries, "历史数据");
                                LineData Sdata = new LineData(list, lineDataSet);
                                lineChart.setData(Sdata);
                                lineChart.setScaleEnabled(true);
                                lineChart.invalidate();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });



        }
}