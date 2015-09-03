package com.example.lenovo.livingwhere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.example.lenovo.livingwhere.util.BitmapCache;
import com.example.lenovo.livingwhere.entity.DistanceSort;
import com.example.lenovo.livingwhere.activity.MainActivity;
import com.example.lenovo.livingwhere.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * 首页推荐的适配器
 */

class ViewHolder {
    public ImageView houseImage;
    public TextView text_location, text_count, price;

}

public class RecommendAdapter extends BaseAdapter {
    private LayoutInflater mInflater = null;
    public List<DistanceSort> houseInfo = null;
    ImageLoader imageLoader;
    Gson gson = new Gson();
    final String picHead = "http://115.28.85.146:8080/Zhunaer/upload/housesPic/";

    public RecommendAdapter(Context context, List<DistanceSort> houseInfo) {
        super();
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.houseInfo = houseInfo;
        imageLoader = new ImageLoader(MainActivity.mQueue, new BitmapCache());
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

        final DistanceSort info = houseInfo.get(position);
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_recommend, null);
            holder.houseImage = (ImageView) convertView.findViewById(R.id.item_recommend_houseImage);
            holder.price = (TextView) convertView.findViewById(R.id.item_recommend_price);
            holder.text_location = (TextView) convertView.findViewById(R.id.item_recommend_text_location);
            holder.text_count = (TextView) convertView.findViewById(R.id.item_recommend_text_count);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        // 进行数据设置
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.houseImage,
                R.drawable.recommend_house_default, R.drawable.recommend_house_failed);
        List<String> pics = gson.fromJson(houseInfo.get(position).getHouse().getPictures(),new TypeToken<List<String>>(){}.getType());
        if(pics.size()!=0)
        imageLoader.get(picHead+pics.get(0),listener,200,200);
        //这里数据有些差错，完了再改
        holder.price.setText(String.valueOf(info.getHouse().getPrice()));
        holder.text_count.setText(String.valueOf(info.getHouse().getAmount()));
        holder.text_location.setText(info.getHouse().getAddress());



        /*convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // 加载详细新闻
                detailUrl = mListData.get(position).childUrl;
                AnsynHttpRequest.requestByGet(context, callbackData, R.string.http_news_detail, detailUrl, true, true, false);
            }
        });*/
        return convertView;


    }


}
