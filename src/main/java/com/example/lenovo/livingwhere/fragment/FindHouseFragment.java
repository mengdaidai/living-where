package com.example.lenovo.livingwhere.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.lenovo.livingwhere.entity.DistanceSort;
import com.example.lenovo.livingwhere.net.GsonRequest;
import com.example.lenovo.livingwhere.activity.MainActivity;
import com.example.lenovo.livingwhere.view.OnRefreshListener;
import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.view.RefreshListView;
import com.example.lenovo.livingwhere.adapter.RecommendAdapter;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  展示条件筛选后的房子
 */


public class FindHouseFragment extends Fragment {
    List<DistanceSort> distanceList;
    RefreshListView listview;
    Button inButton,outButton,submitButton;
    List<DistanceSort> mListData;
    RecommendAdapter mAdapter;
    String price,book_start,book_end;
    double longitude,latitude;
    public List<DistanceSort> getDistanceList() {
        return distanceList;
    }
    Spinner spinner;//用于价格筛选

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data!=null){
            mListData = new ArrayList<DistanceSort>((List<DistanceSort>)data.getSerializableExtra("distance"));
            longitude = data.getDoubleExtra("longitude",0);
            latitude = data.getDoubleExtra("latitude", 0);
            price = data.getStringExtra("price");
            book_start = data.getStringExtra("book_start");
            book_end = data.getStringExtra("book_end");
            mAdapter = new RecommendAdapter(getActivity(),mListData);
            listview.setAdapter(mAdapter);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_house, container, false);

        inButton = (Button)view.findViewById(R.id.find_house_in);
        inButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        outButton = (Button)view.findViewById(R.id.find_house_leave);
        outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        submitButton = (Button)view.findViewById(R.id.find_house_submit);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        listview = (RefreshListView)view.findViewById(R.id.find_house_listview);
        listview.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onDownPullRefresh() {
                //下拉刷新，利用Volley框架请求数据并更新当前数据
                GsonRequest<ArrayList<DistanceSort>> pullDownGsonRequest = new GsonRequest<ArrayList<DistanceSort>>(Request.Method.POST,
                        "http://115.28.85.146:8080/Zhunaer/action/msg_matchHouses", new TypeToken<ArrayList<DistanceSort>>(){}.getType(),
                        new Response.Listener<ArrayList<DistanceSort>>() {
                            @Override
                            public void onResponse(ArrayList<DistanceSort> houses) {
                                mListData = houses;
                                mAdapter.notifyDataSetChanged();
                                listview.hideHeaderView();
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
                        map.put("latitude", String.valueOf(latitude));
                        map.put("longitude", String.valueOf(longitude));
                        map.put("price",price);
                        map.put("book_start",book_start);
                        map.put("book_end",book_end);
                        map.put("index", String.valueOf(0));
                        return map;
                    }
                };
                MainActivity.mQueue.add(pullDownGsonRequest);


            }

            @Override
            public void onLoadingMore() {
//上拉加载，利用Volley框架请求数据并更新当前数据
                GsonRequest<ArrayList<DistanceSort>> loadMoreGsonRequest = new GsonRequest<ArrayList<DistanceSort>>(Request.Method.POST,
                        "http://115.28.85.146:8080/Zhunaer/action/msg_matchHouses", new TypeToken<ArrayList<DistanceSort>>(){}.getType(),
                        new Response.Listener<ArrayList<DistanceSort>>() {
                            @Override
                            public void onResponse(ArrayList<DistanceSort> houses) {
                                mListData.addAll(houses);
                                mAdapter.notifyDataSetChanged();
                                listview.hideFooterView();
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
                        map.put("latitude", String.valueOf(latitude));
                        map.put("longitude", String.valueOf(longitude));
                        map.put("price",price);
                        map.put("book_start",book_start);
                        map.put("book_end",book_end);
                        map.put("index", String.valueOf(listview.getCount()-2));
                        return map;
                    }
                };
                MainActivity.mQueue.add(loadMoreGsonRequest);
            }
        });

        return view;
    }


}
