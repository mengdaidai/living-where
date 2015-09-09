package com.example.lenovo.livingwhere.activity;

import android.app.DatePickerDialog;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
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

public class OrderActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener{
    Button inButton,outButton,orderButton;//分别为入住日期，离开日期，预约按钮
    int DatePickertype,rentType = 1;//分别为当前选择日期类型（入住、离开），出租类型（长租or短租）
    String inDate = "",outDate = "";
    ImageButton cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        initView();

    }
    //选择日期后设置
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

    public void initView(){
        inButton = (Button)findViewById(R.id.order_in_button);
        outButton = (Button)findViewById(R.id.order_out_button);
        orderButton = (Button)findViewById(R.id.order_submit_button);
        cancelButton = (ImageButton)findViewById(R.id.order_cancel_button);
        inButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickertype = 0;
                showDatePickerDialog(0);
            }
        });
        outButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickertype = 1;
                showDatePickerDialog(0);

            }
        });
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                        map.put("uid",String.valueOf(MainActivity.userObj.getUid()));
                        map.put("hid",String.valueOf(3));
                        map.put("start",inDate);
                        if(rentType == 0)
                        map.put("end",outDate);
                        map.put("type",String.valueOf(rentType));
                        return map;
                    }
                };
                MyApplication.mQueue.add(orderRequest);
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                outDate = "";
                outButton.setText("离开时间");
                rentType = 1;
            }
        });
    }
    //显示日期选择
    public void showDatePickerDialog(int type) {
        DatePickerFragment newFragment = new DatePickerFragment();
        System.out.println("after nwe Fragment");
        newFragment.setType(type);
        newFragment.show(getFragmentManager(), "datePicker");
        
    }


}
