package com.example.lenovo.livingwhere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.ImageLoader;
import com.example.lenovo.livingwhere.util.BitmapCache;
import com.example.lenovo.livingwhere.activity.MainActivity;
import com.example.lenovo.livingwhere.R;

import java.util.List;

/**
 * 水平滚动显示图片的控件适配器
 */
public class HorizontalScrollViewAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private List<String> mDatas;
    ImageLoader mLoader;
    final String picHead = "http://115.28.85.146:8080/Zhunaer/upload/housesPic/";

    public HorizontalScrollViewAdapter(Context context, List<String> mDatas)
    {
        this.mContext = context;
        mInflater = LayoutInflater.from(context);
        this.mDatas = mDatas;
        mLoader = new ImageLoader(MainActivity.mQueue, new BitmapCache());

    }

    public int getCount()
    {
        return mDatas.size();
    }

    public Object getItem(int position)
    {
        return mDatas.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        if(position>=mDatas.size()){
            return null;
        }
        ViewHolder viewHolder = null;
        if (convertView == null)
        {
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(
                    R.layout.item_index_gallery, parent, false);
            viewHolder.mImg = (ImageView) convertView
                    .findViewById(R.id.id_index_gallery_item_image);

            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(viewHolder.mImg,
                R.drawable.recommend_house_default, R.drawable.recommend_house_failed);
        mLoader.get(picHead+mDatas.get(position),listener,200,200);

        return convertView;
    }

    private class ViewHolder
    {
        ImageView mImg;
    }
}
