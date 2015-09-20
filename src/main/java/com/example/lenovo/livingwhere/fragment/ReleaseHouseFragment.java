package com.example.lenovo.livingwhere.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.activity.AddPictureSelectionActivity;
import com.example.lenovo.livingwhere.activity.BigPictureActivity;
import com.example.lenovo.livingwhere.entity.Houses;
import com.example.lenovo.livingwhere.net.PostUploadRequest;
import com.example.lenovo.livingwhere.util.AfterPicSelection;
import com.example.lenovo.livingwhere.util.FormImage;
import com.example.lenovo.livingwhere.util.MyApplication;
import com.example.lenovo.livingwhere.util.URI;
import com.example.lenovo.livingwhere.view.DialogUtil;
import com.example.lenovo.livingwhere.view.OnFragmentListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发布房子或修改房子信息
 */


public class ReleaseHouseFragment extends Fragment {
    EditText phoneEdit, priceEdit, descripEdit, addressEdit;
    Spinner typeSpinner;
    Button submit;
    GridView picGridView;
    String[] spinnerStr = {"个人住房", "宾馆"};//房子类型
    OnFragmentListener mFragmentListener;
    Dialog dialog;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFragmentListener = (OnFragmentListener) activity;
    }

    ArrayAdapter<String> adapter;
    int type;//表示住房的种类是个人住房或宾馆酒店
    SimpleAdapter simpleAdapter;
    ArrayList<HashMap<String, Object>> imageItem;
    String url = "";
    String picNameModel = "";
    Houses mHouse = null;
    List<FormImage> formImageList = new ArrayList<FormImage>();
    int i = 1;
    String localPath = "";//用于临时存储照片路径

    public Houses getmHouse() {
        return mHouse;
    }

    public void setmHouse(Houses mHouse) {
        this.mHouse = mHouse;
    }

    public ReleaseHouseFragment() {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1://添加照片后返回
                if (data.getBooleanExtra("cancel", false))//我也忘了这句干啥的= =
                    return;
                Uri mImageCaptureUri = data.getData();
                //返回的Uri不为空时，那么图片信息数据都会在Uri中获得。如果为空，那么我们就进行下面的方式获取
                if (mImageCaptureUri != null) {
                    localPath = AfterPicSelection.getPath(getActivity(), mImageCaptureUri);
                    new UpdateViewTask().execute(localPath);
                } else {
                    localPath = data.getStringExtra("localPath");
                    new UpdateViewTask().execute(localPath);
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_release_horse, container, false);
        phoneEdit = (EditText) view.findViewById(R.id.release_house_phone);
        priceEdit = (EditText) view.findViewById(R.id.release_house_price);
        //sizeEdit = (EditText) view.findViewById(R.id.release_house_size);
        descripEdit = (EditText) view.findViewById(R.id.release_house_description);
        addressEdit = (EditText) view.findViewById(R.id.release_house_address);
        typeSpinner = (Spinner) view.findViewById(R.id.release_house_type);
        submit = (Button) view.findViewById(R.id.release_house_submit);
        picGridView = (GridView) view.findViewById(R.id.release_house_grid_view);
        //将可选内容与ArrayAdapter连接起来
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.myspinner, spinnerStr);
        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter 添加到spinner中
        typeSpinner.setAdapter(adapter);
        //添加事件Spinner事件监听
        typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = position;
                TextView tv = (TextView) view;

                tv.setTextSize(20.0f);    //设置大小

                tv.setGravity(android.view.Gravity.CENTER_HORIZONTAL);   //设置居中
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        //设置默认值
        typeSpinner.setVisibility(View.VISIBLE);

        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.comment_plus_64px);
        imageItem = new ArrayList<HashMap<String, Object>>();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("itemImage", bmp);
        imageItem.add(map);
        simpleAdapter = new SimpleAdapter(getActivity(),
                imageItem, R.layout.grid_item_pic,
                new String[]{"itemImage"}, new int[]{R.id.grid_item_pic});
        simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                // TODO Auto-generated method stub
                if (view instanceof ImageView && data instanceof Bitmap) {
                    ImageView i = (ImageView) view;
                    i.setImageBitmap((Bitmap) data);
                    return true;
                }
                return false;
            }
        });
        picGridView.setAdapter(simpleAdapter);
        if (mHouse != null) {//说明此页面用来修改房子信息
            url = URI.UpdateMyHouseInfoAddr;
            picNameModel = "housesPic";
            //phoneEdit.setText(mHouse.getContactPhone());
            phoneEdit.setText(String.valueOf(mHouse.getHid()));
            priceEdit.setText(String.valueOf(mHouse.getPrice()));
            // sizeEdit.setText(String.valueOf(mHouse.getSize()));
            descripEdit.setText(mHouse.getDescription());
            addressEdit.setText(mHouse.getAddress());
            typeSpinner.setSelection(mHouse.getType());
            Gson gson = new Gson();
            final List<String> picsUrl = gson.fromJson(mHouse.getPictures(), new TypeToken<List<String>>() {
            }.getType());

            for (String s : picsUrl) {
                final String ms = s;
                ImageRequest request = new ImageRequest(URI.HousesPic + s, new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        map.put("itemImage", response);
                        map.put("url", ms);
                        imageItem.add(map);
                        simpleAdapter.notifyDataSetChanged();
                    }
                }, 200, 200, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
                );
                MyApplication.mQueue.add(request);

            }

        } else {
            url = URI.ReleaseHousesAddr;
            picNameModel = "housesPic";
        }
        picGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (imageItem.size() == 10 && position == 0) { //如果照片张数满了
                    Toast.makeText(getActivity(), "最多可添加九张照片~", Toast.LENGTH_SHORT).show();
                } else if (position == 0) { //点击的是加号
                    Toast.makeText(getActivity(), "添加照片", Toast.LENGTH_SHORT).show();
                    startActivityForResult(new Intent(getActivity(),
                            AddPictureSelectionActivity.class), 1);

                } else {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    if (imageItem.get(position).get("localPath") != null) {
                        bundle.putString("localPath", (String) imageItem.get(position).get("localPath"));
                        bundle.putInt("position", position);
                        bundle.putInt("type", 1);
                    } else if (imageItem.get(position).get("url") != null) {
                        bundle.putString("url", (String) imageItem.get(position).get("url"));
                        bundle.putInt("position", position);
                        bundle.putInt("type", 3);
                    }
                    intent.putExtras(bundle);
                    intent.setClass(getActivity(), BigPictureActivity.class);
                    startActivityForResult(intent, 2);

                }
            }

        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = DialogUtil.createLoadingDialog(getActivity(),"正在提交");
                dialog.show();

                for (HashMap map1 : imageItem) {
                    if (i == 1) {
                        i++;
                        continue;
                    }

                    Log.e("submit", "before decode");
                    if (map1.get("localPath") != null) {//图片可从本地获取

                        Bitmap bmp = BitmapFactory.decodeFile((String) map1.get("localPath"));
                        formImageList.add(new FormImage(bmp, picNameModel + (i - 1), "住房图" + (i - 1) + ".jpg", "image/jpg"));
                        if (formImageList.size() == imageItem.size() - 1) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("uid", String.valueOf(MyApplication.user.getUid()));
                            map.put("description", descripEdit.getText().toString());
                            map.put("contactPhone", phoneEdit.getText().toString());
                            map.put("address", addressEdit.getText().toString());
                            map.put("size", "0");
                            map.put("price", priceEdit.getText().toString());
                            map.put("longitude", String.valueOf(MyApplication.longitude));
                            map.put("latitude", String.valueOf(MyApplication.latitude));
                            map.put("type", String.valueOf(type));
                            if (mHouse != null) {
                                map.put("hid", String.valueOf(mHouse.getHid()));
                            }
                            PostUploadRequest uploadRequest = new PostUploadRequest(url, formImageList, map, new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    dialog.cancel();
                                    if(response.equals("操作成功")||response.equals("无法访问"))
                                        Toast.makeText(getActivity(),response,Toast.LENGTH_LONG).show();
                                    else{
                                        Gson gson = new Gson();
                                        Houses house = gson.fromJson(response, Houses.class);
                                        i = 1;
                                        mFragmentListener.updateMyHouseList(house);
                                    }

                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError volleyError) {

                                }
                            });
                            MyApplication.mQueue.add(uploadRequest);
                        }
                    } else {//图片来自网络
                        ImageRequest request = new ImageRequest(URI.HousesPic + (String) map1.get("url"), new Response.Listener<Bitmap>() {
                            int mi = i;
                            @Override
                            public void onResponse(Bitmap response) {
                                formImageList.add(new FormImage(response, picNameModel + (mi - 1), "住房图" + (mi - 1) + ".jpg", "image/jpg"));
                                if (formImageList.size() == imageItem.size() - 1) {
                                    Map<String, String> map = new HashMap<String, String>();
                                    map.put("uid", String.valueOf(MyApplication.user.getUid()));
                                    map.put("description", descripEdit.getText().toString());
                                    map.put("contactPhone", phoneEdit.getText().toString());
                                    map.put("address", addressEdit.getText().toString());
                                    map.put("size", "0");
                                    map.put("price", priceEdit.getText().toString());
                                    map.put("longitude", String.valueOf(MyApplication.longitude));
                                    map.put("latitude", String.valueOf(MyApplication.latitude));
                                    map.put("type", String.valueOf(type));
                                    if (mHouse != null) {
                                        map.put("hid", String.valueOf(mHouse.getHid()));
                                    }
                                    PostUploadRequest uploadRequest = new PostUploadRequest(url, formImageList, map, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            if(response.equals("操作成功")||response.equals("无法访问"))
                                                Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                                            else{
                                                Gson gson = new Gson();
                                                Houses house = gson.fromJson(response, Houses.class);
                                                i = 1;
                                                mFragmentListener.updateMyHouseList(house);
                                            }

                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError volleyError) {

                                        }

                                    });
                                    MyApplication.mQueue.add(uploadRequest);

                                }
                            }
                        }, 0, 0, Bitmap.Config.RGB_565, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {

                            }
                        }
                        );
                        MyApplication.mQueue.add(request);
                    }
                    i++;
                }


            }


        });
        return view;
    }


    private class UpdateViewTask extends AsyncTask<String, String, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... params) {
            Bitmap bitmap = AfterPicSelection.getSmallBitmap(params[0], 128 * 128);
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("itemImage", bitmap);
            map.put("localPath", localPath);
            imageItem.add(map);
            simpleAdapter = new SimpleAdapter(getActivity(),
                    imageItem, R.layout.grid_item_pic,
                    new String[]{"itemImage"}, new int[]{R.id.grid_item_pic});
            simpleAdapter.setViewBinder(new SimpleAdapter.ViewBinder() {
                @Override
                public boolean setViewValue(View view, Object data,
                                            String textRepresentation) {
                    // TODO Auto-generated method stub
                    if (view instanceof ImageView && data instanceof Bitmap) {
                        ImageView i = (ImageView) view;
                        i.setImageBitmap((Bitmap) data);
                        return true;
                    }
                    return false;
                }
            });
            picGridView.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
        }
    }


}
