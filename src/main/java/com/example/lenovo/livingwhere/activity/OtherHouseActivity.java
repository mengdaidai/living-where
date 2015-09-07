package com.example.lenovo.livingwhere.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import com.example.lenovo.livingwhere.net.GsonRequest;
import com.example.lenovo.livingwhere.entity.Houses;
import com.example.lenovo.livingwhere.util.URI;
import com.example.lenovo.livingwhere.view.OnRefreshListener;
import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.view.RefreshListView;
import com.example.lenovo.livingwhere.adapter.HouseDisplayAdapter;
import com.example.lenovo.livingwhere.adapter.MyHouseAdapter;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 用于显示我的房子或某房东其他房子
 */

public class OtherHouseActivity extends AppCompatActivity {
    private RefreshListView mListView;
    ArrayList<Houses> mListData;
    HouseDisplayAdapter mAdapter = null;//查看房东其他房子的adapter
    MyHouseAdapter adapter = null;//房东查看自己房子的adapter
    Intent intent = null;
    int type;//0为查看房东其他房子，1为房东查看自己的房子
    int position;//当前查看的item再listview中的位置


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getIntent();
        type = intent.getIntExtra("type",0);
        if(type == 0){//查看房东其他房子
            setContentView(R.layout.activity_other_house);
            mListView = (RefreshListView)findViewById(R.id.other_house_listview);
            //Volley框架获取数据
            GsonRequest<ArrayList<Houses>> initGsonRequest = new GsonRequest<ArrayList<Houses>>(Request.Method.POST,
                    URI.GetOtherHousesAddr, new TypeToken<ArrayList<Houses>>(){}.getType(),
                    new Response.Listener<ArrayList<Houses>>() {
                        @Override
                        public void onResponse(ArrayList<Houses> houses) {
                            mListData = new ArrayList<Houses>(houses);
                            System.out.println("mlistData  "+mListData);
                            mAdapter = new HouseDisplayAdapter(OtherHouseActivity.this, mListData);
                            mListView.setAdapter(mAdapter);
                            System.out.println("setListAdapter");
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
                    map.put("hid", String.valueOf(intent.getIntExtra("hid", 0)));
                    System.out.println("hid!!!!!!!!"+intent.getIntExtra("hid",0));
                    return map;
                }
            };
            MainActivity.mQueue.add(initGsonRequest);


            mListView.setOnRefreshListener(new OnRefreshListener() {

                @Override
                public void onDownPullRefresh() {
                    //下拉刷新，利用Volley框架请求数据并更新当前数据
                    GsonRequest<ArrayList<Houses>> pullDownGsonRequest = new GsonRequest<ArrayList<Houses>>(Request.Method.POST,
                            URI.GetOtherHousesAddr, new TypeToken<ArrayList<Houses>>() {
                    }.getType(),
                            new Response.Listener<ArrayList<Houses>>() {
                                @Override
                                public void onResponse(ArrayList<Houses> houses) {
                                    mAdapter.houseInfo = houses;
                                    mAdapter.notifyDataSetChanged();
                                    mListView.hideHeaderView();
                                    System.out.println("notifyDataSetChanged");
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
                            map.put("hid", String.valueOf(intent.getIntExtra("hid", 0)));
                            return map;
                        }
                    };
                    MainActivity.mQueue.add(pullDownGsonRequest);

                }

                @Override
                public void onLoadingMore() {
                    mListView.hideFooterView();
                }
            });
        }else{//房东查看自己的房子
            if(MainActivity.userObj.getType() == 0){//该用户不是房东，需先成为房主
                setContentView(R.layout.activity_please_be_host);
                Button toBeHostButton = (Button)findViewById(R.id.please_be_host_button);
                toBeHostButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringRequest toBeHostRequest = new StringRequest(Request.Method.POST, URI.ToBeOwnerAddr,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String s) {
                                        Toast.makeText(OtherHouseActivity.this, s, Toast.LENGTH_SHORT).show();
                                    }
                                }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                            }
                        }){
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                               Map<String,String> map = new HashMap<String,String>();
                                map.put("uid",String.valueOf(MainActivity.userObj.getUid()));
                                System.out.println("uid!!!!!!!!!!!"+MainActivity.userObj.getUid());
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
                    }
                });
            }

            else{//该用户确实为房主
                setContentView(R.layout.activity_other_house);
                mListView = (RefreshListView)findViewById(R.id.other_house_listview);
                //Volley框架获取数据
                GsonRequest<ArrayList<Houses>> initGsonRequest = new GsonRequest<ArrayList<Houses>>(Request.Method.POST,
                        URI.GetMyReleasedHousesAddr, new TypeToken<ArrayList<Houses>>(){}.getType(),
                        new Response.Listener<ArrayList<Houses>>() {
                            @Override
                            public void onResponse(ArrayList<Houses> houses) {
                                mListData = new ArrayList<Houses>(houses);
                                System.out.println("mlistData  "+mListData);
                                adapter = new MyHouseAdapter(OtherHouseActivity.this, mListData);
                                mListView.setAdapter(adapter);
                                System.out.println("setListAdapter");
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
                        System.out.println("uid!!!!!!!!!!" + MainActivity.userObj.getUid());
                        return map;
                    }
                };
                MainActivity.mQueue.add(initGsonRequest);


                mListView.setOnRefreshListener(new OnRefreshListener() {

                    @Override
                    public void onDownPullRefresh() {
                        //下拉刷新，利用Volley框架请求数据并更新当前数据
                        GsonRequest<ArrayList<Houses>> pullDownGsonRequest = new GsonRequest<ArrayList<Houses>>(Request.Method.POST,
                                URI.GetMyReleasedHousesAddr, new TypeToken<ArrayList<Houses>>() {
                        }.getType(),
                                new Response.Listener<ArrayList<Houses>>() {
                                    @Override
                                    public void onResponse(ArrayList<Houses> houses) {
                                        adapter.houseInfo = houses;
                                        adapter.notifyDataSetChanged();
                                        mListView.hideHeaderView();
                                        System.out.println("notifyDataSetChanged");
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
                                map.put("uid", String.valueOf(MainActivity.userObj.getUid()));

                                return map;
                            }
                        };
                        MainActivity.mQueue.add(pullDownGsonRequest);

                    }

                    @Override
                    public void onLoadingMore() {
                        mListView.hideFooterView();
                    }
                });
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case 1://修改房间信息后返回
                if(data!=null){
                    Houses house = (Houses)data.getSerializableExtra("house");
                    mListData.set(position,house);
                    adapter.notifyDataSetChanged();
                }
        }
    }
}
