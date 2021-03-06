package com.example.lenovo.livingwhere.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.lenovo.livingwhere.entity.CommentObj;
import com.example.lenovo.livingwhere.net.GsonRequest;
import com.example.lenovo.livingwhere.util.MyApplication;
import com.example.lenovo.livingwhere.util.URI;
import com.example.lenovo.livingwhere.view.OnRefreshListener;
import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.view.RefreshListView;
import com.example.lenovo.livingwhere.adapter.HouseCommentAdapter;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于展示某房子所有评论的页面
 */

public class HouseCommentActivity extends AppCompatActivity implements View.OnClickListener{
    RefreshListView mListview ;
    List<CommentObj> mListData;
    HouseCommentAdapter mAdapter;
    int hid;
    ImageButton backButton;
    TextView title;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_comment);
        initView();
        initEvent();
    }


    public void initView(){
        backButton = (ImageButton)findViewById(R.id.toolbar_back_title_back);
        title = (TextView)findViewById(R.id.toolbar_back_title_text);
        title.setText("房屋评论");
        Intent intent = getIntent();
        hid = intent.getIntExtra("hid",0);
        mListview = (RefreshListView)findViewById(R.id.house_comment_listview);
        GsonRequest<List<CommentObj>> request = new GsonRequest<List<CommentObj>>(Request.Method.POST, URI.GetCommentsAddr, new TypeToken<List<CommentObj>>() {
        }.getType(),
                new Response.Listener<List<CommentObj>>() {
                    @Override
                    public void onResponse(List<CommentObj> commentObjs) {
                        mListData = commentObjs;
                        mAdapter = new HouseCommentAdapter(HouseCommentActivity.this,mListData);
                        mListview.setAdapter(mAdapter);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String,String> map = new HashMap<String,String>();
                map.put("index",String.valueOf(0));
                map.put("hid",String.valueOf(hid));
                return map;
            }
        };
        MyApplication.mQueue.add(request);
    }

    public void initEvent(){
        backButton.setOnClickListener(this);
        mListview.setOnRefreshListener(new OnRefreshListener() {
            //下拉刷新
            @Override
            public void onDownPullRefresh() {
                GsonRequest<List<CommentObj>> request = new GsonRequest<List<CommentObj>>(Request.Method.POST, URI.GetCommentsAddr, new TypeToken<List<CommentObj>>() {
                }.getType(),
                        new Response.Listener<List<CommentObj>>() {
                            @Override
                            public void onResponse(List<CommentObj> commentObjs) {
                                mListData = commentObjs;
                                System.out.println();
                                mAdapter.notifyDataSetChanged();
                                mListview.hideHeaderView();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("index", String.valueOf(0));
                        map.put("hid", String.valueOf(hid));
                        return map;
                    }
                };
                MyApplication.mQueue.add(request);
            }
            //上拉加载
            @Override
            public void onLoadingMore() {
                GsonRequest<List<CommentObj>> request = new GsonRequest<List<CommentObj>>(Request.Method.POST, URI.GetCommentsAddr, new TypeToken<List<CommentObj>>() {
                }.getType(),
                        new Response.Listener<List<CommentObj>>() {
                            @Override
                            public void onResponse(List<CommentObj> commentObjs) {
                                mListData.addAll(commentObjs);
                                mAdapter.notifyDataSetChanged();
                                mListview.hideFooterView();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        int index = mListview.getCount()-2;
                        map.put("index", String.valueOf(index));
                        map.put("hid", String.valueOf(hid));
                        return map;
                    }
                };
                MyApplication.mQueue.add(request);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_back_title_back:
                finish();
                break;
        }
    }
}
