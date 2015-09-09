package com.example.lenovo.livingwhere.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;

import com.example.lenovo.livingwhere.activity.MainActivity;
import com.example.lenovo.livingwhere.activity.OrderActivity;
import com.example.lenovo.livingwhere.activity.SelectionConditionActivity;

import java.util.Calendar;

/**
 * 用于日期选择
 */

public class DatePickerFragment extends DialogFragment {
    int type ;//0代表OrderActivity,1代表SelectionActivity

    public DatePickerFragment() {
        // Required empty public constructor
    }

    public void setType(int type){
        this.type = type;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        Log.e("month", String.valueOf(month));

        if(type == 0)
            return new DatePickerDialog(getActivity(), (OrderActivity)getActivity(), year, month, day);
        else
            return new DatePickerDialog(getActivity(), (FindHouseFragment)((MainActivity)getActivity()).fragments[2], year, month, day);


    }



}
