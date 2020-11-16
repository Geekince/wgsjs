package com.wwsl.wgsj.activity.me;

import android.content.Context;
import android.content.Intent;
import android.widget.FrameLayout;

import com.wwsl.wgsj.R;
import com.wwsl.wgsj.activity.common.AbsActivity;
import com.wwsl.wgsj.utils.WordUtil;
import com.wwsl.wgsj.views.HelpCenterViewHolder;

public class HelpCenterActivity extends AbsActivity {

    public static void forward(Context context) {
        context.startActivity(new Intent(context, HelpCenterActivity.class));
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activitity_help_center;
    }


    @Override
    protected void main() {
        setTitle(WordUtil.getString(R.string.help_center));
        HelpCenterViewHolder mainListViewHolder = new HelpCenterViewHolder(mContext, (FrameLayout) findViewById(R.id.layoutHelpCenter));
//        mainListViewHolder.hideTop();
        mainListViewHolder.addToParent();
        mainListViewHolder.loadData();
    }
}
