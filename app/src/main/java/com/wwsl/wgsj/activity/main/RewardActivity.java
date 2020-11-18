package com.wwsl.wgsj.activity.main;

import android.app.Activity;
import android.content.Intent;
import com.frame.fire.util.LogUtils;
import com.wwsl.wgsj.AppConfig;
import com.wwsl.wgsj.Constants;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.base.BaseActivity;
import com.wwsl.wgsj.utils.ToastUtil;
import com.wwsl.wgsj.views.dialog.loading.LoadingDialog;
import com.zj.zjsdk.ad.ZjAdError;
import com.zj.zjsdk.ad.ZjRewardVideoAd;
import com.zj.zjsdk.ad.ZjRewardVideoAdListener;

public class RewardActivity extends BaseActivity implements ZjRewardVideoAdListener {

  private ZjRewardVideoAd rewardVideoAD;
  private LoadingDialog   mLoadingDialog;

  private boolean canGetReward = false;

  public static void startActivity(Activity context) {
    Intent intent = new Intent(context, RewardActivity.class);
    context.startActivityForResult(intent, 1001);
  }

  @Override protected int setLayoutId() {
    return R.layout.activity_ad_reward_video;
  }

  @Override protected void init() {
    rewardVideoAD = new ZjRewardVideoAd(this, Constants.AD_VIDEO_REWARD_ID, this);
    rewardVideoAD.setUserId(AppConfig.getInstance().getUserBean().getId());
    loadAd();
  }

  public void loadAd() {
    if (mLoadingDialog == null) {
      mLoadingDialog = new LoadingDialog(this);
    }
    mLoadingDialog.show();
    rewardVideoAD.loadAd();
  }

  @Override public void onZjAdLoaded(String s) {
    LogUtils.e("myth", "激励视频加载成功");
    if (mLoadingDialog != null) {
      mLoadingDialog.close();
    }
    rewardVideoAD.showAD();
  }

  @Override public void onZjAdVideoCached() {
    LogUtils.e("myth", "onZjAdVideoCached");
  }

  @Override public void onZjAdShow() {
    LogUtils.e("myth", "onZjAdShow");
  }

  @Override public void onZjAdShowError(ZjAdError zjAdError) {
    LogUtils.e("myth", "激励视频加载失败");
    if (mLoadingDialog != null) {
      mLoadingDialog.close();
    }
    ToastUtil.show("激励视频加载失败");
  }

  @Override public void onZjAdClick() {
    LogUtils.e("myth", "onZjAdClick");
  }

  @Override public void onZjAdVideoComplete() {
    LogUtils.e("myth", "onZjAdVideoComplete");
  }

  @Override public void onZjAdExpose() {
    LogUtils.e("myth", "onZjAdExpose");
  }

  @Override public void onZjAdReward(String s) {
    canGetReward = true;
    LogUtils.e("myth", "onZjAdReward");
  }

  @Override public void onZjAdClose() {
    Intent intent = new Intent();
    intent.putExtra("times", canGetReward ? 1 : 0);
    setResult(Constants.REQUEST_CODE_AD, intent);
    finish();
  }

  @Override public void onZjAdError(ZjAdError zjAdError) {
    LogUtils.e("myth", "onZjAdError");
  }
}
