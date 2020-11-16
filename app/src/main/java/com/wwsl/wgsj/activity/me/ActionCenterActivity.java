package com.wwsl.wgsj.activity.me;

import android.content.Context;
import android.content.Intent;
import android.widget.FrameLayout;

import com.wwsl.wgsj.R;
import com.wwsl.wgsj.activity.common.AbsActivity;
import com.wwsl.wgsj.utils.WordUtil;
import com.wwsl.wgsj.views.ActionCenterViewHolder;

public class ActionCenterActivity extends AbsActivity {

    public static void forward(Context context) {
        context.startActivity(new Intent(context, ActionCenterActivity.class));
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_action_center;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(WordUtil.getString(R.string.action_center));

        ActionCenterViewHolder mainHomeTicketViewHolder = new ActionCenterViewHolder(mContext, (FrameLayout) findViewById(R.id.layoutActionCenter));
        mainHomeTicketViewHolder.addToParent();
        mainHomeTicketViewHolder.loadData();
    }
}
