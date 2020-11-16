package com.wwsl.wgsj.activity.maodou;

import android.app.Dialog;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.adapter.maodou.TaskProcessAdapter;
import com.wwsl.wgsj.base.BaseFragment;
import com.wwsl.wgsj.bean.maodou.NetTaskProcessBean;
import com.wwsl.wgsj.http.HttpCallback;
import com.wwsl.wgsj.http.HttpUtil;
import com.wwsl.wgsj.utils.CommonUtil;
import com.wwsl.wgsj.utils.DialogUtil;
import com.wwsl.wgsj.utils.StringUtil;
import com.wwsl.wgsj.utils.ToastUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author :
 * @date : 2020/7/8 18:45
 * @description : 历史任务
 */
public class HistoryTaskFragment extends BaseFragment {

    private SwipeRecyclerView recycler;
    private TaskProcessAdapter adapter;
    private SmartRefreshLayout root;
    private List<NetTaskProcessBean> data;

    public static HistoryTaskFragment newInstance() {
        return new HistoryTaskFragment();
    }


    @Override
    protected int setLayoutId() {
        return R.layout.fragment_history_task;
    }

    @Override
    protected void init() {
        recycler = (SwipeRecyclerView) findViewById(R.id.recycler);
        root = (SmartRefreshLayout) findViewById(R.id.root);
        root.setRefreshHeader(new ClassicsHeader(getContext()));
        root.setOnRefreshListener(this::refreshData);
        data = new ArrayList<>();
        adapter = new TaskProcessAdapter(new ArrayList<>());
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler.setAdapter(adapter);
        recycler.useDefaultLoadMore();
        adapter.setEmptyView(CommonUtil.getEmptyView("暂无历史任务记录", getContext(), root));

        adapter.setOnItemChildClickListener((adapter, view, position) -> {
            if (view.getId() == R.id.txDelete) {
                NetTaskProcessBean bean = (NetTaskProcessBean) adapter.getItem(position);
                if (!StringUtil.isEmpty(bean.getId())) {
                    DialogUtil.showSimpleDialog(getContext(), "确认删除历史任务记录?", new DialogUtil.SimpleCallback() {
                        @Override
                        public void onConfirmClick(Dialog dialog, String content) {
                            dialog.dismiss();
                            showLoadCancelable(false, "删除中");
                            HttpUtil.deleteHistoryItem(bean.getId(), new HttpCallback() {
                                @Override
                                public void onSuccess(int code, String msg, String[] info) {
                                    if (code == 200) {
                                        adapter.removeAt(position);
                                    }
                                    ToastUtil.show(msg);
                                    dismissLoad();
                                }

                                @Override
                                public void onError() {
                                    dismissLoad();
                                }
                            });
                        }
                    });
                }
            }
        });
    }

    private void refreshData(RefreshLayout refreshLayout) {
        loadData();
    }


    @Override
    protected void initialData() {
        root.autoRefresh();
    }

    public void loadData() {
        HttpUtil.getMwTaskHistory(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 200) {
                    data.clear();
                    List<NetTaskProcessBean> netTaskProcessBeans = JSON.parseArray(Arrays.toString(info), NetTaskProcessBean.class);
                    for (int i = 0; i < netTaskProcessBeans.size(); i++) {
                        netTaskProcessBeans.get(i).setHistory(true);
                    }
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
