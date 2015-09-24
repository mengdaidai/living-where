package com.example.lenovo.livingwhere.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.lenovo.livingwhere.entity.BookHistoryObj;
import com.example.lenovo.livingwhere.entity.Houses;
import com.example.lenovo.livingwhere.net.GsonRequest;
import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.adapter.MyOrderExpandableAdapter;
import com.example.lenovo.livingwhere.util.MyApplication;
import com.example.lenovo.livingwhere.util.URI;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 我的预约历史
 */
public class MyOrderHistoryActivity extends AppCompatActivity implements View.OnClickListener{
    ExpandableListView listview;



    List<String> typeList;//第一级数据
    List<List<BookHistoryObj>> childData;//第二级数据们~
    MyOrderExpandableAdapter adapter;
    ImageButton backButton;
    TextView title;


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_back_title_back:
                finish();
                break;

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_order_history);
        initView();
        initEvent();
    }

    public void initView(){
        title = (TextView)findViewById(R.id.toolbar_back_title_text);
        title.setText("预约历史");
        backButton = (ImageButton)findViewById(R.id.toolbar_back_title_back);
        listview = (ExpandableListView)findViewById(R.id.my_order_history_listview);

        typeList = new ArrayList<String>();
        typeList.add("请求预约中");
        typeList.add("房主已接受");
        typeList.add("确认完成");
        childData = new ArrayList<List<BookHistoryObj>>();
        List<BookHistoryObj> requestOrder = new ArrayList<BookHistoryObj>();
        List<BookHistoryObj> hostAgree = new ArrayList<BookHistoryObj>();
        List<BookHistoryObj> compelete = new ArrayList<BookHistoryObj>();
        childData.add(requestOrder);
        childData.add(hostAgree);
        childData.add(compelete);

        GsonRequest<ArrayList<BookHistoryObj>> myOrderHistoryGsonRequest = new GsonRequest<ArrayList<BookHistoryObj>>(Request.Method.POST,
                URI.GetMyBookHistoryAddr, new TypeToken<ArrayList<BookHistoryObj>>(){}.getType(),
                new Response.Listener<ArrayList<BookHistoryObj>>() {
                    @Override
                    public void onResponse(ArrayList<BookHistoryObj> bookHistory) {
                        for(BookHistoryObj obj:bookHistory){
                            childData.get(obj.getState()).add(obj);
                        }
                        adapter = new MyOrderExpandableAdapter(MyOrderHistoryActivity.this,typeList,childData);
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
                if(MyApplication.user.getType() == 2)
                    map.put("kind",String.valueOf(0));
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
                BookHistoryObj obj = childData.get(groupPosition).get(childPosition);
                Houses house = new Houses(obj.getHid(),obj.getContactPhone(),obj.getPrice(),obj.getSize(),obj.getDescription(),obj.getPictures(),obj.getAddress(),obj.getLongitude(),obj.getLatitude(),obj.getAmount(),obj.getStar(),0,0);
                Intent intent = new Intent(MyOrderHistoryActivity.this,HouseDetailsActivity.class);
                intent.putExtra("house",(Serializable)house);
                startActivity(intent);
                return true;
            }
        });
    }


}
