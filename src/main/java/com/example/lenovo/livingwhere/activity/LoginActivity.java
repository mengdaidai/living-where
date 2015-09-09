package com.example.lenovo.livingwhere.activity;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.entity.CurrentUserObj;
import com.example.lenovo.livingwhere.mob.RegisterPage;
import com.example.lenovo.livingwhere.net.GsonRequest;
import com.example.lenovo.livingwhere.util.MyApplication;
import com.example.lenovo.livingwhere.util.URI;
import com.example.lenovo.livingwhere.view.DialogUtil;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class LoginActivity extends Activity implements View.OnClickListener {
    private TextView tv_register;
    // // 填写从短信SDK应用后台注册得到的APPKEY
    // // 填写从短信SDK应用后台注册得到的APPKEY
    private static String APPKEY = "9c481e11e1b3";

    // // 填写从短信SDK应用后台注册得到的APPSECRET
    private static String APPSECRET = "5b65d6c1069a01fcbd8864e7ef53ff4a";
    private boolean ready;
    private Button bt_login;
    private EditText username;
    private EditText password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initSDK();//短信
        initView();
    }

    void initView() {
        tv_register = (TextView) this.findViewById(R.id.register);
        tv_register.setText(Html.fromHtml("<u>" + "没有账号？点此注册!" + "</u>"));
        bt_login = (Button) findViewById(R.id.bt_login);
        username = (EditText) findViewById(R.id.et_username);
        password = (EditText) findViewById(R.id.et_passw);
        tv_register.setOnClickListener(this);
        bt_login.setOnClickListener(this);

    }

    private void initSDK() {
        // 初始化SDK

        SMSSDK.initSDK(this, APPKEY, APPSECRET);
        final Handler handler = new Handler();
        EventHandler eventHandler = new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                handler.sendMessage(msg);
            }
        };
        // ?注册回调监听接口
        SMSSDK.registerEventHandler(eventHandler);
        ready = true;


    }

    protected void onDestroy() {
        if (ready) {
            SMSSDK.unregisterAllEventHandler();
        }
        super.onDestroy();
    }

    void tv_register() {
        RegisterPage registerPage = new RegisterPage();
        registerPage.setRegisterCallback(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    @SuppressWarnings("unchecked")
                    HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                    String country = (String) phoneMap.get("country");
                    String phone = (String) phoneMap.get("phone");

                    // 提交用户信息
                    tv_registerUser(country, phone);
                }
            }
        });
        registerPage.show(this);
    }

    //短信	 提交用户信息
    private void tv_registerUser(String country, String phone) {
        Random rnd = new Random();
        int id = Math.abs(rnd.nextInt());
        String uid = String.valueOf(id);
        String nickName = "SmsSDK_User_" + uid;
        SMSSDK.submitUserInfo(uid, nickName, "", country, phone);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.bt_login) {
            //登陆
            if (TextUtils.isEmpty(username.getText()))
                Toast.makeText(this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            else if (TextUtils.isEmpty(password.getText()))
                Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
            else {
                //登陆
                startLogin(username.getText().toString().trim(), password.getText().toString().trim());
            }

        } else if (v.getId() == R.id.register) {
            tv_register();
        }


    }

    void startLogin(final String username, final String passw) {
        final Dialog dialog = DialogUtil.createLoadingDialog(this, "正在登陆");
        dialog.show();
        GsonRequest<CurrentUserObj> loadMoreGsonRequest = new GsonRequest<CurrentUserObj>(Request.Method.POST,
                URI.LoginAddr, new TypeToken<CurrentUserObj>() {
        }.getType(),
                new Response.Listener<CurrentUserObj>() {
                    @Override
                    public void onResponse(CurrentUserObj user) {
                        MyApplication.user = user;
                        Toast.makeText(LoginActivity.this,
                                user.getMessage(), Toast.LENGTH_SHORT)
                                .show();
                        if ("登陆成功".equals(user.getMessage())) {
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("phoneNum", username);
                map.put("password", passw);
                return map;
            }
        };
        MyApplication.mQueue.add(loadMoreGsonRequest);

    }
}
