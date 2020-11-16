package com.wwsl.wgsj.activity.maodou;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.wwsl.wgsj.R;
import com.wwsl.wgsj.adapter.maodou.PackageDetailAdapter;
import com.wwsl.wgsj.base.BaseActivity;
import com.wwsl.wgsj.bean.maodou.PackageDetailBean;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MdPackageActivity extends BaseActivity {

    private SwipeRecyclerView recycler;
    private PackageDetailAdapter adapter;
    private List<PackageDetailBean> data;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_md_package;
    }

    @Override
    protected void init() {
        initView();
        data = new ArrayList<>();

        data.add(PackageDetailBean.builder().type(0).title("购买令牌 x1").isIncome(false).num("199.0").per("元宝").time("2020-05-07 15:42").build());
        data.add(PackageDetailBean.builder().type(1).title("出售令牌 x1").isIncome(true).num("199.0").per("元宝").time("2020-05-07 15:42").build());
        data.add(PackageDetailBean.builder().type(1).title("购买令牌 x1").isIncome(true).num("199.0").per("令牌").time("2020-05-07 15:42").build());
        data.add(PackageDetailBean.builder().type(0).title("购买令牌 x1").isIncome(true).num("199.0").per("元宝").time("2020-05-07 15:42").build());
        data.add(PackageDetailBean.builder().type(1).title("购买令牌 x1").isIncome(true).num("199.0").per("元宝").time("2020-05-07 15:42").build());
        data.add(PackageDetailBean.builder().type(0).title("购买令牌 x1").isIncome(true).num("199.0").per("元宝").time("2020-05-07 15:42").build());
        adapter = new PackageDetailAdapter(data);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.useDefaultLoadMore();
        recycler.setAdapter(adapter);
    }

    public void backClick(View view) {
        finish();
    }

    private void initView() {
        recycler = (SwipeRecyclerView) findViewById(R.id.recycler);
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, MdPackageActivity.class);
        context.startActivity(intent);
    }
}
