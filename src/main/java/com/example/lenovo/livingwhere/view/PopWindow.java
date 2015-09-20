package com.example.lenovo.livingwhere.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.lenovo.livingwhere.R;


public class PopWindow extends PopupWindow implements OnClickListener {
    private View contentView;
    private TextView tv_View;
    private Context context;
    private TextView tv_1;
    private TextView tv_2;

    public PopWindow(final Context context, TextView view) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        contentView = inflater.inflate(R.layout.popup_dialog, null);
        tv_1 = (TextView) contentView.findViewById(R.id.tv_1);
        tv_2 = (TextView) contentView.findViewById(R.id.tv_2);
        tv_1.setOnClickListener(this);
        tv_2.setOnClickListener(this);
        tv_View = view;
        // 设置SelectPicPopupWindow的View
        this.setContentView(contentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(996699);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(new BitmapDrawable());
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);

    }

    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            this.showAtLocation(parent, Gravity.CENTER, 0, 0);
            // this.showAsDropDown(parent, parent.getLayoutParams().width / 2,
            // 18);

        } else {
            this.dismiss();
        }
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        if (v.getId() == R.id.tv_1) {
            tv_View.setText("个人住房");
        } else {
            tv_View.setText("宾馆酒店");
        }
        this.dismiss();
    }
}
