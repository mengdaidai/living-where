package com.example.lenovo.livingwhere.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.example.lenovo.livingwhere.util.BitmapCache;
import com.example.lenovo.livingwhere.R;

/**
 * 这个类用于显示点击缩略图后显示大图
 */

public class BigPictureActivity extends AppCompatActivity {
    ImageView image;
    ImageButton deleteButton,backButton;
    String localPath,url;
    int position;//记录点击位置
    Intent intent;
    Bundle bundle;
    ImageLoader imageLoader;
    ImageLoader.ImageListener listener;
    final String picHouse = "http://115.28.85.146:8080/Zhunaer/upload/housesPic/";
    final String picHead = "http://115.28.85.146:8080/Zhunaer/upload/headPic/";
    final String picComment = "http://115.28.85.146:8080/Zhunaer/upload/commentsPic/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        intent = getIntent();
        bundle = intent.getExtras();
        intiView(bundle.getInt("type"));

    }

    public void intiView(int type){
        switch(type){
            case 1://有localpath，可以删除的大图（评论、发布房子、修改房子信息）
                setContentView(R.layout.activity_big_picture);
                image = (ImageView)findViewById(R.id.big_picture_image);
                localPath = (String)bundle.get("localPath");
                position = (int)bundle.get("position");
                Bitmap bmp = BitmapFactory.decodeFile(localPath);
                image.setImageBitmap(bmp);
                deleteButton = (ImageButton)findViewById(R.id.actionbar_big_picture_deleteButton);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog();
                    }
                });
                backButton = (ImageButton)findViewById(R.id.actionbar_big_picture_backButton);
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                break;
            case 2://有url,不可以删除的大图（房子详情、头像）
                setContentView(R.layout.activity_big_head_pic);
                url = (String)bundle.get("url");
                backButton = (ImageButton)findViewById(R.id.big_head_pic_back);
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                image =(ImageView)findViewById(R.id.big_head_pic_image);
                imageLoader = new ImageLoader(MainActivity.mQueue,new BitmapCache());
                listener = ImageLoader.getImageListener(image,
                        R.drawable.recommend_house_default, R.drawable.recommend_house_failed);
                if(bundle.getInt("from")==1)//代表房子相关
                    imageLoader.get(picHouse+url,listener);
                else//代表头像相关
                    imageLoader.get(picHead+url,listener);
                break;
            case 3://有url,可以删除的大图（修改房屋信息）
                setContentView(R.layout.activity_big_picture);
                image = (ImageView)findViewById(R.id.big_picture_image);
                position = (int)bundle.get("position");
                url = (String)bundle.get("url");
                imageLoader = new ImageLoader(MainActivity.mQueue,new BitmapCache());
                listener = ImageLoader.getImageListener(image,
                        R.drawable.recommend_house_default, R.drawable.recommend_house_failed);
                imageLoader.get(picHouse+url,listener);
                deleteButton = (ImageButton)findViewById(R.id.actionbar_big_picture_deleteButton);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog();
                    }
                });
                backButton = (ImageButton)findViewById(R.id.actionbar_big_picture_backButton);
                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });

        }

    }


    protected void dialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(BigPictureActivity.this);
        builder.setMessage("ȷ确认删除图片吗");
        builder.setTitle("提示");
        builder.setPositiveButton("ȷ确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                Intent intent = new Intent();
                intent.putExtra("delete", true);
                intent.putExtra("position", position);
                setResult(RESULT_OK
                        , intent);
                finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }





}
