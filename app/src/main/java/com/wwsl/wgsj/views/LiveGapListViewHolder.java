package com.wwsl.wgsj.views;

import android.content.Context;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.activity.live.LiveActivity;
import com.wwsl.wgsj.adapter.LiveGapListAdapter;
import com.wwsl.wgsj.bean.UserBean;
import com.wwsl.wgsj.http.HttpCallback;
import com.wwsl.wgsj.http.HttpConst;
import com.wwsl.wgsj.http.HttpUtil;
import com.wwsl.wgsj.interfaces.LifeCycleAdapter;
import com.wwsl.wgsj.interfaces.OnItemClickListener;
import com.wwsl.wgsj.utils.DialogUtil;
import com.wwsl.wgsj.utils.ToastUtil;

import java.util.List;

public class LiveGapListViewHolder extends AbsLivePageViewHolder implements OnItemClickListener<UserBean> {
    private String mLiveUid;
    private RecyclerView mRecyclerView;
    private LiveGapListAdapter mLiveGapListAdapter;
    private HttpCallback mHttpCallback;

    public LiveGapListViewHolder(Context context, ViewGroup parentView, String liveUid) {
        super(context, parentView);
        mLiveUid = liveUid;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.view_live_gap_list;
    }

    @Override
    public void init() {
        super.init();
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mHttpCallback = new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info.length > 0) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    List<UserBean> list = JSON.parseArray(obj.getString("list"), UserBean.class);
                    if (mLiveGapListAdapter == null) {
                        mLiveGapListAdapter = new LiveGapListAdapter(mContext, list);
                        mLiveGapListAdapter.setOnItemClickListener(LiveGapListViewHolder.this);
                        mRecyclerView.setAdapter(mLiveGapListAdapter);
                    } else {
                        mLiveGapListAdapter.setList(list);
                    }
                }
            }
        };
        mLifeCycleListener = new LifeCycleAdapter() {
            @Override
            public void onDestroy() {
                HttpUtil.cancel(HttpConst.GET_GAP_LIST);
                HttpUtil.cancel(HttpConst.CANCEL_GAP);
            }
        };
    }

    @Override
    public void loadData() {
        HttpUtil.getGapList(mLiveUid, mHttpCallback);
    }

    @Override
    public void onHide() {
        if (mLiveGapListAdapter != null) {
            mLiveGapListAdapter.clear();
        }
    }

    @Override
    public void onItemClick(final UserBean bean, int position) {
        DialogUtil.showStringArrayDialog(mContext, new Integer[]{
                R.string.live_setting_gap_cancel}, new DialogUtil.StringArrayDialogCallback() {
            @Override
            public void onItemClick(String text, int tag) {
                if (tag == R.string.live_setting_gap_cancel) {
                    HttpUtil.cancelGap(mLiveUid, bean.getId(), new HttpCallback() {
                        @Override
                        public void onSuccess(int code, String msg, String[] info) {
                            ToastUtil.show(msg);
                            if (code == 0) {
                                if (mLiveGapListAdapter != null) {
                                    mLiveGapListAdapter.removeItem(bean.getId());
                                }
                                ((LiveActivity) mContext).cancelShutUp(bean.getId(), bean.getUsername());
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    public void release() {
        if (mLiveGapListAdapter != null) {
            mLiveGapListAdapter.release();
        }
        mLiveGapListAdapter = null;
        super.release();
    }
}
