package com.example.lenovo.livingwhere.activity;

import android.app.DatePickerDialog;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.lenovo.livingwhere.fragment.DatePickerFragment;
import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.util.MyApplication;
import com.example.lenovo.livingwhere.util.URI;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * 预约页面
 */

public class OrderActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener,View.OnClickListener{
    Button inButton,outButton,orderButton;//分别为入住日期，离开日期，预约按钮
    int DatePickertype,rentType = 1;//分别为当前选择日期类型（入住、离开），出租类型（长租or短租）
    String inDate = "",outDate = "";
    ImageButton cancelButton,backButton;
    TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initView();
        initEvent();
    }
    //选择日期后设置
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        //do some stuff for example write on log and update TextField on activity
        Log.w("DatePicker", "Date = " + year);
        if(DatePickertype == 0){
            inDate = String.valueOf(year);
            if(month+1<10) inDate+=("0"+String.valueOf(month+1));
            else inDate+=String.valueOf(month+1);
            if(day<10) inDate+=("0"+String.valueOf(day));
            else inDate+=String.valueOf(day);
            String text = String.valueOf(year)+"年"+String.valueOf(month+1)+"月"+String.valueOf(day)+"日";
            inButton.setText(text);
            Log.e("inDate",inDate);
        }
        else if(DatePickertype == 1){
            outDate = String.valueOf(year);
            if(month+1<10) outDate+=("0"+String.valueOf(month+1));
            else outDate+=String.valueOf(month+1);
            if(day<10) outDate+=("0"+String.valueOf(day));
            else outDate+=String.valueOf(day);
            outButton.setText(year+"年"+(month+1)+"月"+day + "日");
            Log.e("outDate", outDate);
            rentType = 0;
        }

    }

    public void initView(){
        inButton = (Button)findViewById(R.id.order_in_button);
        outButton = (Button)findViewById(R.id.order_out_button);
        orderButton = (Button)findViewById(R.id.order_submit_button);
        cancelButton = (ImageButton)findViewById(R.id.order_cancel_button);
        backButton = (ImageButton)findViewById(R.id.toolbar_back_title_back);
        title = (TextView)findViewById(R.id.toolbar_back_title_text);
        title.setText("预约");

    }

    public void initEvent(){
        inButton.setOnClickListener(this);
        outButton.setOnClickListener(this);
        orderButton.setOnClickListener(this);
        cancelButton.setOnClickListener(this);
        backButton.setOnClickListener(this);
    }


    //显示日期选择
    public void showDatePickerDialog(int type) {
        DatePickerFragment newFragment = new DatePickerFragment();
        System.out.println("after nwe Fragment");
        newFragment.setType(type);
        newFragment.show(getFragmentManager(), "datePicker");
        
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_back_title_back:
                finish();
                break;
            case R.id.order_in_button:
                DatePickertype = 0;
                showDatePickerDialog(0);
                break;
            case R.id.order_out_button:
                DatePickertype = 1;
                showDatePickerDialog(0);
                break;
            case R.id.order_submit_button:
                if(inDate.equals("")){
                    Toast.makeText(OrderActivity.this,"请先选择入住日期~",Toast.LENGTH_LONG).show();
                    return;
                }

                //发送网络请求
                StringRequest orderRequest = new StringRequest(Request.Method.POST, URI.AddToBookHistoryAddr,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String s) {
                                Toast.makeText(OrderActivity.this,s,Toast.LENGTH_LONG).show();
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }){

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
                        Map<String,String> map = new HashMap<String, String>();
                        map.put("uid",String.valueOf(MyApplication.user.getUid()));
                        map.put("hid",String.valueOf(3));
                        map.put("start",inDate);
                        if(rentType == 0)
                            map.put("end",outDate);
                        map.put("type",String.valueOf(rentType));
                        return map;
                    }
                };
                MyApplication.mQueue.add(orderRequest);
                break;
            case R.id.order_cancel_button:
                outDate = "";
                outButton.setText("离开时间");
                rentType = 1;
                break;
        }
    }


}
