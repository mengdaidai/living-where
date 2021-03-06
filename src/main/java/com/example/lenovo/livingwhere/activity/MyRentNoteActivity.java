package com.example.lenovo.livingwhere.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.example.lenovo.livingwhere.entity.BookHistoryObj;
import com.example.lenovo.livingwhere.entity.Houses;
import com.example.lenovo.livingwhere.net.GsonRequest;
import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.entity.RentHistoryObj;
import com.example.lenovo.livingwhere.adapter.MyRentExpandableAdapter;
import com.example.lenovo.livingwhere.util.MyApplication;
import com.example.lenovo.livingwhere.util.URI;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我出租的房子订单页面
 */

public class MyRentNoteActivity extends Activity implements View.OnClickListener{
    ExpandableListView listview;
    List<String> typeList;//第一级数据
    List<List<RentHistoryObj>> childData;//第二级数据们~
    MyRentExpandableAdapter adapter;
    ImageButton backButton;
    TextView title;


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_back_title_back:
                finish();
                break;
            case R.id.please_be_host_button:
                StringRequest toBeHostRequest = new StringRequest(Request.Method.POST, URI.ToBeOwnerAddr,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                Toast.makeText(MyRentNoteActivity.this, s, Toast.LENGTH_SHORT).show();
                                if(s.equals("操作成功"))
                                    MyApplication.user.setType(1);
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> map = new HashMap<String,String>();
                        map.put("uid", String.valueOf(MyApplication.user.getUid()));
                        return map;
                    }

                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        String mString;
                        try {
                            mString =
                                    new String(response.data, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            return Response.error(new ParseError(e));
                        }
                        return Response.success(mString,
                                HttpHeaderParser.parseCacheHeaders(response));
                    }
                };
                MyApplication.mQueue.add(toBeHostRequest);
                break;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(MyApplication.user.getType() == 0) {
            setContentView(R.layout.activity_please_be_host);
            Button toBeHostButt = (Button) findViewById(R.id.please_be_host_button);
            toBeHostButt.setOnClickListener(this);
            ImageButton backButton = (ImageButton)findViewById(R.id.toolbar_back_title_back);
            backButton.setOnClickListener(this);
        }
        else{
            setContentView(R.layout.activity_my_order_history);
            initView();
            initEvent();
        }

    }

    public void initView(){
        title = (TextView)findViewById(R.id.toolbar_back_title_text);
        title.setText("出租历史");
        backButton = (ImageButton)findViewById(R.id.toolbar_back_title_back);
        listview = (ExpandableListView)findViewById(R.id.my_order_history_listview);
        typeList = new ArrayList<String>();
        typeList.add("用户申请预约");
        typeList.add("房主已接受预约");
        childData = new ArrayList<List<RentHistoryObj>>();
        List<RentHistoryObj> requestOrder = new ArrayList<RentHistoryObj>();
        List<RentHistoryObj> hostAgree = new ArrayList<RentHistoryObj>();
        childData.add(requestOrder);
        childData.add(hostAgree);

        GsonRequest<ArrayList<RentHistoryObj>> myOrderHistoryGsonRequest = new GsonRequest<ArrayList<RentHistoryObj>>(Request.Method.POST,
                URI.GetMyBookHistoryAddr, new TypeToken<ArrayList<RentHistoryObj>>(){}.getType(),
                new Response.Listener<ArrayList<RentHistoryObj>>() {
                    @Override
                    public void onResponse(ArrayList<RentHistoryObj> bookHistory) {
                        for(RentHistoryObj obj:bookHistory){
                            childData.get(obj.getState()).add(obj);
                        }
                        adapter = new MyRentExpandableAdapter(MyRentNoteActivity.this,typeList,childData);
                        listview.setAdapter(adapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("TAG", error.getMessage(), error);
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("uid", String.valueOf(MyApplication.user.getUid()));
                if(MyApplication.user.getType() == 1)
                    map.put("kind",String.valueOf(1));
                return map;
            }
        };
        MyApplication.mQueue.add(myOrderHistoryGsonRequest);
    }

    public void initEvent(){
        backButton.setOnClickListener(this);
        listview.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                RentHistoryObj obj = childData.get(groupPosition).get(childPosition);
                Houses house = obj.getHouse();
                Intent intent = new Intent(MyRentNoteActivity.this,HouseDetailsActivity.class);
                intent.putExtra("house",(Serializable)house);
                startActivity(intent);
                return true;
            }
        });
    }
}
