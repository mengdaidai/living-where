package com.example.lenovo.livingwhere.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.example.lenovo.livingwhere.util.BitmapCache;
import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.util.MyApplication;
import com.example.lenovo.livingwhere.util.URI;

/**
 * 我的信息页面
 */
public class MyInfoActivity extends AppCompatActivity implements View.OnClickListener{

    Button editButton;
    ImageButton backButton;
    TextView nicknameText,ageText,signatureText,genderText,title;
    ImageView headPicImageView;//头像
    ImageLoader imageLoader;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode){
            case 1://编辑信息的请求
                if(data == null) return;
                Bundle bundle = data.getExtras();
                nicknameText.setText(MyApplication.user.getNickname());
                ageText.setText(Integer.toString(MyApplication.user.getAge()));
                signatureText.setText(MyApplication.user.getSignature());
                if(MyApplication.user.getGender() == 0)
                    genderText.setText("男");
                else
                    genderText.setText("女");
                boolean picChanged = (boolean)bundle.get("picChanged");
                if(picChanged) {
                    headPicImageView.setImageBitmap(MyApplication.smallHeadBitmap);
                }
                break;
            case 2://查看大图的请求
                //啥也不干
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_info_activity);

        initView();
        initEvent();
    }

    public void initView(){
        title = (TextView)findViewById(R.id.toolbar_back_title_text);
        title.setText("个人信息");
        editButton = (Button)findViewById(R.id.my_info_edit);
        headPicImageView = (ImageView)findViewById(R.id.my_info_head_pic);
        imageLoader = new ImageLoader(MyApplication.mQueue,new BitmapCache());
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(headPicImageView,
                R.drawable.my_info_btn_header, R.drawable.my_info_btn_header);
        imageLoader.get(URI.HeadPic+MyApplication.user.getHeadPic(), listener, 400, 400);
        nicknameText = (TextView)findViewById(R.id.my_info_nick_name);
        nicknameText.setText(MyApplication.user.getNickname());
        ageText = (TextView)findViewById(R.id.my_info_age);
        ageText.setText(Integer.toString(MyApplication.user.getAge()));
        genderText = (TextView)findViewById(R.id.my_info_gender);
        if(MyApplication.user.getGender() == 0)
            genderText.setText("男");
        else
            genderText.setText("女");
        signatureText = (TextView)findViewById(R.id.my_info_gexingqianming);
        signatureText.setText(MyApplication.user.getSignature());
        backButton = (ImageButton)findViewById(R.id.toolbar_back_title_back);


    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.toolbar_back_title_back:
                finish();
                break;
            case R.id.my_info_edit:
                Intent intent = new Intent(MyInfoActivity.this,EditInfoActivity.class);
                startActivityForResult(intent,1);
                break;
        }
    }


    public void initEvent(){
        backButton.setOnClickListener(this);
        headPicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MyInfoActivity.this, BigPictureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("type", 2);
                bundle.putString("url",MyApplication.user.getHeadPic());
                bundle.putInt("from",2);
                intent.putExtras(bundle);
                startActivityForResult(intent, 2);
            }
        });
        editButton.setOnClickListener(this);
    }
}
