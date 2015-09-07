package com.example.lenovo.livingwhere.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.lenovo.livingwhere.net.GsonRequest;
import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.entity.RentHistoryObj;
import com.example.lenovo.livingwhere.adapter.MyRentExpandableAdapter;
import com.example.lenovo.livingwhere.util.URI;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我出租的房子订单页面
 */

public class MyRentNoteActivity extends Activity {
    ExpandableListView listview;
    List<String> typeList;//第一级数据
    List<List<RentHistoryObj>> childData;//第二级数据们~
    MyRentExpandableAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_history);
        initView();
    }

    public void initView(){
        listview = (ExpandableListView)findViewById(R.id.my_order_history_listview);
        typeList = new ArrayList<String>();
        typeList.add("用户申请预约");
        typeList.add("房主已接受预约");
        typeList.add("用户确认完成");
        childData = new ArrayList<List<RentHistoryObj>>();
        List<RentHistoryObj> requestOrder = new ArrayList<RentHistoryObj>();
        List<RentHistoryObj> hostAgree = new ArrayList<RentHistoryObj>();
        List<RentHistoryObj> compelete = new ArrayList<RentHistoryObj>();
        childData.add(requestOrder);
        childData.add(hostAgree);
        childData.add(compelete);

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
                map.put("uid", String.valueOf(MainActivity.userObj.getUid()));
                map.put("kind",String.valueOf(0));
                return map;
            }
        };
        MainActivity.mQueue.add(myOrderHistoryGsonRequest);
    }
}
