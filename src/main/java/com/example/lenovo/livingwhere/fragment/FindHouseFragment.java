package com.example.lenovo.livingwhere.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.lenovo.livingwhere.activity.HouseDetailsActivity;
import com.example.lenovo.livingwhere.activity.OrderActivity;
import com.example.lenovo.livingwhere.activity.SelectionConditionActivity;
import com.example.lenovo.livingwhere.entity.DistanceSort;
import com.example.lenovo.livingwhere.entity.Houses;
import com.example.lenovo.livingwhere.net.GsonRequest;
import com.example.lenovo.livingwhere.activity.MainActivity;
import com.example.lenovo.livingwhere.util.MyApplication;
import com.example.lenovo.livingwhere.util.URI;
import com.example.lenovo.livingwhere.view.OnFragmentListener;
import com.example.lenovo.livingwhere.view.OnRefreshListener;
import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.view.RefreshListView;
import com.example.lenovo.livingwhere.adapter.RecommendAdapter;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  展示条件筛选后的房子
 */


public class FindHouseFragment extends Fragment implements View.OnClickListener,DatePickerDialog.OnDateSetListener{
    List<DistanceSort> distanceList;
    RefreshListView listview;
    Button inButton,outButton,submitButton;
    List<DistanceSort> mListData;
    RecommendAdapter mAdapter;//listview的适配器
    ArrayAdapter<String> adapter;//价格Spinner的适配器
    double longitude,latitude;
    public List<DistanceSort> getDistanceList() {
        return distanceList;
    }
    Spinner spinner;//用于价格筛选
    String[] priceString = {"200","500","1000","5000","10000"};
    int DatePickertype,rentType = 1;
    String inDate = "",outDate = "",price;







    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_find_house, container, false);

        inButton = (Button)view.findViewById(R.id.find_house_in);
        inButton.setOnClickListener(this);
        outButton = (Button)view.findViewById(R.id.find_house_leave);
        outButton.setOnClickListener(this);
        submitButton = (Button)view.findViewById(R.id.find_house_submit);
        submitButton.setOnClickListener(this);
        listview = (RefreshListView)view.findViewById(R.id.find_house_listview);
        listview.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onDownPullRefresh() {
                //下拉刷新，利用Volley框架请求数据并更新当前数据
                GsonRequest<ArrayList<DistanceSort>> pullDownGsonRequest = new GsonRequest<ArrayList<DistanceSort>>(Request.Method.POST,
                        URI.MatchHousesAddr, new TypeToken<ArrayList<DistanceSort>>() {
                }.getType(),
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
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("latitude", String.valueOf(latitude));
                        map.put("longitude", String.valueOf(longitude));
                        map.put("price", price);
                        map.put("book_start", inDate);
                        map.put("book_end", outDate);
                        map.put("index", String.valueOf(0));
                        return map;
                    }
                };
                MyApplication.mQueue.add(pullDownGsonRequest);


            }

            @Override
            public void onLoadingMore() {
                //上拉加载，利用Volley框架请求数据并更新当前数据
                GsonRequest<ArrayList<DistanceSort>> loadMoreGsonRequest = new GsonRequest<ArrayList<DistanceSort>>(Request.Method.POST,
                        URI.MatchHousesAddr, new TypeToken<ArrayList<DistanceSort>>() {
                }.getType(),
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
                }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> map = new HashMap<String, String>();
                        map.put("latitude", String.valueOf(latitude));
                        map.put("longitude", String.valueOf(longitude));
                        map.put("price", price);
                        map.put("book_start", inDate);
                        map.put("book_end", outDate);
                        map.put("index", String.valueOf(listview.getCount() - 2));
                        return map;
                    }
                };
                MyApplication.mQueue.add(loadMoreGsonRequest);
            }
        });
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Houses house = mListData.get(position-1).getHouse();
                Intent intent = new Intent(getActivity(), HouseDetailsActivity.class);
                intent.putExtra("house",(Serializable)house);
                startActivity(intent);
            }
        });
        spinner = (Spinner)view.findViewById(R.id.find_house_price);
        //将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<String>(getActivity(),android.R.layout.simple_spinner_item,priceString);

        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //将adapter 添加到spinner中
        spinner.setAdapter(adapter);

        //添加事件Spinner事件监听
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                price = priceString[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //设置默认值
        spinner.setVisibility(View.VISIBLE);

        return view;
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        //do some stuff for example write on log and update TextField on activity
        Log.w("DatePicker", "Date = " + year);
        if(DatePickertype == 0){
            inDate = String.valueOf(year);
            if(month<10) inDate+=("0"+String.valueOf(month));
            else inDate+=String.valueOf(month);
            if(day<10) inDate+=("0"+String.valueOf(day));
            else inDate+=String.valueOf(day);
            String text = String.valueOf(year)+"年"+String.valueOf(month)+"月"+String.valueOf(day)+"日";
            inButton.setText(text);
            Log.e("inDate",inDate);
        }
        else if(DatePickertype == 1){
            outDate = String.valueOf(year);
            if(month<10) outDate+=("0"+String.valueOf(month));
            else outDate+=String.valueOf(month);
            if(day<10) outDate+=("0"+String.valueOf(day));
            else outDate+=String.valueOf(day);
            outButton.setText(year+"年"+month+"月"+day + "日");
            Log.e("outDate", outDate);
            rentType = 0;
        }

    }

    public void showDatePickerDialog(int type) {
        DatePickerFragment newFragment = new DatePickerFragment();
        System.out.println("after nwe Fragment");
        newFragment.setType(type);
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.find_house_in:
                DatePickertype = 0;
                showDatePickerDialog(1);
                break;
            case R.id.find_house_leave:
                DatePickertype = 1;
                showDatePickerDialog(1);
                break;
            case R.id.find_house_submit:
                if(inDate.equals("")){
                    Toast.makeText(getActivity(), "请先选择入住日期~", Toast.LENGTH_LONG).show();
                    return;
                }
                GsonRequest<ArrayList<DistanceSort>> myOrderHistoryGsonRequest = new GsonRequest<ArrayList<DistanceSort>>(Request.Method.POST,
                        URI.MatchHousesAddr, new TypeToken<ArrayList<DistanceSort>>(){}.getType(),
                        new Response.Listener<ArrayList<DistanceSort>>() {
                            @Override
                            public void onResponse(ArrayList<DistanceSort> distance) {
                                mListData = new ArrayList<DistanceSort>(distance);
                                mAdapter = new RecommendAdapter(getActivity(),mListData);
                                listview.setAdapter(mAdapter);
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
                        map.put("longitude", String.valueOf(MainActivity.longitude));
                        map.put("latitude",String.valueOf(MainActivity.latitude));
                        map.put("price",price);
                        map.put("book_start",inDate);
                        map.put("book_end",outDate);
                        map.put("index",String.valueOf(0));
                        return map;
                    }
                };
                MyApplication.mQueue.add(myOrderHistoryGsonRequest);
                break;

            default:
                break;
        }

    }




}
