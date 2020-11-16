package com.wwsl.wgsj.activity.live;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.frame.fire.util.LogUtils;
import com.wwsl.wgsj.Constants;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.activity.common.AbsActivity;
import com.wwsl.wgsj.activity.common.SearchActivity;
import com.wwsl.wgsj.bean.LiveBean;
import com.wwsl.wgsj.bean.LiveListBean;
import com.wwsl.wgsj.http.HttpCallback;
import com.wwsl.wgsj.http.HttpConst;
import com.wwsl.wgsj.http.HttpUtil;
import com.wwsl.wgsj.presenter.CheckLivePresenter;
import com.wwsl.wgsj.utils.LiveStorge;
import com.wwsl.wgsj.utils.ToastUtil;
import com.wwsl.wgsj.views.OnVideoTickListener;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import cn.hutool.core.util.StrUtil;

public class MainLiveListActivity extends AbsActivity implements SwipeRecyclerView.LoadMoreListener, SwipeRefreshLayout.OnRefreshListener, OnVideoTickListener {

    private final static String TAG = "MainLiveListActivity";
    private static final float MAX_SCALE = 1f;
    private static final float MIN_SCALE = 0.8f;
    private LiveListAdapter adapter;
    private List<LiveListBean> beans;
    private SwipeRecyclerView scrollView;
    private SwipeRefreshLayout liveFreshLayout;
    private ConstraintLayout noDataLayout;
    private int mPage = 1;
    private CheckLivePresenter mCheckLivePresenter;
    private int curIndex = -1;
    private LinearLayoutManager linearLayoutManager;
    private LiveCutDownTimer liveCutDownTimer;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_live_list;
    }

    @Override
    protected void main(Bundle savedInstanceState) {
        super.main(savedInstanceState);
        beans = new ArrayList<>();
        adapter = new LiveListAdapter(beans);
        scrollView = findViewById(R.id.picker);
        noDataLayout = findViewById(R.id.noDataLayout);
        liveFreshLayout = findViewById(R.id.liveFreshLayout);
        scrollView.useDefaultLoadMore();

        scrollView.setLoadMoreListener(this);
        liveFreshLayout.setOnRefreshListener(this);
        liveCutDownTimer = new LiveCutDownTimer(1000, this);
        initView();

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (beans.size() == 0) {
            loadData(true);
        }


        if (curIndex < adapter.getData().size() && curIndex >= 0) {

            for (int i = 0; i < adapter.getData().size(); i++) {
                if (i == curIndex) {
                    adapter.getData().get(i).setPlay(true);
                    adapter.getData().get(i).setNeedPlay(true);
                }
            }

            adapter.notifyItemChanged(curIndex, LiveListAdapter.PAYLOAD_PLAY);
        }

    }

    private void loadData(boolean isClearAll) {

        HttpUtil.getLiveList(mPage, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {

                if (code != 0) {
                    scrollView.loadMoreFinish(false, true);
                    ToastUtil.show(msg);
                    return;
                }

                List<LiveListBean> temp = new ArrayList<>();
                List<LiveBean> tempLiveBean = new ArrayList<>();
                if (info != null && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    tempLiveBean = JSON.parseArray(obj.getString("list"), LiveBean.class);
                    Iterator<LiveBean> iterator = tempLiveBean.iterator();
                    while (iterator.hasNext()) {
                        LiveBean bean = iterator.next();
                        //排除没有再直播的数据
                        if (StrUtil.isEmpty(bean.getIslive()) || !"1".equals(bean.getIslive())) {
                            iterator.remove();
                        } else {
                            LiveListBean liveListBean = new LiveListBean(bean.getUid(), bean, false, false);
                            if (isClearAll) {
                                temp.add(liveListBean);
                            } else if (!beans.contains(liveListBean)) {
                                // 加载更多 去重
                                temp.add(liveListBean);
                            }
                        }
                    }
                }

                if (temp.size() > 0) {
                    LiveStorge.getInstance().put(Constants.LIVE_HOME, tempLiveBean);
                    if (isClearAll) {
                        //刷新
                        beans.clear();
                        //默认播放第一个
                        liveCutDownTimer.start();
//                        temp.get(0).setNeedPlay(true);
//                        temp.get(0).setPlay(true);
                        beans.addAll(temp);
                        adapter.setNewInstance(temp);
                    } else {
                        beans.addAll(temp);
                        adapter.addData(temp);
                    }
                } else {
                    if (beans.size() == 0) {
                        noDataLayout.setVisibility(View.VISIBLE);
                    } else {
                        if (!isClearAll) {
                            scrollView.scrollToPosition(beans.size() - 1);
                        }
                    }
                    ToastUtil.show(msg);
                }

                if (beans.size() % HttpConst.ITEM_COUNT != 0) {
                    mPage--;
                }

                liveFreshLayout.setRefreshing(false);
                scrollView.loadMoreFinish(false, true);
            }
        });
    }

    private void initView() {

        findViewById(R.id.btnSearch).setOnClickListener((v) -> {
            SearchActivity.forward(mContext);
        });

        PagerSnapHelper pagerSnapHelper = new PagerSnapHelper();
        pagerSnapHelper.attachToRecyclerView(scrollView);
        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        scrollView.setLayoutManager(linearLayoutManager);
        scrollView.setAdapter(adapter);
        scrollView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int childCount = recyclerView.getChildCount();
                for (int i = 0; i < childCount; i++) {
                    View child = recyclerView.getChildAt(i);
                    int left = child.getLeft();
                    int paddingStart = recyclerView.getPaddingStart();
                    // 遍历recyclerView子项，以中间项左侧偏移量为基准进行缩放
                    float bl = Math.min(1, Math.abs(left - paddingStart) * 1f / child.getWidth());
                    float scale = MAX_SCALE - bl * (MAX_SCALE - MIN_SCALE);
                    child.setScaleY(scale);
                }
            }

            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState) {
                    /*正在拖拽*/
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        break;
                    /*滑动停止*/
                    case RecyclerView.SCROLL_STATE_IDLE:
                        //停止 之前正在播放的item
                        if (null == adapter) return;
                        int position = linearLayoutManager.findFirstVisibleItemPosition();
                        if (position < adapter.getData().size() && position >= 0) {
                            for (int i = 0; i < adapter.getData().size(); i++) {
                                if (i != position) {
                                    adapter.getData().get(i).setNeedPlay(false);
                                }
                            }
                        }

                        if (curIndex >= 0) {
                            adapter.notifyItemChanged(curIndex, LiveListAdapter.PAYLOAD_PLAY);
                        }

                        //静止x秒后开始播放
                        if (liveCutDownTimer.isTicking()) {
                            liveCutDownTimer.cancel();
                            liveCutDownTimer.start();
                        } else {
                            liveCutDownTimer.start();
                        }

                        break;
                    /*惯性滑动中*/
                    case RecyclerView.SCROLL_STATE_SETTLING:
                        break;
                }
            }
        });


        scrollView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                //加载第一个view
                if (scrollView.getChildCount() == 1) {
                    LogUtils.e(TAG, "onChildViewAttachedToWindow: ");
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {

            }
        });


        adapter.setOnItemClickListener((baseAdapter, view, position) -> {
                    if (curIndex >= 0) {
                        adapter.getData().get(curIndex).setNeedPlay(false);
                        adapter.notifyItemChanged(curIndex, LiveListAdapter.PAYLOAD_PLAY);
                    }
                    watchLive(Objects.requireNonNull(adapter.getItem(position).getLiveBean()), Constants.LIVE_ALL, position);
                }
        );
    }

    private void playCurrentLive() {
        int position = linearLayoutManager.findFirstVisibleItemPosition();
        if (curIndex == position) return;
        if (position < adapter.getData().size() && position >= 0) {
            for (int i = 0; i < adapter.getData().size(); i++) {
                if (i == position) {
                    adapter.getData().get(i).setPlay(true);
                    adapter.getData().get(i).setNeedPlay(true);
                }
            }

            adapter.notifyItemChanged(position, LiveListAdapter.PAYLOAD_PLAY);

            curIndex = position;
        }

    }

    @Override
    protected void onDestroy() {

        if (mCheckLivePresenter != null) {
            mCheckLivePresenter.cancel();
        }

        if (adapter != null) {
            adapter.release();
            adapter = null;
            scrollView = null;
        }


        HttpUtil.cancel(HttpConst.GET_LIVING);
        super.onDestroy();
    }


    public void watchLive(LiveBean liveBean, String key, int position) {
        if (liveBean.getIslive() != null && liveBean.getIslive().equals("1")) {
            if (mCheckLivePresenter == null) {
                mCheckLivePresenter = new CheckLivePresenter(mContext);
            }
            mCheckLivePresenter.watchLive(liveBean, key, position);
        } else {
            ToastUtil.show("直播已经结束!!");
            adapter.remove(position);
        }
    }

    @Override
    public void onLoadMore() {
        mPage++;
        loadData(false);
    }

    @Override
    public void onRefresh() {
        mPage = 1;
        loadData(true);
    }


    @Override
    public void onTick(long millisUntilFinished) {

    }

    @Override
    public void onFinish() {
        playCurrentLive();
    }


}
