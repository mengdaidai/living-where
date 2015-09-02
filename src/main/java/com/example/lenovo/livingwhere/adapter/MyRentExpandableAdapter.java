package com.example.lenovo.livingwhere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.example.lenovo.livingwhere.util.BitmapCache;
import com.example.lenovo.livingwhere.activity.MainActivity;
import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.entity.RentHistoryObj;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * 我出租的房子的预约记录
 */
class RentExpandableViewHolder{
    TextView parentTitle,childLocationText,childStartText,childEndText,childPriceText;
    ImageView houseImage;


}

public class MyRentExpandableAdapter extends BaseExpandableListAdapter {
    List<String> typeList;
    List<List<RentHistoryObj>> childData;
    private LayoutInflater mInflater = null;
    ImageLoader mLoader;
    Gson gson;
    final String picHead = "http://115.28.85.146:8080/Zhunaer/upload/housesPic/";
    public MyRentExpandableAdapter(Context context,List<String> typeList,List<List<RentHistoryObj>> childData) {
        this.typeList = typeList;
        this.childData = childData;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLoader = new ImageLoader(MainActivity.mQueue, new BitmapCache());
        gson = new Gson();
    }

    @Override
    public int getGroupCount() {
        return typeList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childData.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return typeList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childData.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    //并不知道公用一个viewholder会出现什么后果
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        RentExpandableViewHolder holder = null;
        if(convertView == null){
            holder = new RentExpandableViewHolder();
            convertView = mInflater.inflate(R.layout.item_parent_expandable,null);
            holder.parentTitle = (TextView)convertView.findViewById(R.id.item_parent_expandable_title);
            convertView.setTag(holder);
        }else{
            holder = (RentExpandableViewHolder)convertView.getTag();
        }
        holder.parentTitle.setText(typeList.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        RentExpandableViewHolder holder = null;
        if(convertView == null){
            holder = new RentExpandableViewHolder();
            convertView = mInflater.inflate(R.layout.item_child_expandable,null);
            holder.childLocationText = (TextView)convertView.findViewById(R.id.item_parent_expandable_title);
            holder.childEndText = (TextView)convertView.findViewById(R.id.item_child_expandable_text_endtime);
            holder.childStartText= (TextView)convertView.findViewById(R.id.item_child_expandable_text_starttime);
            holder.childPriceText = (TextView)convertView.findViewById(R.id.item_child_expandable_price);
            holder.houseImage = (ImageView)convertView.findViewById(R.id.item_child_expandable_houseImage);
            convertView.setTag(holder);
        }else{
            holder = (RentExpandableViewHolder)convertView.getTag();
        }

        ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.houseImage,
                R.drawable.recommend_house_default, R.drawable.recommend_house_failed);
        List<String> pics = gson.fromJson(childData.get(groupPosition).get(childPosition).getHeadPic(),new TypeToken<List<String>>(){}.getType());
        mLoader.get(picHead+pics.get(0),listener,200,200);
        //holder.childLocationText.setText(childData.get(groupPosition).get(childPosition).getAddress());
        holder.childEndText.setText(childData.get(groupPosition).get(childPosition).getStart());
        holder.childStartText.setText(childData.get(groupPosition).get(childPosition).getEnd());
        //holder.childPriceText.setText(String.valueOf(childData.get(groupPosition).get(childPosition).getPrice()));
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}

