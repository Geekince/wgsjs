package com.wwsl.wgsj.activity.maodou;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import androidx.fragment.app.Fragment;

import com.flyco.tablayout.SlidingTabLayout;
import com.wwsl.wgsj.HtmlConfig;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.activity.common.WebViewActivity;
import com.wwsl.wgsj.adapter.CommPagerAdapter;
import com.wwsl.wgsj.base.BaseActivity;
import com.wwsl.wgsj.custom.SimpleViewPager;

import java.util.ArrayList;
import java.util.Arrays;

public class MaoDouFoundationActivity extends BaseActivity {
    private ArrayList<Fragment> fragments;
    private ProcessingFragment processingFragment;
    private TodayFragment todayFragment;
    private MingWenTaskFragment mingWenTaskFragment;
    private HistoryTaskFragment historyTaskFragment;
    private SlidingTabLayout tabLayout;
    private SimpleViewPager viewPager;
    private CommPagerAdapter pagerAdapter;

    @Override
    protected int setLayoutId() {
        return R.layout.activity_mao_dou_fundation;
    }

    @Override
    protected void init() {
        initView();
    }

    public void backClick(View view) {
        finish();
    }

    public void showRule(View view) {
        WebViewActivity.forward(this, HtmlConfig.WEB_LINK_MD_RULE);
    }

    public static void forward(Context context) {
        Intent intent = new Intent(context, MaoDouFoundationActivity.class);
        context.startActivity(intent);
    }


    private void initView() {
        tabLayout = (SlidingTabLayout) findViewById(R.id.tabLayout);
        viewPager = (SimpleViewPager) findViewById(R.id.viewpager);


        fragments = new ArrayList<>();
        String[] titles = new String[4];

        titles[0] = "我的任务";
        processingFragment = ProcessingFragment.newInstance();
        fragments.add(processingFragment);

        titles[1] = "今日进度";
        todayFragment = TodayFragment.newInstance();
        fragments.add(todayFragment);

        titles[2] = "任务领取";
        mingWenTaskFragment = MingWenTaskFragment.newInstance();
        fragments.add(mingWenTaskFragment);

        titles[3] = "历史任务";
        historyTaskFragment = HistoryTaskFragment.newInstance();
        fragments.add(historyTaskFragment);

        pagerAdapter = new CommPagerAdapter(getSupportFragmentManager(), fragments, Arrays.asList(titles));
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(pagerAdapter);

        tabLayout.setViewPager(viewPager, titles);
        tabLayout.setCurrentTab(0);
    }
}
