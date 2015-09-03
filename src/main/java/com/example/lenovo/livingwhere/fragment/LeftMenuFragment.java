package com.example.lenovo.livingwhere.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.lenovo.livingwhere.R;

/**
 * 左边侧滑栏
 */


public class LeftMenuFragment extends Fragment {

    public LeftMenuFragment() {
        // Required empty public constructor
        int x = 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_left_menu, container, false);
    }


}
