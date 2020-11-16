package com.wwsl.wgsj.activity.main;

import android.content.Context;
import android.view.ViewGroup;

import com.wwsl.wgsj.R;
import com.wwsl.wgsj.views.AbsMainViewHolder;

/**
 * Created by cxf on 2018/9/22.
 * MainActivity 首页
 */

public class MainTestViewHolder extends AbsMainViewHolder {
    private final static String TAG = "MainTestViewHolder";


    public MainTestViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    public void init() {

    }

}
