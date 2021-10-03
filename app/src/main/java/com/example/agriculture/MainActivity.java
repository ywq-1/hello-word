package com.example.agriculture;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.com.newland.nle_sdk.requestEntity.SignIn;
import cn.com.newland.nle_sdk.responseEntity.User;
import cn.com.newland.nle_sdk.responseEntity.base.BaseResponseEntity;
import cn.com.newland.nle_sdk.util.NCallBack;
import cn.com.newland.nle_sdk.util.NetWorkBusiness;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private Button mButLogin;
    private EditText mEditUsername;
    private EditText mEditPassword;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    String str_Username;
    String str_Password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化SharedPreference
        sp = getSharedPreferences("nlecloud",MODE_PRIVATE);
        editor = sp.edit();
        //找到登录控件
        mButLogin = findViewById(R.id.login);
        mEditUsername = findViewById(R.id.Edit_username);
        mEditPassword = findViewById(R.id.Edit_password);

        if (sp.getString("username", str_Username) != null && sp.getString("password", str_Password) != null) {
            if (!sp.getString("username", str_Username).equals("") && !sp.getString("password", str_Password).equals("")) {
                mEditUsername.setText(sp.getString("username", "1"));
                mEditPassword.setText(sp.getString("password", "2"));
            }
        }

        //设置监听，当登录按钮按下后
        mButLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

    }//onCreate的}
        private void login(){
            String Address = "http://api.nlecloud.com:80/";
            str_Username = mEditUsername.getText().toString();
            str_Password = mEditPassword.getText().toString();

            if (str_Username.equals("") || str_Password.equals("")) {
                Toast.makeText(this, "用户名或密码不为空", Toast.LENGTH_SHORT).show();
                return;
            }

            NetWorkBusiness netWorkBusiness = new NetWorkBusiness("",Address);
            netWorkBusiness.signIn(new SignIn(str_Username, str_Password), new NCallBack<BaseResponseEntity<User>>(getApplicationContext()) {
                @Override
                protected void onResponse(BaseResponseEntity<User> response) {
                    if (response != null){
                        if (response.getStatus() == 0){
                            //保存正确的用户名和密码
                            editor.putString("username", str_Username);
                            editor.putString("password", str_Password);
                            editor.apply();

                            String accessToken = response.getResultObj().getAccessToken();
                            Intent intent = new Intent(MainActivity.this,FunctionActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("accessToken",accessToken);
                            intent.putExtras(bundle);

                            Log.d("FunctionActivity","accessToken:"+ accessToken);
                            startActivity(intent);
                            finish();
                        }
                    }else{
                        Toast.makeText(MainActivity.this,"登陆失败",Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<BaseResponseEntity<User>> call, Throwable t) {
                    Toast.makeText(MainActivity.this,"登陆失败",Toast.LENGTH_LONG).show();
                }

            });

        }
}//class的}