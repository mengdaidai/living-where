package com.example.lenovo.livingwhere.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.adapter.HorizontalScrollViewAdapter;
import com.example.lenovo.livingwhere.entity.Houses;
import com.example.lenovo.livingwhere.view.HouseDetailsHorizontalScrollView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class HouseDetailsActivity extends AppCompatActivity implements View.OnClickListener{

    private HouseDetailsHorizontalScrollView mHorizontalScrollView;//展示房子图片的横向滚动组件
    private HorizontalScrollViewAdapter mAdapter;
    TextView addressText,countText,phoneText,introductionText,starText,priceText,otherHouseText,title;
    Button orderButton,commentButton,lookCommentButton;
    Houses mHouse;
    private List<String> mDatas = new ArrayList<String>();
    ImageButton backButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_house_details);
        Intent intent = getIntent();
        mHouse = (Houses)intent.getSerializableExtra("house");
        initView();
        initEvent();
    }


    public void initView(){
        backButton = (ImageButton)findViewById(R.id.toolbar_back_title_back);
        title = (TextView)findViewById(R.id.toolbar_back_title_text);
        title.setText("住房详情");
        commentButton = (Button)findViewById(R.id.house_details_comment);
        lookCommentButton = (Button)findViewById(R.id.house_details_look_comment);
        otherHouseText = (TextView)findViewById(R.id.house_details_other_houses);
        otherHouseText.setText(Html.fromHtml("<u>" + "查看房主其他房子" + "</u>"));
        orderButton = (Button)findViewById(R.id.house_details_order);
        addressText = (TextView)findViewById(R.id.house_details_place);
        addressText.setText(addressText.getText()+mHouse.getAddress());
        countText = (TextView)findViewById(R.id.house_details_count);
        countText.setText(countText.getText()+String.valueOf(mHouse.getAmount()));
        phoneText = (TextView)findViewById(R.id.house_details_tel);
        phoneText.setText(Html.fromHtml("<u>" + mHouse.getContactPhone() + "</u>"));
        introductionText = (TextView)findViewById(R.id.house_details_introduction);
        introductionText.setText(mHouse.getDescription());
        starText = (TextView)findViewById(R.id.house_details_star);
        starText.setText(starText.getText()+String.valueOf(mHouse.getStar()));
        priceText = (TextView)findViewById(R.id.house_details_price);
        priceText.setText("￥"+String.valueOf(mHouse.getPrice())+"/天");
        mHorizontalScrollView = (HouseDetailsHorizontalScrollView) findViewById(R.id.id_horizontalScrollView);
        Gson gson = new Gson();
        mDatas = gson.fromJson(mHouse.getPictures(),new TypeToken<List<String>>(){}.getType());
        mAdapter = new HorizontalScrollViewAdapter(HouseDetailsActivity.this, mDatas);

        //初始化
        mHorizontalScrollView.initDatas(mAdapter);

    }


    public void initEvent(){
        backButton.setOnClickListener(this);
        commentButton.setOnClickListener(this);
        lookCommentButton.setOnClickListener(this);
        otherHouseText.setOnClickListener(this);
        orderButton.setOnClickListener(this);
        phoneText.setOnClickListener(this);
        //设置item点击事件
        mHorizontalScrollView.setOnItemClickListener(new HouseDetailsHorizontalScrollView.OnItemClickListener() {

            @Override
            public void onClick(View view, int position) {
                String pic = mDatas.get(position);
                Intent intent = new Intent(HouseDetailsActivity.this, BigPictureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("type", 2);
                bundle.putString("url", pic);
                bundle.putInt("from", 1);
                intent.putExtras(bundle);
                startActivity(intent);

            }
        });
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.toolbar_back_title_back:
                finish();
                break;
            case R.id.house_details_comment:
                intent = new Intent(HouseDetailsActivity.this, CommentActivity.class);
                intent.putExtra("hid", mHouse.getHid());
                startActivity(intent);
                break;
            case R.id.house_details_look_comment:
                intent = new Intent(HouseDetailsActivity.this, HouseCommentActivity.class);
                intent.putExtra("hid", mHouse.getHid());
                startActivity(intent);
                break;
            case R.id.house_details_other_houses:
                intent = new Intent(HouseDetailsActivity.this, OtherHouseActivity.class);
                intent.putExtra("hid",mHouse.getHid());
                intent.putExtra("type",0);
                startActivity(intent);
                break;
            case R.id.house_details_order:
                intent = new Intent(HouseDetailsActivity.this, OrderActivity.class);
                startActivity(intent);
                break;
            case R.id.house_details_tel:
                Intent phoneIntent = new Intent(
                        "android.intent.action.CALL", Uri.parse("tel:"
                        + mHouse.getContactPhone()));
                startActivity(phoneIntent);
                break;
        }
    }
}
