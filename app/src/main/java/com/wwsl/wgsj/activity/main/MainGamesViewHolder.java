package com.wwsl.wgsj.activity.main;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import com.alibaba.fastjson.JSON;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemChildClickListener;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.flyco.tablayout.SlidingTabLayout;
import com.frame.fire.util.LogUtils;
import com.mob68.ad.RewardVideoAd;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.wwsl.wgsj.AppConfig;
import com.wwsl.wgsj.Constants;
import com.wwsl.wgsj.HtmlConfig;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.activity.UserHomePageActivity;
import com.wwsl.wgsj.activity.common.WebViewActivity;
import com.wwsl.wgsj.activity.maodou.HistoryTaskFragment;
import com.wwsl.wgsj.activity.maodou.MaoDouFoundationActivity;
import com.wwsl.wgsj.activity.maodou.MingWenTaskFragment;
import com.wwsl.wgsj.activity.maodou.ProcessingFragment;
import com.wwsl.wgsj.activity.maodou.TodayFragment;
import com.wwsl.wgsj.activity.me.WorkViewHolder;
import com.wwsl.wgsj.activity.message.ChatActivity;
import com.wwsl.wgsj.activity.message.MessageSecondActivity;
import com.wwsl.wgsj.activity.message.SysMessageActivity;
import com.wwsl.wgsj.adapter.CommPagerAdapter;
import com.wwsl.wgsj.adapter.MsgShortAdapter;
import com.wwsl.wgsj.adapter.ViewPagerAdapter;
import com.wwsl.wgsj.bean.AdvertiseBean;
import com.wwsl.wgsj.bean.MsgShortBean;
import com.wwsl.wgsj.bean.net.MsgConservationNetBean;
import com.wwsl.wgsj.bean.net.RecommendUserBean;
import com.wwsl.wgsj.custom.SimpleViewPager;
import com.wwsl.wgsj.http.HttpCallback;
import com.wwsl.wgsj.http.HttpUtil;
import com.wwsl.wgsj.im.ImMessageUtil;
import com.wwsl.wgsj.interfaces.CommonCallback;
import com.wwsl.wgsj.utils.OutAdListener;
import com.wwsl.wgsj.views.AbsMainViewHolder;
import com.wwsl.wgsj.views.AbsViewHolder;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.wwsl.wgsj.adapter.MsgShortAdapter.PAYLOAD_FOLLOW;

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
        todayFragment = new TodayViewHolder(mContext, viewPager);
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
}