package com.wwsl.wgsj.activity.me;

import android.content.Context;
import android.content.Intent;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.alibaba.fastjson.JSON;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.activity.common.AbsActivity;
import com.wwsl.wgsj.adapter.GiftGainAdapter;
import com.wwsl.wgsj.bean.GiftDetailBean;
import com.wwsl.wgsj.http.HttpCallback;
import com.wwsl.wgsj.http.HttpUtil;
import com.wwsl.wgsj.utils.ToastUtil;
import com.wwsl.wgsj.utils.WordUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GainGiftListActivity extends AbsActivity implements SwipeRecyclerView.LoadMoreListener {

    private SwipeRecyclerView gainRecycle;
    private GiftGainAdapter adapter;

    private List<GiftDetailBean> gainShowBeans;
    private String stream;
    private int mPage = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_gain_gift_list;
    }

    @Override
    protected void main() {
        stream = getIntent().getStringExtra("stream");
        gainRecycle = findViewById(R.id.gainRecycle);
        gainShowBeans = new ArrayList<>();
        adapter = new GiftGainAdapter(gainShowBeans);

        gainRecycle.setLayoutManager(new LinearLayoutManager(this));
        gainRecycle.addItemDecoration(new DefaultItemDecoration(ContextCompat.getColor(this, R.color.divider_color)));
        gainRecycle.useDefaultLoadMore(); // 使用默认的加载更多的View。
        gainRecycle.setLoadMoreListener(this); // 加载更多的监听。
        gainRecycle.setAdapter(adapter);
        adapter.setEmptyView(R.layout.view_no_data_default);
    }

    @Override
    protected void onResume() {
        if (gainShowBeans.isEmpty()) {
            getGiftData();
        }
        super.onResume();
    }

    private void getGiftData() {
        HttpUtil.getGiftDetail(stream, mPage, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                showData(code, msg, info);
            }
        });
    }

    private void showData(int code, String msg, String[] info) {
        if (code != 0) {
            ToastUtil.show(msg);
            mPage--;
            return;
        }
        List<GiftDetailBean> list = new ArrayList<>();
        if (info != null) {
            list = JSON.parseArray(Arrays.toString(info), GiftDetailBean.class);
            if (list == null) {
                return;
            }
            if (list.size() > 0) {
                adapter.addData(list);
            } else {
                ToastUtil.show(WordUtil.getString(R.string.no_more_data));
                mPage--;
            }
        } else {
            ToastUtil.show(WordUtil.getString(R.string.no_more_data));
            mPage--;
        }

        gainRecycle.loadMoreFinish(false, list.size() == 0);
    }


    public static void forward(Context context, String stream) {
        Intent intent = new Intent(context, GainGiftListActivity.class);
        intent.putExtra("stream", stream);
        context.startActivity(intent);
    }


    @Override
    public void onLoadMore() {
        mPage++;
        getGiftData();
    }
}
