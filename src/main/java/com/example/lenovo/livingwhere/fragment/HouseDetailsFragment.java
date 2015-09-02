package com.example.lenovo.livingwhere.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.lenovo.livingwhere.activity.BigPictureActivity;
import com.example.lenovo.livingwhere.activity.CommentActivity;
import com.example.lenovo.livingwhere.activity.HouseCommentActivity;
import com.example.lenovo.livingwhere.activity.OrderActivity;
import com.example.lenovo.livingwhere.activity.OtherHouseActivity;
import com.example.lenovo.livingwhere.adapter.HorizontalScrollViewAdapter;
import com.example.lenovo.livingwhere.view.HouseDetailsHorizontalScrollView;
import com.example.lenovo.livingwhere.entity.Houses;
import com.example.lenovo.livingwhere.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * 房子细节展示
 */


public class HouseDetailsFragment extends Fragment {

    private View view;

    private HouseDetailsHorizontalScrollView mHorizontalScrollView;//展示房子图片的横向滚动组件
    private HorizontalScrollViewAdapter mAdapter;
    TextView addressText;
    Button orderButton,otherHouseButton,commentButton,lookCommentButton;
    Houses mHouse;

    private List<String> mDatas = new ArrayList<String>();
    public HouseDetailsFragment() {
        System.out.println("HouseDetailsFragment");
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("HouseDetailsFragment");


    }

    public void initView(){
        commentButton = (Button)view.findViewById(R.id.house_details_comment);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CommentActivity.class);
                intent.putExtra("hid", mHouse.getHid());
                startActivity(intent);
            }
        });
        lookCommentButton = (Button)view.findViewById(R.id.house_details_look_comment);
        lookCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), HouseCommentActivity.class);
                intent.putExtra("hid", mHouse.getHid());
                startActivity(intent);
            }
        });
        otherHouseButton = (Button)view.findViewById(R.id.house_details_other_houses);
        otherHouseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OtherHouseActivity.class);
                intent.putExtra("hid",mHouse.getHid());
                intent.putExtra("type",0);
                startActivity(intent);
            }
        });
        orderButton = (Button)view.findViewById(R.id.house_details_order);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), OrderActivity.class);
                startActivity(intent);
            }
        });
        addressText = (TextView)view.findViewById(R.id.house_details_place);
        addressText.setText(mHouse.getAddress());
        mHorizontalScrollView = (HouseDetailsHorizontalScrollView) view.findViewById(R.id.id_horizontalScrollView);
        Gson gson = new Gson();
        mDatas = gson.fromJson(mHouse.getPictures(),new TypeToken<List<String>>(){}.getType());
        mAdapter = new HorizontalScrollViewAdapter(getActivity(), mDatas);
        //设置item点击事件
        mHorizontalScrollView.setOnItemClickListener(new HouseDetailsHorizontalScrollView.OnItemClickListener() {

            @Override
            public void onClick(View view, int position) {
                String pic = mDatas.get(position);
                Intent intent = new Intent(getActivity(), BigPictureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("type", 2);
                bundle.putString("url", pic);
                bundle .putInt("from",1);
                startActivity(intent);

            }
        });
        //初始化
        mHorizontalScrollView.initDatas(mAdapter);

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_house_details,container,false);
        return view;
    }

    public void displayDetails(Houses house){
        mHouse = house;
        initView();
    }



}
