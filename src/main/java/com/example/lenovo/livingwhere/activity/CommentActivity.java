package com.example.lenovo.livingwhere.activity;

import android.app.Dialog;
import android.content.CursorLoader;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.lenovo.livingwhere.util.AfterPicSelection;
import com.example.lenovo.livingwhere.util.FormImage;
import com.example.lenovo.livingwhere.net.PostUploadRequest;
import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.util.MyApplication;
import com.example.lenovo.livingwhere.util.URI;
import com.example.lenovo.livingwhere.view.DialogUtil;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于编辑评论的页面
 */

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {


    Button submitButton;//提交
    EditText commentEdit;//评论编辑框
    GridView gridView;//显示图片的
    Bitmap bmp;//临时存储Bitmap
    static ArrayList<HashMap<String, Object>> imageItem;//simpleAdpter数据
    static SimpleAdapter simpleAdapter;     //gridview适配器
    int hid;
    List<String> localPaths ;//用于临时存储照片路径
    ImageButton backButton;
    TextView title;
    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        initView();
        initEvent();
    }

    public void initView() {
        backButton = (ImageButton) findViewById(R.id.toolbar_back_title_back);
        title = (TextView) findViewById(R.id.toolbar_back_title_text);
        title.setText("编辑评论");
        Intent intent = getIntent();
        hid = intent.getIntExtra("hid", 0);
        submitButton = (Button) findViewById(R.id.comment_submit);
        commentEdit = (EditText) findViewById(R.id.comment_edittext);
        bmp = BitmapFactory.decodeResource(getResources(), R.drawable.comment_plus_64px);
        imageItem = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("itemImage", bmp);
        imageItem.add(map);
        simpleAdapter = new SimpleAdapter(this,
                imageItem, R.layout.grid_item_pic,
                new String[]{"itemImage"}, new int[]{R.id.grid_item_pic});

        gridView = (GridView) findViewById(R.id.comment_pic_grid_view);
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                // TODO Auto-generated method stub
                if (view instanceof ImageView && data instanceof Bitmap) {
                    ImageView v = (ImageView) view;
                    v.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
        gridView.setAdapter(simpleAdapter);


    }


    public void initEvent() {
        backButton.setOnClickListener(this);
        submitButton.setOnClickListener(this);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (imageItem.size() == 5 && position == imageItem.size()-1) { //如果照片张数满了
                    Toast.makeText(CommentActivity.this, "最多可添加四张照片~", Toast.LENGTH_SHORT).show();
                } else if (position == imageItem.size()-1) { //点击的是加号
                    Toast.makeText(CommentActivity.this, "添加照片", Toast.LENGTH_SHORT).show();
                    startActivityForResult(new Intent(CommentActivity.this,
                            AddPictureSelectionActivity.class), 1);

                } else {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putString("localPath", (String) imageItem.get(position).get("localPath"));
                    bundle.putInt("position", position);
                    bundle.putInt("type", 1);
                    intent.putExtras(bundle);
                    intent.setClass(CommentActivity.this, BigPictureActivity.class);
                    startActivityForResult(intent, 2);

                }
            }

        });
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        System.out.println("requestCode" + requestCode);
        switch (requestCode) {
            case 1://选择照片后返回
                if (data.getBooleanExtra("cancel", false))//我也忘了这句干啥的= =
                    return;
                Uri mImageCaptureUri = data.getData();
                if (data.getStringExtra("localPath") == null) {
                    localPaths = (List<String>)data.getSerializableExtra("dirs");
                    new UpdateViewTask().execute(localPaths);
                } else {
                    String localPath = data.getStringExtra("localPath");
                    localPaths = new ArrayList<String>();
                    localPaths.add(localPath);
                    new UpdateViewTask().execute(localPaths);
                }

                break;
            case 2://查看大图后返回
                if (data != null) {
                    boolean delete = data.getBooleanExtra("delete", false);
                    Log.e("delete", "" + delete);
                    if (delete) {
                        int pos = data.getIntExtra("position", 0);
                        imageItem.remove(pos);
                        simpleAdapter.notifyDataSetChanged();
                    }

                }
                break;

            default:
                break;

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.comment_submit:
                dialog = DialogUtil.createLoadingDialog(this,"正在提交");
                dialog.show();
                List<FormImage> formImageList = new ArrayList<FormImage>();
                int i = 1;
                for (HashMap map : imageItem) {
                    if (i == imageItem.size()) {
                        break;

                    }

                    Log.e("submit", "before decode");
                    Log.e("path", (String) map.get("localPath"));

                    Bitmap bmp = BitmapFactory.decodeFile((String) map.get("localPath"));
                    Log.e("submit", "after decode");
                    formImageList.add(new FormImage(bmp, "pic" + (i - 1), "评论图" + (i - 1) + ".jpg", "image/jpg"));
                    i++;
                }
                Map<String, String> map = new HashMap<String, String>();
                map.put("uid", String.valueOf(MyApplication.user.getUid()));
                map.put("hid", String.valueOf(hid));
                map.put("message", commentEdit.getText().toString());
                PostUploadRequest uploadRequest = new PostUploadRequest(URI.AddCommentsAddr, formImageList, map, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        dialog.cancel();
                        Toast.makeText(CommentActivity.this,response,Toast.LENGTH_LONG).show();
                        imageItem.clear();
                        System.out.println(response);
                        finish();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                });
                MyApplication.mQueue.add(uploadRequest);

                break;
            case R.id.toolbar_back_title_back:
                finish();
                break;
        }
    }


    private class UpdateViewTask extends AsyncTask<List<String>, String, List<Bitmap>> {

        @Override
        protected List<Bitmap> doInBackground(List<String>... params) {
            List<Bitmap> bitmaps = new ArrayList<Bitmap>();
            for(String dir:params[0]){
                Bitmap bitmap = AfterPicSelection.getSmallBitmap(dir, 128 * 128);
                bitmaps.add(bitmap);
            }
            return bitmaps;
        }

        @Override
        protected void onPostExecute(List<Bitmap> bitmaps) {

            int i = 0;
            for(Bitmap bitmap:bitmaps) {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("itemImage", bitmap);
                map.put("localPath",localPaths.get(i));
                imageItem.add(0, map);
                i++;
            }
            simpleAdapter = new SimpleAdapter(CommentActivity.this,
                    imageItem, R.layout.grid_item_pic,
                    new String[]{"itemImage"}, new int[]{R.id.grid_item_pic});
            simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    // TODO Auto-generated method stub
                    if (view instanceof ImageView && data instanceof Bitmap) {
                        ImageView v = (ImageView) view;
                        v.setImageBitmap((Bitmap) data);
                        return true;
                    }
                    return false;
                }
            });
            gridView.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
        }
    }


}
