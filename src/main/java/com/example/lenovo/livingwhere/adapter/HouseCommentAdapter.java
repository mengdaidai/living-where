package com.example.lenovo.livingwhere.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.example.lenovo.livingwhere.activity.BigPictureActivity;
import com.example.lenovo.livingwhere.util.BitmapCache;
import com.example.lenovo.livingwhere.entity.CommentObj;
import com.example.lenovo.livingwhere.activity.MainActivity;
import com.example.lenovo.livingwhere.R;
import com.example.lenovo.livingwhere.util.MyApplication;
import com.example.lenovo.livingwhere.util.URI;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * 查看某房子所有评论的适配器
 */
 class HouseCommentViewHolder {
    public TextView text_message, text_nickname;//评论内容，昵称
    public ImageView image_head;//显示头像
    public ImageView[] commentPics;//评论的图片们~

}

public class HouseCommentAdapter extends BaseAdapter {
    private LayoutInflater mInflater = null;
    public List<CommentObj> commentInfo = null;
    ImageLoader imageLoader;
    Gson gson = new Gson();
    Context context;


    public HouseCommentAdapter(Context context, List<CommentObj> commentInfo) {
        super();
        mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.commentInfo = commentInfo;
        imageLoader = new ImageLoader(MyApplication.mQueue, new BitmapCache());
        this.context = context;
    }

    @Override
    public int getCount() {
        return commentInfo.size();
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
        final CommentObj info = commentInfo.get(position);
        HouseCommentViewHolder holder = null;
        if (convertView == null) {
            holder = new HouseCommentViewHolder();
            convertView = mInflater.inflate(R.layout.item_house_comment, null);
            holder.commentPics = new ImageView[4];
            holder.commentPics[0] = (ImageView) convertView.findViewById(R.id.house_comment_pic1);
            holder.commentPics[1] = (ImageView) convertView.findViewById(R.id.house_comment_pic2);
            holder.commentPics[2] = (ImageView) convertView.findViewById(R.id.house_comment_pic3);
            holder.commentPics[3] = (ImageView) convertView.findViewById(R.id.house_comment_pic4);
            holder.text_message = (TextView) convertView.findViewById(R.id.house_comment_message);
            holder.text_nickname = (TextView) convertView.findViewById(R.id.house_comment_nickname);
            holder.image_head = (ImageView)convertView.findViewById(R.id.house_comment_pic_head);

            convertView.setTag(holder);
        } else {
            holder = (HouseCommentViewHolder) convertView.getTag();
        }
        for(int i = 0;i<4;i++)
            holder.commentPics[i].setVisibility(View.VISIBLE);
        // 进行数据设置
        ImageLoader.ImageListener listener = ImageLoader.getImageListener(holder.image_head,
                R.drawable.recommend_house_default, R.drawable.recommend_house_failed);
        imageLoader.get(URI.HeadPic+info.getHeadPic(),listener,200,200);
        final List<String> pics = gson.fromJson(info.getPictures(),new TypeToken<List<String>>(){}.getType());
        for(int i = 0;i<pics.size();i++){
            ImageLoader.ImageListener mListener = ImageLoader.getImageListener(holder.commentPics[i],
                    R.drawable.recommend_house_default, R.drawable.recommend_house_failed);
            imageLoader.get(URI.CommentsPic+pics.get(i),mListener,200,200);
            holder.commentPics[i].setVisibility(View.VISIBLE);
            holder.commentPics[i].setTag(i);
            holder.commentPics[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, BigPictureActivity.class);
                    intent.putExtra("type", 2);
                    intent.putExtra("url",pics.get((int)v.getTag()) );
                    intent.putExtra("from",3);
                    context.startActivity(intent);
                }
            });
        }
        //这里数据有些差错，完了再改
        holder.text_message.setText(info.getMessage());
        holder.text_nickname.setText(info.getNickname());



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
