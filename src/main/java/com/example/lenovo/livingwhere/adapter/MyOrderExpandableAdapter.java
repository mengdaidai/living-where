package com.example.lenovo.livingwhere.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Rating;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.example.lenovo.livingwhere.activity.BigPictureActivity;
import com.example.lenovo.livingwhere.util.BitmapCache;
import com.example.lenovo.livingwhere.entity.BookHistoryObj;
import com.example.lenovo.livingwhere.activity.MainActivity;
import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.util.MyApplication;
import com.example.lenovo.livingwhere.util.URI;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 我的预约历史的适配器
 */


class ExpandableViewHolder{
    TextView parentTitle,childLocationText,childStartText,childEndText,childTelText;
    ImageView houseImage;
    Button compeleteButton;


}

public class MyOrderExpandableAdapter extends BaseExpandableListAdapter{
    List<String> typeList;
    List<List<BookHistoryObj>> childData;
    private LayoutInflater mInflater = null;
    ImageLoader mLoader;
    Gson gson;
    Context context;
    public MyOrderExpandableAdapter(Context context,List<String> typeList,List<List<BookHistoryObj>> childData) {
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
        ExpandableViewHolder holder = null;
        if(convertView == null){
            holder = new ExpandableViewHolder();
            convertView = mInflater.inflate(R.layout.item_parent_expandable,null);
            holder.parentTitle = (TextView)convertView.findViewById(R.id.item_parent_expandable_title);
            convertView.setTag(holder);
        }else{
            holder = (ExpandableViewHolder)convertView.getTag();
        }
        holder.parentTitle.setText(typeList.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ExpandableViewHolder holder = null;
        if(convertView == null){
            holder = new ExpandableViewHolder();
            convertView = mInflater.inflate(R.layout.item_child_expandable,null);
            holder.childLocationText = (TextView)convertView.findViewById(R.id.item_child_expandable_text_location);
            holder.childEndText = (TextView)convertView.findViewById(R.id.item_child_expandable_text_endtime);
            holder.childStartText= (TextView)convertView.findViewById(R.id.item_child_expandable_text_starttime);
            holder.houseImage = (ImageView)convertView.findViewById(R.id.item_child_expandable_houseImage);
            holder.compeleteButton = (Button)convertView.findViewById(R.id.item_child_expandable_button_compelete);
            holder.childTelText = (TextView)convertView.findViewById(R.id.item_child_expandable_text_tel);
            holder.compeleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Button button = (Button)v;
                    final int childPos = (int)v.getTag();
                    StringRequest request = new StringRequest(Request.Method.POST, URI.ChangeBookStateAddr, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String s) {
                            Toast.makeText(context, s, Toast.LENGTH_LONG).show();
                            if(s.equals("操作成功")){
                                button.setEnabled(false);
                                button.setText("已完成");
                                childData.get(1).get(childPos).setState(2);
                            }
                            dialog(childData.get(1).get(childPos).getHid());
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError volleyError) {

                        }
                    }){
                        @Override
                        protected Response<String> parseNetworkResponse(NetworkResponse response) {
                            try {
                                String mString =
                                        new String(response.data, "utf-8");

                                return Response.success(mString,
                                        HttpHeaderParser.parseCacheHeaders(response));
                            } catch (UnsupportedEncodingException e) {
                                return Response.error(new ParseError(e));
                            }
                        }


                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String,String> map = new HashMap<String,String>();
                            map.put("bhid",String.valueOf(childData.get(1).get(childPos).getBhid()));
                            map.put("hid",String.valueOf(childData.get(1).get(childPos).getHid()));
                            map.put("state",String.valueOf(2));
                            map.put("uid",String.valueOf(MyApplication.user.getUid()));
                            return map;
                        }
                    };
                    MyApplication.mQueue.add(request);
                }
            });
            convertView.setTag(holder);
        }else{
            holder = (ExpandableViewHolder)convertView.getTag();

        }
        if(groupPosition == 1)
            holder.compeleteButton.setVisibility(View.VISIBLE);
        else
            holder.compeleteButton.setVisibility(View.GONE);
        if(childData.get(groupPosition).get(childPosition).getState() == 2){
            holder.compeleteButton.setEnabled(false);
        }else{
            holder.compeleteButton.setEnabled(true);
        }
        holder.compeleteButton.setTag(childPosition);

        ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.houseImage,
                R.drawable.recommend_house_default, R.drawable.recommend_house_failed);
        List<String> pics = gson.fromJson(childData.get(groupPosition).get(childPosition).getPictures(),new TypeToken<List<String>>(){}.getType());
        if(pics!=null)
        mLoader.get(URI.HousesPic + pics.get(0), listener, 200, 200);
        holder.childLocationText.setText("地    点："+childData.get(groupPosition).get(childPosition).getAddress());
        holder.childEndText.setText("离开时间:"+childData.get(groupPosition).get(childPosition).getEnd());
        holder.childStartText.setText("入住时间:"+childData.get(groupPosition).get(childPosition).getStart());
        holder.childTelText.setText("电    话:"+childData.get(groupPosition).get(childPosition).getContactPhone());
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    protected void dialog(int h) {
        final int hid = h;
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater factory = LayoutInflater.from(context);
        final View ratingView = factory.inflate(R.layout.dialog_rating,null);
        builder.setMessage("请为该房子评分吧~");
        builder.setTitle("提示");
        builder.setView(ratingView);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                final RatingBar bar = (RatingBar) ratingView.findViewById(R.id.rating);
                //final DialogInterface dialogInterface = dialog;
                StringRequest request = new StringRequest(Request.Method.POST, URI.StarScaleAddr, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        if (s.equals("操作成功")) {
                            Toast.makeText(context, s, Toast.LENGTH_LONG).show();
                            dialog.dismiss();
                        } else {
                            Toast.makeText(context, s, Toast.LENGTH_LONG).show();
                            System.out.println(s);
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

                    }
                }) {
                    @Override
                    protected Response<String> parseNetworkResponse(NetworkResponse response) {
                        try {
                            String mString =
                                    new String(response.data, "utf-8");

                            return Response.success(mString,
                                    HttpHeaderParser.parseCacheHeaders(response));
                        } catch (UnsupportedEncodingException e) {
                            return Response.error(new ParseError(e));
                        }
                    }
                    @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("stars", String.valueOf((int)bar.getRating()));
                    map.put("hid",String.valueOf(hid));
                        map.put("uid",String.valueOf(MyApplication.user.getUid()));
                    return map;
                }
            };
                MyApplication.mQueue.add(request);


            }
        });
        builder.create().show();
    }

}
