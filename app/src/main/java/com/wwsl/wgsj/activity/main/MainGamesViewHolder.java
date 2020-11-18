package com.wwsl.wgsj.activity.main;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import androidx.viewpager.widget.ViewPager;
import com.flyco.tablayout.SlidingTabLayout;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.adapter.ViewPagerAdapter;
import com.wwsl.wgsj.views.AbsMainViewHolder;
import com.wwsl.wgsj.views.AbsViewHolder;
import java.util.ArrayList;
import java.util.List;

public class MainGamesViewHolder extends AbsMainViewHolder {
    private ProcessingViewHolder  processingFragment;
    private TodayViewHolder       todayFragment;
    private MingWenTaskViewHolder mingWenTaskFragment;
    private HistoryViewHolder     historyTaskFragment;
    private SlidingTabLayout      tabLayout;
    private ViewPager             viewPager;
    private ViewPagerAdapter      viewPagerAdapter;

    private ArrayList<AbsViewHolder> viewList;

    public MainGamesViewHolder(Context context, ViewGroup parentView, Activity activity) {
        super(context, parentView, activity);
    }

    @Override protected int getLayoutId() {
        return R.layout.fragment_current_location2;
    }

    @Override public void init() {
        initView();
    }

    private void initView() {
        tabLayout = (SlidingTabLayout) findViewById(R.id.game_tablayout);
        viewPager = (ViewPager) findViewById(R.id.game_vp);

        String[] titles = new String[4];

        viewList = new ArrayList<>();
        titles[0] = "我的任务";
        titles[1] = "今日进度";
        titles[2] = "任务领取";
        titles[3] = "历史任务";

        List<View> views = new ArrayList<>(4);
        processingFragment = new ProcessingViewHolder(mContext, viewPager);
        viewList.add(processingFragment);
        todayFragment = new TodayViewHolder(mContext, viewPager,mainActivity);
        viewList.add(todayFragment);
        mingWenTaskFragment = new MingWenTaskViewHolder(mContext, viewPager);
        viewList.add(mingWenTaskFragment);
        historyTaskFragment = new HistoryViewHolder(mContext, viewPager);
        viewList.add(historyTaskFragment);

        views.add(viewList.get(0).getContentView());
        views.add(viewList.get(1).getContentView());
        views.add(viewList.get(2).getContentView());
        views.add(viewList.get(3).getContentView());

        viewPagerAdapter = new ViewPagerAdapter(views);

        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setViewPager(viewPager, titles);
    }

    public void adRewardBack(int times){
        if (todayFragment!= null){
            todayFragment.adRewardBack(times);
        }
    }
}