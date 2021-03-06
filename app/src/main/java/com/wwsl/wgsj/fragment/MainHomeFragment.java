package com.wwsl.wgsj.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.frame.fire.util.LogUtils;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.activity.common.SearchActivity;
import com.wwsl.wgsj.activity.live.MainLiveListActivity;
import com.wwsl.wgsj.activity.main.RewardActivity;
import com.wwsl.wgsj.adapter.CommPagerAdapter;
import com.wwsl.wgsj.base.BaseFragment;
import com.wwsl.wgsj.bean.LiveBean;
import com.wwsl.wgsj.event.VideoPlayEvent;
import com.wwsl.wgsj.http.HttpCallback;
import com.wwsl.wgsj.http.HttpConst;
import com.wwsl.wgsj.http.HttpUtil;
import com.wwsl.wgsj.utils.StringUtil;
import com.wwsl.wgsj.utils.ToastUtil;
import com.wwsl.wgsj.utils.VideoCutDownTimer;
import com.wwsl.wgsj.views.OnVideoTickListener;
import com.wwsl.wgsj.views.TabEntity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import io.netopen.hotbitmapgg.library.view.RingProgressBar;


/**
 * @author :
 * @date : 2020/6/17 17:28
 * @description : 主页fragment
 */
public class MainHomeFragment extends BaseFragment implements OnVideoTickListener {

    private int[] mIconUnselectIds = {R.mipmap.icon_main_tab_hot_normal, R.mipmap.icon_main_tab_fujin_unselect, R.mipmap.icon_main_tab_follow_normal};

    private int[] mIconSelectIds = {R.mipmap.icon_main_tab_hot_select, R.mipmap.icon_main_tab_fujin_select, R.mipmap.icon_main_tab_follow_select};

    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    private VideoPlayFragment hotFragment;
    private VideoPlayFragment specialFragment;
    private VideoPlayFragment followFragment;
    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.taskPb)
    RingProgressBar taskPb;
    @BindView(R.id.tvTimes)
    TextView tvTimes;

    @BindView(R.id.tab_title)
    CommonTabLayout tabTitle;
    @BindView(R.id.clickLive)
    TextView tvLive;

    private boolean needShowTimeTick = false;
    private int cutDownTime = 5;//默认是10
    private int tikTimes;//计时总次数
    private int perTime = 15000;//单次计时时间
    private int perMinTick = 1;//每分钟计时几次
    private VideoCutDownTimer cutDownTimer;
    private long tempDuration = 0;
    private String curTickVideoId = "";

    private ArrayList<VideoPlayFragment> fragments = new ArrayList<>();
    private CommPagerAdapter pagerAdapter;
    /**
     * 默认显示第一页推荐页
     */
    public static int curIndex = 0;


    @Override
    protected int setLayoutId() {
        return R.layout.fragment_main;
    }

    @Override
    protected void init() {

        setFragments();
        findViewById(R.id.btnSearch).setOnClickListener((v) -> {
            SearchActivity.forward(mContext);
        });
    }


    private void initCutTime(int time) {
        cutDownTime = time;

        perMinTick = 60000 / perTime;

        tikTimes = perMinTick * cutDownTime;// 计时总次数

        needShowTimeTick = (tikTimes > 0);

        taskPb.setVisibility(needShowTimeTick ? View.VISIBLE : View.GONE);
        tvTimes.setVisibility(needShowTimeTick ? View.VISIBLE : View.GONE);
    }

    private void setFragments() {
        cutDownTimer = new VideoCutDownTimer(perTime, this);
        String[] titles = new String[mIconSelectIds.length];
        titles[0] = "";
        hotFragment = new VideoPlayFragment();
        hotFragment.setType(HttpConst.VIDEO_TYPE_HOT);
        fragments.add(hotFragment);
        titles[1] = "";
        specialFragment = new VideoPlayFragment();
        specialFragment.setType(HttpConst.VIDEO_TYPE_HOMETOWN);
        fragments.add(specialFragment);
        titles[2] = "";
        followFragment = new VideoPlayFragment();
        followFragment.setType(HttpConst.VIDEO_TYPE_FOLLOW);
        fragments.add(followFragment);

        for (int i = 0; i < titles.length; i++) {
            mTabEntities.add(new TabEntity(titles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }

        pagerAdapter = new CommPagerAdapter(getChildFragmentManager(), fragments, Arrays.asList(titles));

        viewPager.setOffscreenPageLimit(titles.length);
        viewPager.setAdapter(pagerAdapter);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (curIndex == position) return;
                curIndex = position;
                tabTitle.setCurrentTab(curIndex);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        tvLive.setOnClickListener((v) -> {
            mContext.startActivity(new Intent(mContext, MainLiveListActivity.class));
        });

        tabTitle.setTabData(mTabEntities);
        tabTitle.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                viewPager.setCurrentItem(position);
            }

            @Override
            public void onTabReselect(int position) {
                if (curIndex == position) {
                    fragments.get(position).getNewestVideo();
                }
            }
        });

        tabTitle.setCurrentTab(0);
    }


    public String getCurrentUserId() {
        String id = null;
        int currentItem = viewPager.getCurrentItem();
        id = fragments.get(currentItem).getCurrentVideoUserId();
        return id;
    }

    private final static String TAG = "MainHomeFragment";

    @Override
    protected void initialData() {
        HttpUtil.getTaskCutTime(new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && info != null && info.length > 0) {
                    if (StringUtil.isInteger(info[0])) {
                        initCutTime(Integer.parseInt(info[0]));
                    } else {
                        LogUtils.e(TAG, "initCutTime: 接口参数返回错误");
                    }
                }
            }
        });
    }


    public void pausePlay() {
        if (viewPager != null) {
            fragments.get(viewPager.getCurrentItem()).onPause();
        }
    }

    public void resumePlay() {
        if (viewPager != null) {
            fragments.get(viewPager.getCurrentItem()).onResume();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (fragments.get(curIndex).onBackPressed()) {
            return true;
        }
        return super.onBackPressed();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }

        resumePlay();
        isHaveLive();
    }

    private void isHaveLive() {
        HttpUtil.getLiveList(1, new HttpCallback() {
            @Override
            public void onSuccess(int code, String msg, String[] info) {
                if (code == 0 && null != info) {
                    JSONObject obj = JSON.parseObject(info[0]);
                    List<LiveBean> temp = JSON.parseArray(obj.getString("list"), LiveBean.class);
                    if (temp.size() > 0) {
                        tvLive.setVisibility(View.VISIBLE);
                    } else {
                        tvLive.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void onTick(long millisUntilFinished) {
        int i = (int) ((millisUntilFinished / 10 + tempDuration / 10) / 60);
        taskPb.setProgress(i);
    }

    @Override
    public void onFinish() {
        tikTimes--;

        if ((tikTimes % perMinTick) == 0) {
            cutDownTime--;
            if (cutDownTime < 4 && cutDownTime >= 1) {
                RewardActivity.startActivity(mActivity);
            } else {
                HttpUtil.videoTaskTick();
                startNewTick();
            }
        }else {
            startNewTick();
        }
    }

    private void startNewTick() {
        VideoPlayFragment fragment = fragments.get(viewPager.getCurrentItem());
        if (cutDownTime > 0 && !curTickVideoId.equals(fragment.getCurrentVideoId())) {
            curTickVideoId = fragment.getCurrentVideoId();
            int temp = tikTimes % perMinTick;
            temp = (temp == 0 ? 4 : temp);
            tempDuration = (perMinTick - temp) * perTime;
            tvTimes.setText(String.valueOf(cutDownTime));
            cutDownTimer.start();
        } else if (cutDownTime == 0) {
            taskPb.setVisibility(View.GONE);
            tvTimes.setVisibility(View.GONE);
        }
    }

    @Subscribe
    public void onVideoPlayEvent(VideoPlayEvent event) {
        curTickVideoId = event.getVideoId();
        if (!cutDownTimer.isTicking() && cutDownTime != 0 && needShowTimeTick) {
            int temp = tikTimes % perMinTick;
            temp = (temp == 0 ? 4 : temp);
            tempDuration = (perMinTick - temp) * perTime;
            tvTimes.setText(String.valueOf(cutDownTime));
            cutDownTimer.start();
        } else {

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onDetach() {
        super.onDetach();
//        try {
//            //此处为了解决数据错乱问题 ->效果未知 不易测试
//            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
//            childFragmentManager.setAccessible(true);
//            childFragmentManager.set(this, null);
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
//        super.onSaveInstanceState(outState);
    }

    public void adRewardBack(int times) {
        if (1 == times) {
            HttpUtil.videoTaskTick();
        } else {
            ToastUtil.show("观看时长较短,未完成任务!");
            cutDownTime++;
            tikTimes = perMinTick * cutDownTime;
            tvTimes.setText(String.valueOf(cutDownTime));
        }
    }
}
