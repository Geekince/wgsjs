package com.wwsl.wgsj.activity.main;

import android.app.Activity;
import android.content.Context;
import android.view.ViewGroup;

import androidx.viewpager.widget.ViewPager;

import com.wwsl.wgsj.R;
import com.wwsl.wgsj.adapter.CommPagerAdapter;
import com.wwsl.wgsj.base.BaseFragment;
import com.wwsl.wgsj.event.EventBean;
import com.wwsl.wgsj.event.MainPageChangeEvent;
import com.wwsl.wgsj.fragment.MainHomeFragment;
import com.wwsl.wgsj.fragment.PersonalHomeFragment;
import com.wwsl.wgsj.interfaces.LifeCycleAdapter;
import com.wwsl.wgsj.interfaces.LifeCycleListener;
import com.wwsl.wgsj.views.AbsMainViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cxf on 2018/9/22.
 * MainActivity 首页
 */

public class MainHomeViewHolder extends AbsMainViewHolder {
    private final static String TAG = "MainHomeViewHolder";
    private ViewPager viewPager;
    private CommPagerAdapter pagerAdapter;
    private ArrayList<BaseFragment> fragments;
    private PersonalHomeFragment personalHomeFragment;
    private MainHomeFragment mainHomeFragment;

    public MainHomeViewHolder(Context context, ViewGroup parentView) {
        super(context, parentView);
    }

    public MainHomeViewHolder(Context context, ViewGroup parentView, Activity activity) {
        super(context, parentView, activity);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main_home;
    }

    @Override
    public void init() {
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        fragments = new ArrayList<>();
        mainHomeFragment = new MainHomeFragment();
        personalHomeFragment = new PersonalHomeFragment();
        fragments.add(mainHomeFragment);
        fragments.add(personalHomeFragment);
        List<String> title = new ArrayList<>();
        title.add("");
        title.add("");

        pagerAdapter = new CommPagerAdapter(mainActivity.getSupportFragmentManager(), fragments, title);
        viewPager.setOffscreenPageLimit(2);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 1) {
                    personalHomeFragment.updateUserInfo(mainHomeFragment.getCurrentUserId());
                }
                //显示与隐藏底部导航栏
                EventBus.getDefault().post(new EventBean(position == 1 ? "bottom_hide" : "bottom_show"));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        viewPager.setAdapter(pagerAdapter);
    }

    @Subscribe
    public void toUserHomeFragment(MainPageChangeEvent event) {
        if (viewPager != null) {
            viewPager.setCurrentItem(event.getTo());
        }
    }

    @Override
    public List<LifeCycleListener> getLifeCycleListenerList() {
        List<LifeCycleListener> list = new ArrayList<>();
        list.add(new LifeCycleAdapter() {
            @Override
            public void onCreate() {
                super.onCreate();
            }

            @Override
            public void onStart() {
                super.onStart();
                if (!EventBus.getDefault().isRegistered(MainHomeViewHolder.this)) {
                    EventBus.getDefault().register(MainHomeViewHolder.this);
                }
            }

            @Override
            public void onDestroy() {
                EventBus.getDefault().unregister(MainHomeViewHolder.this);
            }
        });
        return list;
    }

    public void setCurrentPage(int position) {

    }


    @Override
    public void onResume() {
        super.onResume();
        if (isFirstLoadData()) {
            setCurrentPage(0);
        } else {
            if (viewPager != null && viewPager.getCurrentItem() == 0) {
                mainHomeFragment.onResume();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (viewPager != null && viewPager.getCurrentItem() == 0) {
            mainHomeFragment.pausePlay();
        }
    }

    @Override
    public boolean onBackPressed() {
        if (!fragments.get(viewPager.getCurrentItem()).onBackPressed()) {
            if (viewPager.getCurrentItem() == 1) {
                viewPager.setCurrentItem(0);
                return true;
            }
        }

        return super.onBackPressed();
    }

    public void updateUserInfo(String uid) {
        if (personalHomeFragment != null) {
            personalHomeFragment.updateUserInfo(uid);
        }
    }
}
