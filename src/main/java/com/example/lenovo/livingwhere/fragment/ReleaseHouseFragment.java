package com.example.lenovo.livingwhere.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.activity.AddPictureSelectionActivity;
import com.example.lenovo.livingwhere.activity.BigPictureActivity;
import com.example.lenovo.livingwhere.activity.ChooseLocActivity;
import com.example.lenovo.livingwhere.adapter.MyAdapter;
import com.example.lenovo.livingwhere.entity.Houses;
import com.example.lenovo.livingwhere.net.PostUploadRequest;
import com.example.lenovo.livingwhere.util.AfterPicSelection;
import com.example.lenovo.livingwhere.util.FormImage;
import com.example.lenovo.livingwhere.util.MyApplication;
import com.example.lenovo.livingwhere.util.URI;
import com.example.lenovo.livingwhere.view.DialogUtil;
import com.example.lenovo.livingwhere.view.OnFragmentListener;
import com.example.lenovo.livingwhere.view.PopWindow;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 发布房子或修改房子信息
 */


public class ReleaseHouseFragment extends Fragment {
    EditText phoneEdit, priceEdit, descripEdit;
    Button submit,locationButton,beHostButton;
    GridView picGridView;
    OnFragmentListener mFragmentListener;
    Dialog dialog;
    TextView tv_hometype;
    double latitude,longitude;



    ArrayAdapter<String> adapter;
    int type;//表示住房的种类是个人住房或宾馆酒店
    SimpleAdapter simpleAdapter;
    ArrayList<HashMap<String, Object>> imageItem;
    String url = "";
    String picNameModel = "";
    Houses mHouse = null;
    List<FormImage> formImageList = new ArrayList<FormImage>();
    int i = 1;
    List<String> localPaths ;//用于临时存储照片路径

    public Houses getmHouse() {
        return mHouse;
    }

    public void setmHouse(Houses mHouse) {
        this.mHouse = mHouse;
    }

    public ReleaseHouseFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mFragmentListener = (OnFragmentListener) activity;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 0://定位返回
                if(data!=null) {
                    longitude = data.getDoubleExtra("Longitude", 0);
                    latitude = data.getDoubleExtra("Lantitude", 0);
                    String Location = data.getStringExtra("Location");
                    locationButton.setText(Location);
                    Toast.makeText(getActivity(), longitude + Location + latitude, Toast.LENGTH_SHORT).show();
                }

                break;
            case 1://添加照片后返回
                if (data.getBooleanExtra("cancel", false))//我也忘了这句干啥的= =
                    return;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = null;
            view = inflater.inflate(R.layout.fragment_release_house, container, false);
            phoneEdit = (EditText) view.findViewById(R.id.release_house_phone);
            priceEdit = (EditText) view.findViewById(R.id.release_house_price);
            descripEdit = (EditText) view.findViewById(R.id.release_house_description);
            locationButton = (Button) view.findViewById(R.id.release_house_address);
            tv_hometype = (TextView) view.findViewById(R.id.release_house_type);
            submit = (Button) view.findViewById(R.id.release_house_submit);
            picGridView = (GridView) view.findViewById(R.id.release_house_grid_view);
            tv_hometype.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopWindow popWindow = new PopWindow(v.getContext(), tv_hometype);
                    popWindow.showPopupWindow(tv_hometype);
                }
            });
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
                phoneEdit.setText(String.valueOf(mHouse.getContactPhone()));
                priceEdit.setText(String.valueOf(mHouse.getPrice()));
                // sizeEdit.setText(String.valueOf(mHouse.getSize()));
                descripEdit.setText(mHouse.getDescription());
                locationButton.setText(mHouse.getAddress());
                // typeSpinner.setSelection(mHouse.getType());
                if (mHouse.getType() == 0)
                    tv_hometype.setText("个人住房");
                else if (mHouse.getType() == 1)
                    tv_hometype.setText("宾馆酒店");
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

            } else {//说明此页面用于发布房子
                url = URI.ReleaseHousesAddr;
                picNameModel = "housesPic";

            }
            picGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (imageItem.size() == 10 && position == imageItem.size()-1) { //如果照片张数满了
                        Toast.makeText(getActivity(), "最多可添加九张照片~", Toast.LENGTH_SHORT).show();
                    } else if (position == imageItem.size()-1) { //点击的是加号
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

            locationButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent (getActivity(),ChooseLocActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putDouble("Lantitude", MyApplication.latitude);
                    bundle.putDouble("Longitude", MyApplication.longitude);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 0);
                }
            });

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog = DialogUtil.createLoadingDialog(getActivity(), "正在提交");
                    dialog.show();

                    for (HashMap map1 : imageItem) {
                        if (i == imageItem.size()) {
                            break;
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
                                map.put("address", locationButton.getText().toString());
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
                                        formImageList.clear();
                                        i = 1;
                                        if(response.equals("操作成功")||response.equals("无法访问"))
                                            Toast.makeText(getActivity(),response,Toast.LENGTH_LONG).show();
                                        else{

                                            Gson gson = new Gson();
                                            Houses house = gson.fromJson(response, Houses.class);

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
                                        map.put("address", locationButton.getText().toString());
                                        map.put("size", "0");
                                        map.put("price", priceEdit.getText().toString());
                                        map.put("longitude", String.valueOf(longitude));
                                        map.put("latitude", String.valueOf(latitude));
                                        map.put("type", String.valueOf(type));
                                        if (mHouse != null) {
                                            map.put("hid", String.valueOf(mHouse.getHid()));
                                        }
                                        PostUploadRequest uploadRequest = new PostUploadRequest(url, formImageList, map, new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                dialog.cancel();
                                                formImageList.clear();
                                                i = 1;
                                                if(response.equals("操作成功")||response.equals("无法访问"))
                                                    Toast.makeText(getActivity(), response, Toast.LENGTH_LONG).show();
                                                else {
                                                    Gson gson = new Gson();
                                                    Houses house = gson.fromJson(response, Houses.class);

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
            for(Bitmap bitmap:bitmaps){
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("itemImage", bitmap);
                map.put("localPath", localPaths.get(i));
                imageItem.add(0,map);
                i++;
            }
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
            MyAdapter.mSelectedImage.clear();
        }
    }


}
