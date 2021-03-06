package com.wwsl.wgsj.activity.me;

import android.content.Context;
import android.content.Intent;

import com.wwsl.wgsj.Constants;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.activity.common.AbsActivity;
import com.wwsl.wgsj.utils.WordUtil;

public class UserVideoListActivity extends AbsActivity {
    private String userId;

    public static void forward(Context context, String userId) {
        Intent intent = new Intent(context, UserVideoListActivity.class);
        intent.putExtra(Constants.UID, userId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_trend;
    }

    @Override
    protected void main() {
        super.main();
        setTitle(WordUtil.getString(R.string.video));
        userId = getIntent().getStringExtra(Constants.UID);


    }
}
