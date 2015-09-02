package com.example.lenovo.livingwhere.activity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.lenovo.livingwhere.R;

import java.io.File;

/**
 * 用于选择拍照或选择图片上传的页面，弹出式
 */

public class AddPictureSelectionActivity extends Activity implements View.OnClickListener{
    Button fromLocalButton,takePhotoButton,cancelButton;
    LinearLayout layout;
    long systemTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_picture_selection);
        takePhotoButton = (Button) this.findViewById(R.id.add_pic_selection_take);
        fromLocalButton = (Button) this.findViewById(R.id.add_pic_selection_from_local);
        cancelButton = (Button) this.findViewById(R.id.add_pic_selection_cancel);

        layout = (LinearLayout) findViewById(R.id.pop_linear);

        // 点击窗口内弹出Toast提示点击窗口外可以关闭窗口
        layout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                Toast.makeText(getApplicationContext(), "点击窗口外可关闭",
                        Toast.LENGTH_SHORT).show();

            }
        });
        cancelButton.setOnClickListener(this);
        fromLocalButton.setOnClickListener(this);
        takePhotoButton.setOnClickListener(this);
    }

    // 重写onTouchEvent点击屏幕关闭弹窗
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if(data!=null){
            if (data.getData()!= null){
                Intent intent = new Intent();
                intent.setData(data.getData());
                setResult(RESULT_OK, intent);
            }

        } else {
            Intent intent = new Intent();
            intent.putExtra("localPath", Environment.getExternalStorageDirectory() + "/LivingWherePic/"+systemTime + ".jpg");
            setResult(RESULT_OK, intent);
        }



        finish();

    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_pic_selection_take://选择拍照
                try {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    systemTime = System.currentTimeMillis();
                    File dir=new File(Environment.getExternalStorageDirectory() + "/LivingWherePic/"+systemTime + ".jpg");
                    if(!dir.getParentFile().exists())dir.getParentFile().createNewFile();
                    if(!dir.exists()) dir.createNewFile();
                    Uri u= Uri.fromFile(dir);
                    intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
                    startActivityForResult(intent, 1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.add_pic_selection_from_local://选择本地图片
                try {

                    /*Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 2);*/
                    Intent intent;
                    intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(intent, 1);
                } catch (ActivityNotFoundException e) {

                }
                break;
            case R.id.add_pic_selection_cancel://取消
                Intent intent = new Intent();
                intent.putExtra("cancel",true);
                setResult(RESULT_OK,intent);
                finish();
                break;
            default:
                break;
        }

    }



}




