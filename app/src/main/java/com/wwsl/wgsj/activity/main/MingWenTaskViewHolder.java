package com.wwsl.wgsj.activity.main;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.lxj.xpopup.XPopup;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.wwsl.wgsj.AppConfig;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.adapter.maodou.MwTaskAdapter;
import com.wwsl.wgsj.bean.maodou.NetMwTaskBean;
import com.wwsl.wgsj.dialog.MwDetailDialog;
import com.wwsl.wgsj.http.HttpCallback;
import com.wwsl.wgsj.http.HttpUtil;
import com.wwsl.wgsj.utils.DialogUtil;
import com.wwsl.wgsj.utils.ToastUtil;
import com.wwsl.wgsj.views.AbsViewHolder;
import com.wwsl.wgsj.views.dialog.InputPwdDialog;
import com.wwsl.wgsj.views.dialog.OnDialogCallBackListener;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MingWenTaskViewHolder extends AbsViewHolder {
    private SwipeRecyclerView   recycler;
    private SmartRefreshLayout  refreshLayout;
    private MwTaskAdapter       adapter;
    private List<NetMwTaskBean> data;

    public MingWenTaskViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ming_wen_task;
    }



    @Override
    public void init() {
        recycler = (SwipeRecyclerView) findViewById(R.id.recycler);
        refreshLayout = (SmartRefreshLayout) findViewById(R.id.refreshLayout);
        refreshLayout.setRefreshHeader(new ClassicsHeader(mContext));
        refreshLayout.setOnRefreshListener(this::refreshData);
        data = new ArrayList<>();
        adapter = new MwTaskAdapter(new ArrayList<>());
        recycler.setLayoutManager(new LinearLayoutManager(mContext));
        recycler.setAdapter(adapter);
        adapter.setOnItemChildClickListener(new OnItemChildClickListener() {
            @Override
            public void onItemChildClick(@NonNull BaseQuickAdapter adapter, @NonNull View view, int position) {
                new XPopup.Builder(mContext)
                    .hasShadowBg(true)
                    .customAnimator(new DialogUtil.DialogAnimator())
                    .asCustom(new InputPwdDialog(mContext, String.format("将要扣除%s令牌", data.get(position).getPrice()), new OnDialogCallBackListener() {
                        @Override
                        public void onDialogViewClick(View view, Object object) {
                            String pwd = (String) object;
                            buyMwTask(position, pwd);
                        }
                    }))
                    .show();
            }
        });
        adapter.setOnItemClickListener((adapter, view, position) -> new XPopup.Builder(mContext)
            .hasShadowBg(false)
            .customAnimator(new DialogUtil.DialogAnimator())
            .asCustom(new MwDetailDialog(Objects.requireNonNull(mContext), data.get(position)))
            .show());

        refreshLayout.autoRefresh();
    }

    private void refreshData(RefreshLayout refreshLayout) {
        loadData();
    }

    private void buyMwTask(int position, String pwd) {

        HttpUtil.buyMwTask(AppConfig.getInstance().getUserBean().getMobile(), data.get(position).getId(), pwd, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 200) {
                    //购买成功
                    String i = String.valueOf(Integer.parseInt(adapter.getData().get(position).getProductCount()) + 1);
                    adapter.getData().get(position).setProductCount(i);
                    adapter.notifyItemChanged(position, MwTaskAdapter.PAYLOAD_BUY_MW);
                }

                ToastUtil.show(msg);
            }
        });
    }

    public void loadData() {
        HttpUtil.getAllMwTask(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 200) {
                    data.clear();
                    List<NetMwTaskBean> netMwTaskBeans = JSON.parseArray(Arrays.toString(info), NetMwTaskBean.class);
                    data.addAll(netMwTaskBeans);
                    adapter.setNewInstance(netMwTaskBeans);
                }
                refreshLayout.finishRefresh();
            }

            @Override
            public void onError() {
                refreshLayout.finishRefresh();
            }
        });
    }
}
