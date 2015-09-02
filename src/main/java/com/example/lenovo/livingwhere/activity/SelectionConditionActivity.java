package com.example.lenovo.livingwhere.activity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.lenovo.livingwhere.entity.DistanceSort;
import com.example.lenovo.livingwhere.fragment.DatePickerFragment;
import com.example.lenovo.livingwhere.net.GsonRequest;
import com.example.lenovo.livingwhere.R;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 展示筛选条件的页面，不过快没用了
 */
public class SelectionConditionActivity extends Activity implements View.OnClickListener,DatePickerDialog.OnDateSetListener{
    Button sureButton,cancelButton,startButton,endButton;
    EditText priceEdit;
    LinearLayout layout;
    Intent intent;
    int DatePickertype,rentType = 1;
    String inDate = "",outDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selection_condition);
        intent = getIntent();
        priceEdit  =(EditText)this.findViewById(R.id.selection_condition_price);
        startButton = (Button) this.findViewById(R.id.selection_condition_start);
        endButton = (Button) this.findViewById(R.id.selection_condition_end);
        sureButton = (Button) this.findViewById(R.id.selection_condition_commit);
        cancelButton = (Button) this.findViewById(R.id.selection_condition_cancel);

        layout = (LinearLayout) findViewById(R.id.condition_pop_linear);

        layout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "点击屏幕也取消~",
                        Toast.LENGTH_SHORT).show();

            }
        });
        // ��Ӱ�ť����
        cancelButton.setOnClickListener(this);
        sureButton.setOnClickListener(this);
        endButton.setOnClickListener(this);
        startButton.setOnClickListener(this);
    }

    // ʵ��onTouchEvent������������Ļʱ��ٱ�Activity
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }



    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.selection_condition_start:
                DatePickertype = 0;
                showDatePickerDialog(1);
                break;
            case R.id.selection_condition_end:
                DatePickertype = 1;
                showDatePickerDialog(1);
                break;
            case R.id.selection_condition_commit:
                if(inDate.equals("")){
                    Toast.makeText(SelectionConditionActivity.this,"请先选择入住日期~",Toast.LENGTH_LONG).show();
                    return;
                }
                GsonRequest<ArrayList<DistanceSort>> myOrderHistoryGsonRequest = new GsonRequest<ArrayList<DistanceSort>>(Request.Method.POST,
                        "http://115.28.85.146:8080/Zhunaer/action/msg_matchHouses", new TypeToken<ArrayList<DistanceSort>>(){}.getType(),
                        new Response.Listener<ArrayList<DistanceSort>>() {
                            @Override
                            public void onResponse(ArrayList<DistanceSort> distance) {
                                Intent intent = new Intent();
                                intent.putExtra("distance",(Serializable)distance);
                                intent.putExtra("longitude", MainActivity.longitude);
                                intent.putExtra("latitude", MainActivity.latitude);
                                intent.putExtra("price", priceEdit.getText().toString());
                                intent.putExtra("book_start", inDate);
                                intent.putExtra("book_end", outDate);
                                setResult(RESULT_OK, intent);
                                finish();
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
                        map.put("price",priceEdit.getText().toString());
                        map.put("book_start",inDate);
                        map.put("book_end",outDate);
                        map.put("index",String.valueOf(0));
                        return map;
                    }
                };
                MainActivity.mQueue.add(myOrderHistoryGsonRequest);
                break;
            case R.id.selection_condition_cancel:
                setResult(RESULT_OK);
                finish();
            default:
                break;
        }

    }

    public void showDatePickerDialog(int type) {
        DatePickerFragment newFragment = new DatePickerFragment();
        System.out.println("after nwe Fragment");
        newFragment.setType(type);
        newFragment.show(getFragmentManager(), "datePicker");
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
            startButton.setText(text);
            Log.e("inDate",inDate);
        }
        else if(DatePickertype == 1){
            outDate = String.valueOf(year);
            if(month<10) outDate+=("0"+String.valueOf(month));
            else outDate+=String.valueOf(month);
            if(day<10) outDate+=("0"+String.valueOf(day));
            else outDate+=String.valueOf(day);
            endButton.setText(year+"年"+month+"月"+day + "日");
            Log.e("outDate", outDate);
            rentType = 0;
        }

    }


}
