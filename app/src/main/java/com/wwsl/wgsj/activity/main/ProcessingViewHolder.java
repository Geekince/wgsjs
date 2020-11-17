package com.wwsl.wgsj.activity.main;

import android.content.Context;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.alibaba.fastjson.JSON;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.adapter.maodou.TaskProcessAdapter;
import com.wwsl.wgsj.bean.maodou.NetTaskProcessBean;
import com.wwsl.wgsj.http.HttpCallback;
import com.wwsl.wgsj.http.HttpUtil;
import com.wwsl.wgsj.utils.CommonUtil;
import com.wwsl.wgsj.utils.ToastUtil;
import com.wwsl.wgsj.views.AbsViewHolder;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ProcessingViewHolder extends AbsViewHolder {

    private SwipeRecyclerView        recycler;
    private SmartRefreshLayout       root;
    private TaskProcessAdapter       adapter;
    private List<NetTaskProcessBean> data;


    public ProcessingViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_processing;
    }



    @Override
    public void init() {
        recycler = (SwipeRecyclerView) findViewById(R.id.recycler);
        root = (SmartRefreshLayout) findViewById(R.id.root);

        root.setRefreshHeader(new ClassicsHeader(mContext));
        root.setOnRefreshListener(this::refreshData);
        //        root.setOnLoadMoreListener(this::loadMoreData);

        data = new ArrayList<>();
        adapter = new TaskProcessAdapter(new ArrayList<>());
        recycler.setLayoutManager(new LinearLayoutManager(mContext));
        recycler.setAdapter(adapter);
        recycler.useDefaultLoadMore();
        adapter.setEmptyView(CommonUtil.getEmptyView("暂无进度记录", mContext, root));
        root.autoRefresh();
    }


    private void refreshData(RefreshLayout refreshLayout) {
        loadData();
    }

    public void loadData() {
        HttpUtil.getProMwTask(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 200) {
                    data.clear();
                    List<NetTaskProcessBean> netTaskProcessBeans = JSON.parseArray(Arrays.toString(info), NetTaskProcessBean.class);
                    data.addAll(netTaskProcessBeans);
                    adapter.setNewInstance(netTaskProcessBeans);
                } else {
                    ToastUtil.show(msg);
                }
                root.finishRefresh();
            }

            @Override
            public void onError() {
                root.finishRefresh();
            }
        });
    }
}
