package com.example.lenovo.livingwhere.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.util.MyApplication;
import com.example.lenovo.livingwhere.util.URI;

import junit.framework.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StringRequest request = new StringRequest(Request.Method.POST, URI.StarScaleAddr, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (s.equals("操作成功")) {
                            Toast.makeText(TestActivity.this, s, Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(TestActivity.this, s, Toast.LENGTH_LONG).show();
                            System.out.println(s);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }) {
                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        try {
                            String mString =
                                    new String(response.data, "utf-8");

                            return Response.success(mString,
                                    HttpHeaderParser.parseCacheHeaders(response));
                        } catch (UnsupportedEncodingException e) {
                            return Response.error(new ParseError(e));
                        }
                    }
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        //map.put("uid", String.valueOf(MyApplication.user.getUid()));
                        map.put("uid", String.valueOf(13));
                        //map.put("stars", String.valueOf(bar.getRating()));
                        map.put("stars", String.valueOf(3.5));
                        //map.put("hid",String.valueOf(hid));
                        map.put("hid",String.valueOf(42));
                        return map;
                    }
                };
                MyApplication.mQueue.add(request);
            }
        });
    }




}
