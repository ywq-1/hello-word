package com.example.agriculture;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cn.com.newland.nle_sdk.responseEntity.DeviceState;
import cn.com.newland.nle_sdk.responseEntity.SensorInfo;
import cn.com.newland.nle_sdk.responseEntity.base.BaseResponseEntity;
import cn.com.newland.nle_sdk.util.NCallBack;
import cn.com.newland.nle_sdk.util.NetWorkBusiness;
import retrofit2.Call;
import retrofit2.Response;

public class FunctionActivity extends AppCompatActivity {
    //定义控件
    private Button get_ID; //获取ID
    private EditText equipment_ID; //设备ID

    private Button get_status; //获取在线状态
    private TextView status; //在线状态

    private Button get_temperature; //温度
    private TextView now_temperature; //当前温度

    private Button get_humidity; //湿度
    private TextView show_humidity; //当前湿度

    private Button get_illuminance; //光照
    private TextView show_illuminance;//当前光照

    private Button history_number; //历史记录
    private Button diagram; //图形

    private Button fan_open; //开风扇
    private Button fan_close;
    private Button fan_open2;
    private Button fan_close2;

    private Button electrical_foreward; //电机正转
    private Button electrical_inversion;//电机反转
    private Button electrical_close;
    private Button light_open; //开灯
    private Button light_close;

    private Button automatic_open; //自动模式开
    private Button automatic_close; //自动模式关
    private boolean isAuto = false; //自动模式标志位

    private EditText min_T; //最低温度
    private EditText max_T; //最高温度

    private EditText min_L;//最低光照
    private EditText max_L; //最高光照

    private double temp;
    private NetWorkBusiness netWorkBusiness;
    private String accessToken;
    private String ID = "310656"; //新大陆连接的设备ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_function);



        /**进行初始化*/
        Init();
        /**确定设备ID*/
        get_ID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ID = equipment_ID.getText().toString();
            }
        });
        /**获取设备在线状态*/
        get_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetStatus();
            }
        });
        /**获取温度*/
        get_temperature.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetTemperature();
            }
        });
        /**获取湿度*/
        get_humidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetHumidity();
            }
        });
        /**获取光照*/
        get_illuminance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetIlluminance();
            }
        });

        /**开关风扇1*/
        fan_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control(ID,"Fan",1);
            }
        });
        fan_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control(ID,"Fan",0);
            }
        });
        fan_open2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { control(ID,"Fan2",1); }
        });
        fan_close2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { control(ID,"Fan2",0); }
        });

        /**电机正转*/
        electrical_foreward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control(ID,"nl_steeringengine",1);
            }
        });
        electrical_inversion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control(ID,"nl_steeringengine",-1);
            }
        });
        electrical_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control(ID,"electrical",0);
            }
        });
        /**开关灯*/
        light_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control(ID,"Light_m",1);
            }
        });
        light_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control(ID,"Light_m",0);
            }
        });
        /**历史记录*/
        history_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(FunctionActivity.this,HistoryActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("accessToken",accessToken);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        /**自动模式开*/
        automatic_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                control(ID,"Automatic",1);
                isAuto = true;
                //线程
                Thread1 th = new Thread1();
                new Thread(th).start();
            }
        });

        /**自动模式关**/
        automatic_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAuto = false;
                control(ID,"Automatic",0);
            }
        });
    }//onCreate的

         /**初始化*/
        private void Init(){
            /** 绑定控件*/
             get_ID = findViewById(R.id.get_id); //确认id按钮
             get_status = findViewById(R.id.get_status); //获取状态按钮
             equipment_ID = findViewById(R.id.show_id); //输入的设备id
             status = findViewById(R.id.show_status); //获取的设备状态
             get_temperature = findViewById(R.id.get_temperature);
             now_temperature = findViewById(R.id.show_temperature);
             get_humidity = findViewById(R.id.get_humidity);
             show_humidity = findViewById(R.id.show_humidity);
             get_illuminance = findViewById(R.id.get_illuminance);
             show_illuminance = findViewById(R.id.show_illuminance);

             fan_open = findViewById(R.id.fan_open); //开风扇
             fan_close = findViewById(R.id.fan_close);//关风扇
             fan_open2= findViewById(R.id.fan_open2);
             fan_close2 = findViewById(R.id.fan_close2);

             electrical_foreward = findViewById(R.id.electrical_foreward);//电机正转
             electrical_inversion = findViewById(R.id.electrical_inversion);
             electrical_close = findViewById(R.id.electrical_close);
             light_open = findViewById(R.id.light_open);
             light_close = findViewById(R.id.light_close);
             history_number = findViewById(R.id.history_number);
             diagram = findViewById(R.id.diagram);

             automatic_open = findViewById(R.id.automatic_open);
             automatic_close = findViewById(R.id.automatic_close);

             min_T = findViewById(R.id.Min_T);
             max_T = findViewById(R.id.Max_T);
             min_L = findViewById(R.id.Min_T);
             max_L = findViewById(R.id.Max_L);


             /** 登录*/
             Bundle bundle = getIntent().getExtras();
             accessToken = bundle.getString("accessToken");
             Log.d("FunctionActivity","accessToken:"+ accessToken);
             netWorkBusiness = new NetWorkBusiness(accessToken,"http://api.nlecloud.com:80/");
        }
        /**
         * 控制设备开关
         * */
        public void control(String id,String apiTag,Object value){
           netWorkBusiness.control(id, apiTag, value, new NCallBack<BaseResponseEntity>(getApplicationContext()) {
               @Override
               public void onResponse(Call<BaseResponseEntity> call, Response<BaseResponseEntity> response) { }

               @Override
               public void onFailure(Call<BaseResponseEntity> call, Throwable t) {
                   Toast.makeText(FunctionActivity.this,"有错误",Toast.LENGTH_LONG).show();
               }
               @Override
               protected void onResponse(BaseResponseEntity response) {
                   if (response == null){
                       Toast.makeText(FunctionActivity.this,"请求内容为空",Toast.LENGTH_LONG).show();
                   }
               }
           });
        }

             /**
              * 获取设备在线信息
              * */
            private void GetStatus(){
                netWorkBusiness.getBatchOnLine(ID, new NCallBack<BaseResponseEntity<List<DeviceState>>>() {
                    @Override
                    protected void onResponse(BaseResponseEntity<List<DeviceState>> response) {

                    }

                    @Override
                    public void onFailure(Call<BaseResponseEntity<List<DeviceState>>> call, Throwable t) {
                        Toast.makeText(FunctionActivity.this,"状态获取失败"+t.getMessage(),Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onResponse(Call<BaseResponseEntity<List<DeviceState>>> call, Response<BaseResponseEntity<List<DeviceState>>> response) {
                        BaseResponseEntity baseResponseEntity = response.body();
                        if (baseResponseEntity != null){
                            boolean value = false;
                            final Gson gson = new Gson();
                            try {
                                JSONObject jsonObject = null;
                                String msg = gson.toJson(baseResponseEntity);
                                jsonObject = new JSONObject(msg);   //解析数据.
                                JSONArray resultObj = (JSONArray) jsonObject.get("ResultObj");
                                value = resultObj.getJSONObject(0).getBoolean("IsOnline");
                                if (value){
                                    status.setText("在线");
                                }else{
                                    status.setText("离线");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
             /**
              * 获取温度
              * */
            private void GetTemperature(){
                netWorkBusiness.getSensor(ID, "Temperature", new NCallBack<BaseResponseEntity<SensorInfo>>() {
                    @Override
                    public void onResponse(Call<BaseResponseEntity<SensorInfo>> call, Response<BaseResponseEntity<SensorInfo>> response) {
                        BaseResponseEntity baseResponseEntity = response.body();
                        if (baseResponseEntity != null){
                            //json解析
                            final Gson gson = new Gson();
                            JSONObject jsonObject = null;
                            String msg = gson.toJson(baseResponseEntity);
                            try {
                                jsonObject = new JSONObject(msg);
                                JSONObject resultObj = null;
                                resultObj = (JSONObject) jsonObject.get("ResultObj");
                                String aaa = resultObj.getString("Value");
                                temp = Double.valueOf(aaa).intValue();
                                now_temperature.setText(temp + "℃");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    protected void onResponse(BaseResponseEntity<SensorInfo> response) {}

                    @Override
                    public void onFailure(Call<BaseResponseEntity<SensorInfo>> call, Throwable t) {
                        Toast.makeText(FunctionActivity.this,"温度获取失败"+t.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

          }
             /**
              * 获取湿度
              * */
            private void GetHumidity(){
                netWorkBusiness.getSensor(ID, "humidity", new NCallBack<BaseResponseEntity<SensorInfo>>() {
                    @Override
                    public void onResponse(Call<BaseResponseEntity<SensorInfo>> call, Response<BaseResponseEntity<SensorInfo>> response) {
                        BaseResponseEntity baseResponseEntity = response.body();
                        if (baseResponseEntity != null){
                            //json解析
                            final Gson gson = new Gson();
                            JSONObject jsonObject = null;
                            String msg = gson.toJson(baseResponseEntity);
                            try {
                                jsonObject = new JSONObject(msg);
                                JSONObject resultObj = null;
                                resultObj = (JSONObject) jsonObject.get("ResultObj");
                                String aaa = resultObj.getString("Value");
                                temp = Double.valueOf(aaa).intValue();
                                show_humidity.setText(temp + "%RH");
                            } catch (JSONException e) {
                                 e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    protected void onResponse(BaseResponseEntity<SensorInfo> response) {}

                    @Override
                    public void onFailure(Call<BaseResponseEntity<SensorInfo>> call, Throwable t) {
                        Toast.makeText(FunctionActivity.this,"湿度获取失败"+t.getMessage(),Toast.LENGTH_LONG).show();
                     }
                 });

             }
             /**
             * 获取光照
             * */
            private void GetIlluminance(){
                netWorkBusiness.getSensor(ID, "light", new NCallBack<BaseResponseEntity<SensorInfo>>() {
                    @Override
                    public void onResponse(Call<BaseResponseEntity<SensorInfo>> call, Response<BaseResponseEntity<SensorInfo>> response) {
                        BaseResponseEntity baseResponseEntity = response.body();
                        if (baseResponseEntity != null){
                            //json解析
                            final Gson gson = new Gson();
                            JSONObject jsonObject = null;
                            String msg = gson.toJson(baseResponseEntity);
                            try {
                                jsonObject = new JSONObject(msg);
                                JSONObject resultObj = null;
                                resultObj = (JSONObject) jsonObject.get("ResultObj");
                                String aaa = resultObj.getString("Value");
                                temp = Double.valueOf(aaa).intValue();
                                show_illuminance.setText(temp + "Lux");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    protected void onResponse(BaseResponseEntity<SensorInfo> response) {}

                    @Override
                    public void onFailure(Call<BaseResponseEntity<SensorInfo>> call, Throwable t) {
                        Toast.makeText(FunctionActivity.this,"光照获取失败"+t.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
            /**
             * 设备开启自动模式
             * */
            class Thread1 implements Runnable{
                @Override
                public void run() {
                    while (true){
                        GetTemperature();
                        int currentTemp = (int) temp; //获取当前温度
                        int minT = Double.valueOf(min_T.getText().toString()).intValue();
                        int maxT = Double.valueOf(max_T.getText().toString()).intValue();

                        GetIlluminance();
                        int Temp2 = (int)temp;
                        int minL = Double.valueOf(min_T.getText().toString()).intValue();
                        int maxL = Double.valueOf(max_L.getText().toString()).intValue();

                        if (currentTemp > maxT && isAuto){
                            control(ID,"Fan",1);
                            control(ID,"Fan2",1);
                        }else if(currentTemp > minT && currentTemp <maxT&&isAuto){
                            control(ID,"Fan",1);
                            control(ID,"Fan2",0);
                        }else if (currentTemp < minT&&isAuto){
                            control(ID,"Fan",0);
                            control(ID,"Fan2",0);
                        }

                        if (Temp2 > maxL && isAuto){
                            control(ID,"nl_steeringengine",-1);
                            control(ID,"Light_m",0);
                        }else if( Temp2 > minL && isAuto){
                            control(ID,"nl_steeringengine",0);
                            control(ID,"Light_m",0);
                        }else if(Temp2 < minL && isAuto){
                            control(ID,"nl_steeringengine",1);
                            control(ID,"Light_m",1);
                        }
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

}//class文件的