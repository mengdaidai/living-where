package com.example.lenovo.livingwhere.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.adapter.HorizontalScrollViewAdapter;
import com.example.lenovo.livingwhere.entity.Houses;
import com.example.lenovo.livingwhere.view.HouseDetailsHorizontalScrollView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class HouseDetailsActivity extends AppCompatActivity {

    private HouseDetailsHorizontalScrollView mHorizontalScrollView;//展示房子图片的横向滚动组件
    private HorizontalScrollViewAdapter mAdapter;
    TextView addressText,countText,phoneText,introductionText,starText,priceText,otherHouseText;
    Button orderButton,commentButton,lookCommentButton;
    Houses mHouse;
    private List<String> mDatas = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_details);
        Intent intent = getIntent();
        mHouse = (Houses)intent.getSerializableExtra("house");
        initView();
    }


    public void initView(){
        commentButton = (Button)findViewById(R.id.house_details_comment);
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HouseDetailsActivity.this, CommentActivity.class);
                intent.putExtra("hid", mHouse.getHid());
                startActivity(intent);
            }
        });
        lookCommentButton = (Button)findViewById(R.id.house_details_look_comment);
        lookCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HouseDetailsActivity.this, HouseCommentActivity.class);
                intent.putExtra("hid", mHouse.getHid());
                startActivity(intent);
            }
        });
        otherHouseText = (TextView)findViewById(R.id.house_details_other_houses);
        otherHouseText.setText(Html.fromHtml("<u>" + "查看房主其他房子" + "</u>"));
        otherHouseText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HouseDetailsActivity.this, OtherHouseActivity.class);
                intent.putExtra("hid",mHouse.getHid());
                intent.putExtra("type",0);
                startActivity(intent);
            }
        });
        orderButton = (Button)findViewById(R.id.house_details_order);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HouseDetailsActivity.this, OrderActivity.class);
                startActivity(intent);
            }
        });
        addressText = (TextView)findViewById(R.id.house_details_place);
        addressText.setText("地       点:"+mHouse.getAddress());
        countText = (TextView)findViewById(R.id.house_details_count);
        countText.setText("交易数量："+String.valueOf(mHouse.getAmount()));
        phoneText = (TextView)findViewById(R.id.house_details_tel);
        phoneText.setText("房主电话:"+mHouse.getContactPhone());
        introductionText = (TextView)findViewById(R.id.house_details_introduction);
        introductionText.setText("住房简介:\n"+mHouse.getDescription());
        starText = (TextView)findViewById(R.id.house_details_star);
        starText.setText("星       级:"+String.valueOf(mHouse.getStar()));
        priceText = (TextView)findViewById(R.id.house_details_price);
        priceText.setText("$"+String.valueOf(mHouse.getPrice())+"/天");
        mHorizontalScrollView = (HouseDetailsHorizontalScrollView) findViewById(R.id.id_horizontalScrollView);
        Gson gson = new Gson();
        mDatas = gson.fromJson(mHouse.getPictures(),new TypeToken<List<String>>(){}.getType());
        mAdapter = new HorizontalScrollViewAdapter(HouseDetailsActivity.this, mDatas);
        //设置item点击事件
        mHorizontalScrollView.setOnItemClickListener(new HouseDetailsHorizontalScrollView.OnItemClickListener() {

            @Override
            public void onClick(View view, int position) {
                String pic = mDatas.get(position);
                Intent intent = new Intent(HouseDetailsActivity.this, BigPictureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("type", 2);
                bundle.putString("url", pic);
                bundle .putInt("from",1);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
        //初始化
        mHorizontalScrollView.initDatas(mAdapter);

    }


}
