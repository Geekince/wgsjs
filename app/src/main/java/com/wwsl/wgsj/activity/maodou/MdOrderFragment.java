package com.wwsl.wgsj.activity.maodou;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.flyco.tablayout.SlidingTabLayout;
import com.luck.picture.lib.PictureSelector;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.adapter.BasePagerAdapter;
import com.wwsl.wgsj.base.BaseFragment;
import com.wwsl.wgsj.views.ScrollViewPager;

import java.util.ArrayList;


public class MdOrderFragment extends BaseFragment {

    private int mType;
    private SlidingTabLayout subIndicator;
    private ArrayList<Fragment> mFragments;
    private ScrollViewPager subViewPager;
    private BasePagerAdapter pagerAdapter;
    private int mPage = 1;

    public static MdOrderFragment newInstance(int param1) {
        MdOrderFragment fragment = new MdOrderFragment();
        Bundle args = new Bundle();
        args.putInt("type", param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected int setLayoutId() {
        return R.layout.fragment_md_order;
    }

    @Override
    protected void init() {
        if (getArguments() != null) {
            mType = getArguments().getInt("type");
        }
        subIndicator = (SlidingTabLayout) findViewById(R.id.subIndicator);
        subViewPager = (ScrollViewPager) findViewById(R.id.subViewPager);

        mFragments = new ArrayList<>();

        String[] subTile;
        if (mType == TYPE_SUBSCRIBE) {
            subTile = new String[]{"进行中", "已付款", "已完成"};
        } else {
            subTile = new String[]{"挂单中", "已打款", "已完成"};
        }

        mFragments.add(MdOrderSubFragment.newInstance(mType, MdOrderSubFragment.ORDER_PROCESSING));
        mFragments.add(MdOrderSubFragment.newInstance(mType, MdOrderSubFragment.ORDER_CONFIRM));
        mFragments.add(MdOrderSubFragment.newInstance(mType, MdOrderSubFragment.ORDER_FINISH));

        pagerAdapter = new BasePagerAdapter(getChildFragmentManager(), subTile, mFragments);
        subViewPager.setAdapter(pagerAdapter);
        subViewPager.setOffscreenPageLimit(3);
        subIndicator.setViewPager(subViewPager, subTile);
        subIndicator.setCurrentTab(0);

    }

    @Override
    protected void initialData() {

    }

    public static final int TYPE_SALE = 0;
    public static final int TYPE_SUBSCRIBE = 1;


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (openAlbumResultListener != null) {
                openAlbumResultListener.onResult(requestCode, PictureSelector.obtainMultipleResult(data));
            }
        }
    }
}
