package com.example.lenovo.livingwhere.activity;

import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.example.lenovo.livingwhere.util.AfterPicSelection;
import com.example.lenovo.livingwhere.util.BitmapCache;
import com.example.lenovo.livingwhere.entity.CurrentUserObj;
import com.example.lenovo.livingwhere.util.FormImage;
import com.example.lenovo.livingwhere.net.PostUploadRequest;
import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.util.URI;
import com.google.gson.Gson;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EditInfoActivity extends AppCompatActivity {

    Button uploadHeadPic,submitButton;
    EditText nickNameEdit,ageEdit,qianmingEdit;
    ImageView headPic;
    String localPath = "";//当前上传头像的本地路径
    List<FormImage> myHeadPic;
    Spinner genderSpinner;
    String[] genderString = {"男","女"};
    ArrayAdapter<String> adapter;
    boolean headChanged = false;
    static Bitmap smallBitmap;//压缩后的缩略图
    int gender;
    ImageLoader imageLoader;
    Gson gson;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1://上传头像返回
                if(data.getBooleanExtra("cancel",false))//我也忘了这句干啥的= =
                    return;
                Uri mImageCaptureUri = data.getData();
                //返回的Uri不为空时，那么图片信息数据都会在Uri中获得。如果为空，那么我们就进行下面的方式获取
                if (mImageCaptureUri != null) {
                    localPath = AfterPicSelection.getPath(this, mImageCaptureUri);
                    new UpdateViewTask().execute(localPath);
                }else{
                    localPath = data.getStringExtra("localPath");
                    new UpdateViewTask().execute(localPath);
                }
                break;
            case 2://查看大图返回
                //啥也不用干
                break;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        gson = new Gson();
        initView();
    }
    public void initView(){
        uploadHeadPic = (Button)findViewById(R.id.edit_info_upload_head_pic);
        submitButton = (Button)findViewById(R.id.edit_info_submit);
        nickNameEdit = (EditText)findViewById(R.id.edit_info_nick_name);
        nickNameEdit.setText(MainActivity.userObj.getNickname());
        ageEdit = (EditText)findViewById(R.id.edit_info_age);
        ageEdit.setText(Integer.toString(MainActivity.userObj.getAge()));
        genderSpinner = (Spinner)findViewById(R.id.edit_info_gender);
        qianmingEdit = (EditText)findViewById(R.id.edit_info_gexingqianming);
        qianmingEdit.setText(MainActivity.userObj.getSignature());
        headPic = (ImageView)findViewById(R.id.edit_info_head_pic);
        imageLoader = new ImageLoader(MainActivity.mQueue,new BitmapCache());
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(headPic,
                R.drawable.pic_head_normal, R.drawable.pic_head_selected);
        imageLoader.get(MainActivity.userObj.getHeadPic(), listener,200,200);

        //将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,genderString);

        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //将adapter 添加到spinner中
        genderSpinner.setAdapter(adapter);

        //添加事件Spinner事件监听
        genderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gender = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //设置默认值
        genderSpinner.setVisibility(View.VISIBLE);

        headPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(EditInfoActivity.this, BigPictureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("type", 2);
                bundle.putInt("from",1);
                intent.putExtras(bundle);
                startActivityForResult(intent, 2);
            }
        });


        uploadHeadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//开启选择图片方式弹窗

                Toast.makeText(EditInfoActivity.this, "添加照片", Toast.LENGTH_SHORT).show();
                startActivityForResult(new Intent(EditInfoActivity.this,
                        AddPictureSelectionActivity.class), 1);
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myHeadPic = new ArrayList<FormImage>();
                if(!localPath.equals("")){//说明头像已经被改变
                    Bitmap bmp = BitmapFactory.decodeFile(localPath);
                    myHeadPic.add(new FormImage(bmp, "headPic", "头像.jpg", "image/jpg"));
                    headChanged = true;
                }
                //文本信息
                Map<String, String> map = new HashMap<String, String>();
                map.put("uid", String.valueOf(MainActivity.userObj.getUid()));
                map.put("gender", String.valueOf(gender));//性别男0女1
                map.put("age", ageEdit.getText().toString().trim());
                map.put("signature", qianmingEdit.getText().toString());
                map.put("nickname", nickNameEdit.getText().toString());
                PostUploadRequest editInfoRequest = new PostUploadRequest(URI.UpdateMyInfoAddr, myHeadPic, map, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        Toast.makeText(EditInfoActivity.this, s, Toast.LENGTH_LONG).show();
                        System.out.println(s);
                        MainActivity.userObj = gson.fromJson(s, CurrentUserObj.class);
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("picChanged",headChanged);
                        intent.putExtras(bundle);
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    }
                });
                MainActivity.mQueue.add(editInfoRequest);
            }
        });



    }


    private class UpdateViewTask extends AsyncTask<String,String,Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = AfterPicSelection.getSmallBitmap(params[0], 128 * 128);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            headPic.setImageBitmap(bitmap);

        }
    }




}



