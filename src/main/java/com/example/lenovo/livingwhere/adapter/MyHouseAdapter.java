package com.example.lenovo.livingwhere.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.example.lenovo.livingwhere.activity.EditHouseActivity;
import com.example.lenovo.livingwhere.activity.OtherHouseActivity;
import com.example.lenovo.livingwhere.util.BitmapCache;
import com.example.lenovo.livingwhere.entity.Houses;
import com.example.lenovo.livingwhere.activity.MainActivity;
import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.util.MyApplication;
import com.example.lenovo.livingwhere.util.URI;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查看我的房子的适配器
 */
class MyHouseViewHolder implements Cloneable{
    public ImageView houseImage;
    public TextView text_location, text_count, price;
    Button offLineButton,editButton;

    @Override
    public Object clone() {
        MyHouseViewHolder holder = null;
        try{
            holder = (MyHouseViewHolder)super.clone();
        }catch(CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return holder;
    }

}

public class MyHouseAdapter extends BaseAdapter {
    private LayoutInflater mInflater = null;
    public List<Houses> houseInfo = null;
    ImageLoader imageLoader;
    Gson gson = new Gson();
    Context context;
    int i = 1;
    public MyHouseAdapter(Context context, List<Houses> houseInfo) {
        super();
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.houseInfo = houseInfo;
        imageLoader = new ImageLoader(MyApplication.mQueue, new BitmapCache());
        this.context = context;
    }

    @Override
    public int getCount() {
        return houseInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
       // Toast.makeText(context,"重新绘制",Toast.LENGTH_SHORT).show();
        Houses info = houseInfo.get(position);
        MyHouseViewHolder holder;
        if (convertView == null) {
            holder = new MyHouseViewHolder();
            convertView = mInflater.inflate(R.layout.item_my_house, null);
            holder.houseImage = (ImageView) convertView.findViewById(R.id.item_my_house_houseImage);
            holder.price = (TextView) convertView.findViewById(R.id.item_my_house_price);
            holder.text_location = (TextView) convertView.findViewById(R.id.item_my_house_text_location);
            holder.text_count = (TextView) convertView.findViewById(R.id.item_my_house_text_count);
            holder.offLineButton = (Button)convertView.findViewById(R.id.item_my_house_xiajia);
            holder.editButton = (Button)convertView.findViewById(R.id.item_my_house_edit);
            holder.offLineButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    final Button button = (Button)v;
                    final int mPosition = (int)v.getTag();
                    Toast.makeText(context,"tag!!!!!!!!!!"+mPosition,Toast.LENGTH_SHORT);
                    StringRequest uploadRequest = new StringRequest(Request.Method.POST, URI.ChangeStateOfHousesAddr, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            if (response.equals("操作成功")) {
                                if(houseInfo.get(mPosition).getState() == 1){
                                    button.setText("上架");
                                    houseInfo.get(mPosition).setState(2);
                                }
                                else if(houseInfo.get(mPosition).getState() == 2) {
                                    button.setText("下架");
                                    houseInfo.get(mPosition).setState(1);
                                }

                                Toast.makeText(context, response, Toast.LENGTH_LONG).show();
                            }

                            System.out.println(response);
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("uid", String.valueOf(MyApplication.user.getUid()));
                            map.put("hid", String.valueOf(houseInfo.get(mPosition).getHid()));
                            if (houseInfo.get(mPosition).getState() == 1)
                                map.put("state", String.valueOf(2));
                            else if (houseInfo.get(mPosition).getState() == 2)
                                map.put("state", String.valueOf(1));
                            return map;
                        }

                        @Override
                        protected Response<String> parseNetworkResponse(NetworkResponse response) {
                            String mString = null;
                            try {
                                mString = new String(response.data, "utf-8");
                            } catch (Exception e) {

                            }

                            return Response.success(mString, HttpHeaderParser.parseCacheHeaders(response));
                        }
                    };
                    MyApplication.mQueue.add(uploadRequest);

                }
            });
            holder.editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int mPosition = (int)v.getTag();
                    Intent intent = new Intent(context, EditHouseActivity.class);
                    intent.putExtra("house", (Serializable) houseInfo.get(mPosition));
                    System.out.println(mPosition);
                    ((Activity)context).startActivityForResult(intent, 1);
                    ((OtherHouseActivity)context).setPosition(mPosition);
                }
            });
            convertView.setTag(holder);

        } else {
            holder = (MyHouseViewHolder) convertView.getTag();
        }

        holder.offLineButton.setTag(position);
        holder.editButton.setTag(position);
        holder.offLineButton.setEnabled(true);
        if(houseInfo.get(position).getState() == 1) holder.offLineButton.setText("下架");
        else if(houseInfo.get(position).getState() == 2) holder.offLineButton.setText("上架");
        else if(houseInfo.get(position).getState() == 0)
        {
            holder.offLineButton.setText("等待审核中");
            holder.offLineButton.setEnabled(false);
        }

        // 进行数据设置
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.houseImage,
                R.drawable.recommend_house_default, R.drawable.recommend_house_failed);
        List<String> pics = gson.fromJson(houseInfo.get(position).getPictures(), new TypeToken<List<String>>() {
        }.getType());
        if(pics.size()!=0)
            imageLoader.get(URI.HousesPic + pics.get(0), listener, 200, 200);
        //这里数据有些差错，完了再改
        holder.price.setText(String.valueOf(info.getPrice())+"/天");
        holder.text_count.setText("交易数量:"+String.valueOf(info.getAmount()));
        holder.text_location.setText("地    点：" + info.getAddress());




        return convertView;


    }
}