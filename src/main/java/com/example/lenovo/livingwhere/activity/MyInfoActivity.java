package com.example.lenovo.livingwhere.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.example.lenovo.livingwhere.util.BitmapCache;
import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.util.URI;

/**
 * 我的信息页面
 */
public class MyInfoActivity extends AppCompatActivity {

    Button editButton;
    TextView nicknameText,ageText,signatureText,genderText;
    ImageView headPicImageView;//头像
    ImageLoader imageLoader;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("MyInfoActivity的回调！！！！！！！");
        switch(requestCode){
            case 1://编辑信息的请求
                Bundle bundle = data.getExtras();
                nicknameText.setText(MainActivity.userObj.getNickname());
                ageText.setText(Integer.toString(MainActivity.userObj.getAge()));
                signatureText.setText(MainActivity.userObj.getSignature());
                if(MainActivity.userObj.getGender() == 0)
                    genderText.setText("男");
                else
                    genderText.setText("女");
                boolean picChanged = (boolean)bundle.get("picChanged");
                if(picChanged) headPicImageView.setImageBitmap(EditInfoActivity.smallBitmap);
                //这是一种直接传bitmap的方法，还有在CommentActivity中记得尝试将decodeFile异步进行，不然好卡啊啊啊啊！！！！！！！

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
    }

    public void initView(){
        Intent intent = new Intent();
        Bundle bundle = intent.getExtras();
        editButton = (Button)findViewById(R.id.my_info_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyInfoActivity.this,EditInfoActivity.class);
                startActivityForResult(intent,1);
            }
        });
        headPicImageView = (ImageView)findViewById(R.id.my_info_head_pic);
        headPicImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(MyInfoActivity.this, BigPictureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("type", 2);
                bundle.putString("url",MainActivity.userObj.getHeadPic());
                bundle.putInt("from",1);
                intent.putExtras(bundle);
                startActivityForResult(intent, 2);
            }
        });
        imageLoader = new ImageLoader(MainActivity.mQueue,new BitmapCache());
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(headPicImageView,
                R.drawable.pic_head_normal, R.drawable.pic_head_selected);
        System.out.println(MainActivity.userObj.getHeadPic());
        imageLoader.get(URI.HeadPic+MainActivity.userObj.getHeadPic(), listener, 200, 200);
        nicknameText = (TextView)findViewById(R.id.my_info_nick_name);
        nicknameText.setText(MainActivity.userObj.getNickname());
        ageText = (TextView)findViewById(R.id.my_info_age);
        ageText.setText(Integer.toString(MainActivity.userObj.getAge()));
        genderText = (TextView)findViewById(R.id.my_info_gender);
        if(MainActivity.userObj.getGender() == 0)
            genderText.setText("男");
        else
            genderText.setText("女");
        signatureText = (TextView)findViewById(R.id.my_info_gexingqianming);
        signatureText.setText(MainActivity.userObj.getSignature());

    }


}
