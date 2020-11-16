package com.wwsl.wgsj.activity.main;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.TextView;

import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.adapter.GridVideoAdapter;
import com.wwsl.wgsj.bean.VideoBean;
import com.wwsl.wgsj.dialog.HometownSetDialog;
import com.wwsl.wgsj.http.HttpConst;
import com.wwsl.wgsj.views.AbsMainViewHolder;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.List;

public class MainGamesViewHolder extends AbsMainViewHolder {
    private SwipeRecyclerView recyclerView;
    private GridVideoAdapter adapter;
    private SmartRefreshLayout mRefreshLayout;
    private TextView tvCity;
    private List<VideoBean> data;
    private int mPage = 1;
    private int videoIndex = HttpConst.VIDEO_TYPE_HOMETOWN << 10;
    private String city = "";
    private int videoType = HttpConst.VIDEO_TYPE_HOMETOWN;
    private final static String TAG = "MainHometownViewHolder";
    private HometownSetDialog hometownSetDialog;

    public MainGamesViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_current_location2;
    }

    @Override
    public void init() {
    }

    @Override
    public void loadData() {
    }

}
