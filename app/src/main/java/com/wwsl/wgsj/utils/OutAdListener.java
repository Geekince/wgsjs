package com.wwsl.wgsj.utils;

import com.frame.fire.util.LogUtils;
import com.mob68.ad.listener.IRewardVideoAdListener;

import java.util.HashMap;

public class OutAdListener implements IRewardVideoAdListener {

    private String tag = "";

    public OutAdListener(String tag) {
        this.tag = tag;
    }

    private final static String TAG = "OutAdListener";

    @Override
    public void onAdSuccess() {
        LogUtils.e(TAG, "OutAdListener onAdSuccess: ");
    }

    @Override
    public void onAdFailed(String s) {
        LogUtils.e(TAG, "OutAdListener onAdFailed: ");
    }

    @Override
    public void onAdClick(long l) {
        LogUtils.e(TAG, "OutAdListener onAdClick: ");
    }

    @Override
    public void onVideoPlayStart() {
        LogUtils.e(TAG, "OutAdListener onVideoPlayStart: ");
    }

    @Override
    public void onAdPreSuccess() {
        LogUtils.e(TAG, "OutAdListener onAdPreSuccess: ");
    }

    @Override
    public void onVideoPlayComplete() {
        LogUtils.e(TAG, "OutAdListener onVideoPlayComplete: ");
    }

    @Override
    public void onVideoPlayError(String s) {
        LogUtils.e(TAG, "OutAdListener onVideoPlayError: ");
    }

    @Override
    public void onVideoPlayClose(long l) {
        LogUtils.e(TAG, "OutAdListener onVideoPlayClose: ");
    }

    @Override
    public void onLandingPageOpen() {
        LogUtils.e(TAG, "OutAdListener onLandingPageOpen: ");
    }

    @Override
    public void onLandingPageClose() {
        LogUtils.e(TAG, "OutAdListener onLandingPageClose: ");
    }

    @Override
    public void onReward(HashMap<String, String> hashMap) {
        LogUtils.e(TAG, "OutAdListener onReward: " + hashMap.size());
    }


}
