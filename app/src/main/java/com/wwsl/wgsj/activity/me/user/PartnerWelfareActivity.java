package com.wwsl.wgsj.activity.me.user;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.alibaba.fastjson.JSON;
import com.lxj.xpopup.XPopup;
import com.wwsl.wgsj.AppConfig;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.activity.UserPartnerActivity;
import com.wwsl.wgsj.adapter.PartnerWelfareAdapter;
import com.wwsl.wgsj.base.BaseActivity;
import com.wwsl.wgsj.bean.PartnerWelfareBean;
import com.wwsl.wgsj.bean.PartnerWelfareDetailBean;
import com.wwsl.wgsj.dialog.PartnerWelfareDetailDialog;
import com.wwsl.wgsj.http.HttpCallback;
import com.wwsl.wgsj.http.HttpUtil;
import com.wwsl.wgsj.utils.CommonUtil;
import com.wwsl.wgsj.utils.DialogUtil;
import com.wwsl.wgsj.utils.ToastUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 合伙人福利
 */
public class PartnerWelfareActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, SwipeRecyclerView.LoadMoreListener {

    @BindView(R.id.recyclerView)
    SwipeRecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.iv_empty_view)
    ImageView iv_empty_view;
    @BindView(R.id.tv_hint)
    TextView tv_hint;
    @BindView(R.id.tv_apply)
    TextView tv_apply;

    private List<PartnerWelfareBean> listData = new ArrayList<>();
    private int page = 1;
    private PartnerWelfareAdapter adapter;

    public void backClick(View view) {
        finish();
    }

    @OnClick(R.id.tv_apply)
    public void clickView() {
        Intent intent = new Intent(this, UserPartnerActivity.class);
        startActivityForResult(intent, 666);
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_partner_welfare;
    }

    @Override
    protected void init() {
        if ("0".equals(AppConfig.getInstance().getUserBean().getPartnerId())) {
            iv_empty_view.setVisibility(View.VISIBLE);
            tv_hint.setVisibility(View.VISIBLE);
            tv_apply.setVisibility(View.VISIBLE);
            refreshLayout.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        } else {
            iv_empty_view.setVisibility(View.GONE);
            tv_hint.setVisibility(View.GONE);
            tv_apply.setVisibility(View.GONE);
            refreshLayout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        refreshLayout.setOnRefreshListener(this);
        adapter = new PartnerWelfareAdapter(listData);
        recyclerView.useDefaultLoadMore();
        recyclerView.setLoadMoreListener(this);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener((adapter1, view, position) -> HttpUtil.partnerWelfareDetail(adapter.getItem(position).id, "2020", new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code==0){
                    List<PartnerWelfareDetailBean> detailBeans = JSON.parseArray(Arrays.toString(info), PartnerWelfareDetailBean.class);
                    new XPopup.Builder(PartnerWelfareActivity.this)
                            .hasShadowBg(false)
                            .dismissOnTouchOutside(true)
                            .customAnimator(new DialogUtil.DialogAnimator())
                            .asCustom(new PartnerWelfareDetailDialog(PartnerWelfareActivity.this,detailBeans))
                            .show();
                }else {
                    ToastUtil.show(msg);
                }
            }
        }));

        adapter.setEmptyView(CommonUtil.getEmptyView(null, this, refreshLayout));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }

    private void loadData() {
        HttpUtil.partnerWelfare(page, 10, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0) {
                    List<PartnerWelfareBean> welfareBeans = JSON.parseArray(Arrays.toString(info), PartnerWelfareBean.class);
                    if (page == 1) {
                        listData.clear();
                    }
                    listData.addAll(welfareBeans);
                    adapter.notifyDataSetChanged();
                } else {
                    ToastUtil.show(msg);
                }

            }
        });
        refreshLayout.setRefreshing(false);
        recyclerView.loadMoreFinish(false, false);
    }

    public static void invoke(Activity activity) {
        Intent intent = new Intent(activity, PartnerWelfareActivity.class);
        activity.startActivity(intent);
    }

    //下拉刷新
    @Override
    public void onRefresh() {
        page = 1;
        loadData();
    }

    //上拉加载
    @Override
    public void onLoadMore() {
        page++;
        loadData();
    }
}
