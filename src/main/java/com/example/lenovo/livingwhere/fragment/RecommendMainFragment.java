package com.example.lenovo.livingwhere.fragment;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.lenovo.livingwhere.activity.HouseDetailsActivity;
import com.example.lenovo.livingwhere.entity.DistanceSort;
import com.example.lenovo.livingwhere.net.GsonRequest;
import com.example.lenovo.livingwhere.entity.Houses;
import com.example.lenovo.livingwhere.activity.MainActivity;
import com.example.lenovo.livingwhere.util.MyApplication;
import com.example.lenovo.livingwhere.view.OnFragmentListener;
import com.example.lenovo.livingwhere.view.OnRefreshListener;
import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.view.RefreshListView;
import com.example.lenovo.livingwhere.adapter.RecommendAdapter;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import com.example.lenovo.livingwhere.util.URI;


public class RecommendMainFragment extends ListFragment {

    public Map<String, String> map;
    public String tag = this.getClass().getSimpleName(); // tag 用于测试log用

    public Context context; // 存储上下文对象
    public Activity activity; // 存储上下文对象
    RecommendAdapter mAdapter = null;

    double mLatitude,mLongitude;

    /**
     * 请求url
     */
    private String url;

    /**
     * ListView
     */
    private RefreshListView mListView;
    /**
     * 集合数据
     */
    public ArrayList<DistanceSort> mListData;
    /**
     * 是否重新加载
     */
    private boolean isClear;

    private LayoutInflater mInflater;


    public RecommendMainFragment() {
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        context = getActivity();
        activity = getActivity();

        Log.i(tag, "onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_recommend_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(tag, "onActivityCreated");
        mListView = (RefreshListView) getListView();//(ListView) activity.findViewById(android.R.id.list);
        if (mListData != null && mListData.size() > 0) {
            return;
        }



    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Houses house = mListData.get(position-1).getHouse();
        Intent intent = new Intent(getActivity(), HouseDetailsActivity.class);
        intent.putExtra("house",(Serializable)house);
        startActivity(intent);
        System.out.println("提出网络请求，转到相应页面");
    }


    public void initListView(double latitude,double longitude){
        System.out.println("initListView");
        mLatitude = latitude;
        mLongitude = longitude;

        //Volley框架获取数据
        GsonRequest<ArrayList<DistanceSort>> initGsonRequest = new GsonRequest<ArrayList<DistanceSort>>(Request.Method.POST,
                URI.FindHouseNearByAddr, new TypeToken<ArrayList<DistanceSort>>(){}.getType(),
                new Response.Listener<ArrayList<DistanceSort>>() {
                    @Override
                    public void onResponse(ArrayList<DistanceSort> houses) {
                        mListData = new ArrayList<DistanceSort>(houses);
                        System.out.println("mlistData  "+mListData);
                        mAdapter = new RecommendAdapter(context, mListData);
                       // RecommendMainFragment.this.setListAdapter(mAdapter);
                        setListAdapter(mAdapter);
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
                map.put("latitude", String.valueOf(mLatitude));
                map.put("longitude", String.valueOf(mLongitude));
                map.put("index", String.valueOf(0));
                return map;
            }
        };
        MyApplication.mQueue.add(initGsonRequest);


        mListView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onDownPullRefresh() {
                //下拉刷新，利用Volley框架请求数据并更新当前数据
                GsonRequest<ArrayList<DistanceSort>> pullDownGsonRequest = new GsonRequest<ArrayList<DistanceSort>>(Request.Method.POST,
                        URI.FindHouseNearByAddr, new TypeToken<ArrayList<DistanceSort>>(){}.getType(),
                        new Response.Listener<ArrayList<DistanceSort>>() {
                            @Override
                            public void onResponse(ArrayList<DistanceSort> houses) {
                                mListData.removeAll(mListData);
                                mListData.addAll(houses);
                                mAdapter.notifyDataSetChanged();
                                mListView.hideHeaderView();
                                System.out.println("notifyDataSetChanged");
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
                        map.put("latitude", String.valueOf(mLatitude));
                        map.put("longitude", String.valueOf(mLongitude));
                        map.put("index", String.valueOf(0));
                        return map;
                    }
                };
                MyApplication.mQueue.add(pullDownGsonRequest);

                Log.i(tag,"下拉刷新");
            }

            @Override
            public void onLoadingMore() {
                //上拉加载，利用Volley框架请求数据并更新当前数据
                GsonRequest<ArrayList<DistanceSort>> loadMoreGsonRequest = new GsonRequest<ArrayList<DistanceSort>>(Request.Method.POST,
                        URI.FindHouseNearByAddr, new TypeToken<ArrayList<DistanceSort>>(){}.getType(),
                        new Response.Listener<ArrayList<DistanceSort>>() {
                            @Override
                            public void onResponse(ArrayList<DistanceSort> houses) {
                                mListData.addAll(houses);
                                mAdapter.notifyDataSetChanged();
                                mListView.hideFooterView();
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
                        map.put("latitude", String.valueOf(mLatitude));
                        map.put("longitude", String.valueOf(mLongitude));
                        map.put("index", String.valueOf(mListView.getCount()-2));
                        return map;
                    }
                };
                MyApplication.mQueue.add(loadMoreGsonRequest);
                Log.i(tag,"上拉加载");
            }
        });
    }

}
