package com.wwsl.wgsj.activity.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.frame.fire.util.LogUtils;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.wwsl.wgsj.AppConfig;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.activity.MainActivity;
import com.wwsl.wgsj.activity.video.VideoPlayListActivity;
import com.wwsl.wgsj.adapter.GridVideoAdapter;
import com.wwsl.wgsj.bean.VideoBean;
import com.wwsl.wgsj.dialog.HometownSetDialog;
import com.wwsl.wgsj.http.HttpCallback;
import com.wwsl.wgsj.http.HttpConst;
import com.wwsl.wgsj.http.HttpUtil;
import com.wwsl.wgsj.utils.DialogUtil;
import com.wwsl.wgsj.utils.ToastUtil;
import com.wwsl.wgsj.utils.VideoStorage;
import com.wwsl.wgsj.utils.cache.PreloadManager;
import com.wwsl.wgsj.views.AbsMainViewHolder;
import com.wwsl.wgsj.views.dialog.OnDialogCallBackListener;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.hutool.core.util.StrUtil;

public class MainHometownViewHolder extends AbsMainViewHolder {
    private SwipeRecyclerView recyclerView;
    private GridVideoAdapter adapter;
    private SmartRefreshLayout mRefreshLayout;
    private TextView tvCity;
    private List<VideoBean> data;
    private int mPage = 1;
    private int videoIndex = HttpConst.VIDEO_TYPE_HOMETOWN << 10;
    private String city = "";
    private int videoType = HttpConst.VIDEO_TYPE_HOMETOWN;

    public MainHometownViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_current_location;
    }

    @Override
    public void init() {
        city = AppConfig.getInstance().getUserBean().getCity();
        tvCity = (TextView) findViewById(R.id.txMainCity);
        tvCity.setText(city);
        recyclerView = (SwipeRecyclerView) findViewById(R.id.recyclerview);
        mRefreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshlayout);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        data = new ArrayList<>();
        adapter = new GridVideoAdapter(new ArrayList<>());
        adapter.setEmptyView(LayoutInflater.from(mContext).inflate(R.layout.view_no_data_video, null, false));

        adapter.setOnItemClickListener((adapter, view, position) -> {
            VideoPlayListActivity.forward(mContext, AppConfig.getInstance().getUid(), HttpConst.USER_VIDEO_TYPE_HOMETOWN, position);
        });

        recyclerView.setAdapter(adapter);
        mRefreshLayout.setEnableAutoLoadMore(true);//开启自动加载功能（非必须）
        mRefreshLayout.setOnRefreshListener(this::refresh);
        mRefreshLayout.setOnLoadMoreListener(this::loadMore);
        mRefreshLayout.autoRefresh();
    }

    private void loadMore(RefreshLayout refreshLayout) {
        mPage++;
        HttpUtil.getHometownVideoList(videoType, mPage, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info != null && info.length > 0) {
                    List<VideoBean> videoBeans = JSON.parseArray(Arrays.toString(info), VideoBean.class);

                    for (int i = 0; i < videoBeans.size(); i++) {
                        PreloadManager.getInstance(mContext).addPreloadTask(videoBeans.get(i).getVideoUrl(), videoIndex + i);
                    }

                    data.addAll(videoBeans);
                    adapter.addData(videoBeans);
                    mRefreshLayout.finishLoadMore();
                    VideoStorage.getInstance().putVideoList(AppConfig.getInstance().getUid(), HttpConst.USER_VIDEO_TYPE_HOMETOWN, data);
                } else {
                    mRefreshLayout.finishLoadMoreWithNoMoreData();
                    ToastUtil.show(msg);
//                    mRefreshLayout.finishLoadMore(false);
                }
            }

            @Override
            public void onError() {
                mPage = mPage > 0 ? mPage-- : 0;
                mRefreshLayout.finishLoadMore(false);
            }
        });
    }

    private void refresh(RefreshLayout refreshLayout) {
        loadData();
    }


    @Override
    public void loadData() {
        mPage = 1;
        HttpUtil.getHometownVideoList(videoType, mPage, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {

                if (code == 0 && info != null && info.length > 0) {
                    List<VideoBean> videoBeans = JSON.parseArray(Arrays.toString(info), VideoBean.class);
                    for (int i = 0; i < videoBeans.size(); i++) {
                        PreloadManager.getInstance(mContext).addPreloadTask(videoBeans.get(i).getVideoUrl(), videoIndex + i);
                    }
                    data.clear();
                    data.addAll(videoBeans);
                    adapter.setNewInstance(videoBeans);
                    VideoStorage.getInstance().putVideoList(AppConfig.getInstance().getUid(), HttpConst.USER_VIDEO_TYPE_HOMETOWN, data);
                    mRefreshLayout.finishRefresh();
                } else {
                    data.clear();
                    adapter.setNewInstance(new ArrayList<>());
                    mRefreshLayout.finishRefresh();
                }
            }

            @Override
            public void onError() {
                super.onError();
                mRefreshLayout.finishRefresh(false);
            }
        });

    }


    private final static String TAG = "MainHometownViewHolder";

    @Override
    public void onResume() {
        super.onResume();

        String tempCity = AppConfig.getInstance().getUserBean().getCity();

        if (StrUtil.isEmpty(tempCity) || HttpConst.CITY_DEFAULT_STR.equals(tempCity)) {
            //没有设置家乡
            LogUtils.e(TAG, "MainHometownViewHolder - onResume: " + tempCity);
            showSetHometownDialog();
        } else if (!city.equals(tempCity)) {
            //切换了城市
            city = tempCity;
            tvCity.setText(city);
            loadData();
        } else {
            if (data.size() == 0) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(2000);
                            loadData();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                };

            }
        }
    }

    private HometownSetDialog hometownSetDialog;


    public void showSetHometownDialog() {
        if (null == hometownSetDialog) {
            hometownSetDialog = DialogUtil.getHometownSetDialog((MainActivity) mContext, new OnDialogCallBackListener() {
                @Override
                public void onDialogViewClick(View view, Object object) {
                    String content = (String) object;

                    HttpUtil.updateCity(content, new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            if (code == 0) {
                                if (info.length > 0) {
                                    JSONObject obj = JSON.parseObject(info[0]);
                                    ToastUtil.show(obj.getString("msg"));
                                    AppConfig.getInstance().getUserBean().setCity(content);
                                    city = content;
                                    tvCity.setText(city);
                                    loadData();
                                }
                            } else {
                                ToastUtil.show(msg);
                            }
                            hometownSetDialog.dismiss();
                        }
                    });
                }
            });
        }
        hometownSetDialog.show();
    }
}
