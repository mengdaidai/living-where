package com.example.lenovo.livingwhere.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.example.lenovo.livingwhere.entity.Houses;
import com.example.lenovo.livingwhere.util.BitmapCache;
import com.example.lenovo.livingwhere.activity.MainActivity;
import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.entity.RentHistoryObj;
import com.example.lenovo.livingwhere.util.MyApplication;
import com.example.lenovo.livingwhere.util.URI;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我出租的房子的预约记录
 */
class RentExpandableViewHolder{
    TextView parentTitle,childLocationText,childStartText,childEndText;
    ImageView houseImage;
    Button compeleteButton;
}

public class MyRentExpandableAdapter extends BaseExpandableListAdapter {
    List<String> typeList;
    List<List<RentHistoryObj>> childData;
    private LayoutInflater mInflater = null;
    ImageLoader mLoader;
    Context context;
    Gson gson;
    public MyRentExpandableAdapter(Context context,List<String> typeList,List<List<RentHistoryObj>> childData) {
        this.typeList = typeList;
        this.childData = childData;
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLoader = new ImageLoader(MyApplication.mQueue, new BitmapCache());
        gson = new Gson();
        this.context = context;
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
            holder.childLocationText = (TextView)convertView.findViewById(R.id.item_child_expandable_text_location);
            holder.childEndText = (TextView)convertView.findViewById(R.id.item_child_expandable_text_endtime);
            holder.childStartText= (TextView)convertView.findViewById(R.id.item_child_expandable_text_starttime);
            holder.houseImage = (ImageView)convertView.findViewById(R.id.item_child_expandable_houseImage);
            holder.compeleteButton = (Button)convertView.findViewById(R.id.item_child_expandable_button_compelete);
            if(groupPosition == 0)
                holder.compeleteButton.setVisibility(View.VISIBLE);
            holder.compeleteButton.setTag(childPosition);
            holder.compeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int childPos = (int)v.getTag();
                    StringRequest request = new StringRequest(Request.Method.POST, URI.ChangeBookStateAddr, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            Toast.makeText(context,s,Toast.LENGTH_LONG).show();
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> map = new HashMap<String,String>();
                            map.put("bhid",String.valueOf(childData.get(0).get(childPos).getBhid()));
                            map.put("hid",String.valueOf(childData.get(0).get(childPos).getHid()));
                            map.put("state",String.valueOf(1));
                            return map;
                        }
                    };
                    MyApplication.mQueue.add(request);
                }
            });

            convertView.setTag(holder);
        }else{
            holder = (RentExpandableViewHolder)convertView.getTag();
        }

        ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.houseImage,
                R.drawable.recommend_house_default, R.drawable.recommend_house_failed);
        List<String> pics = gson.fromJson(childData.get(groupPosition).get(childPosition).getHeadPic(), new TypeToken<List<String>>() {
        }.getType());
        if(pics!=null)
        mLoader.get(URI.HousesPic + pics.get(0), listener, 200, 200);
        //int size = childData.get(0).size();
        //RentHistoryObj obj  = childData.get(groupPosition).get(childPosition);
        //Houses house = childData.get(groupPosition).get(childPosition).getHouse();
        //String address = childData.get(groupPosition).get(childPosition).getHouse().getAddress();
        holder.childLocationText.setText(childData.get(groupPosition).get(childPosition).getHouse().getAddress());
        holder.childEndText.setText(childData.get(groupPosition).get(childPosition).getStart());
        holder.childStartText.setText(childData.get(groupPosition).get(childPosition).getEnd());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


}

