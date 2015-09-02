package com.example.lenovo.livingwhere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lenovo.livingwhere.R;

import java.util.List;

/**
 * 侧滑栏适配器
 *
 *
 */

class SlideViewHolder {
    public ImageView imageView;
    public TextView textView;
}
public class SlideMenuAdapter extends BaseAdapter {

    private LayoutInflater mInflater = null;
    public List<String> slideMenuText = null;


    public SlideMenuAdapter(Context context, List<String> slideMenuText) {
        super();
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.slideMenuText = slideMenuText;

    }

    @Override
    public int getCount() {
        return slideMenuText.size();
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
        final String text = slideMenuText.get(position);
        SlideViewHolder holder = null;
        if (convertView == null) {
            holder = new SlideViewHolder();
            convertView = mInflater.inflate(R.layout.item_slide_menu, null);
            holder.imageView = (ImageView) convertView.findViewById(R.id.slide_menu_item_image);
            holder.textView = (TextView) convertView.findViewById(R.id.slide_menu_item_text);

            convertView.setTag(holder);
        } else {
            holder = (SlideViewHolder) convertView.getTag();
        }

        holder.imageView.setImageResource(R.drawable.recommned_icon_address);
        holder.textView.setText(text);

        return convertView;

    }
}
