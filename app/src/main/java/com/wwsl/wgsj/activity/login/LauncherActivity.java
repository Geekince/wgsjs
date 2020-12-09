package com.wwsl.wgsj.activity.login;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTAppDownloadListener;
import com.bytedance.sdk.openadsdk.TTSplashAd;
import com.frame.fire.util.LogUtils;
import com.permissionx.guolindev.PermissionX;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wwsl.wgsj.AppConfig;
import com.wwsl.wgsj.Constants;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.activity.MainActivity;
import com.wwsl.wgsj.activity.common.WebViewActivity;
import com.wwsl.wgsj.ad.TTAdManagerHolder;
import com.wwsl.wgsj.bean.ConfigBean;
import com.wwsl.wgsj.bean.PartnerCityBean;
import com.wwsl.wgsj.bean.UserBean;
import com.wwsl.wgsj.custom.AdvertSkipView;
import com.wwsl.wgsj.glide.ImgLoader;
import com.wwsl.wgsj.http.HttpCallback;
import com.wwsl.wgsj.http.HttpConst;
import com.wwsl.wgsj.http.HttpUtil;
import com.wwsl.wgsj.interfaces.CommonCallback;
import com.wwsl.wgsj.interfaces.NoDoubleClickListener;
import com.wwsl.wgsj.utils.DialogUtil;
import com.wwsl.wgsj.utils.DownloadUtil;
import com.wwsl.wgsj.utils.LocationUtil;
import com.wwsl.wgsj.utils.ScreenDimenUtil;
import com.wwsl.wgsj.utils.SpUtil;
import com.wwsl.wgsj.utils.SystemUtil;
import com.wwsl.wgsj.utils.ToastUtil;
import com.wwsl.wgsj.utils.VersionUtil;
import com.wwsl.wgsj.views.CountdownView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import io.netopen.hotbitmapgg.library.view.RingProgressBar;

/**
 * @author :
 * @date : 2020/6/11 19:51
 * @description : LauncherActivity
 * 启动图-》本地视频-》接口-》图片/视频广告-》众简广告
 */
public class LauncherActivity extends AppCompatActivity {
  private static WeakReference<LauncherActivity> instance;
  private        Window                          window;
  protected      Context                         mContext;

  //图片广告
  private RelativeLayout   ui_img;
  private ImageView        ivAdvert;
  private TextView         tvAdvertSkip;
  private CountdownView    mCountdownView;
  private AdvertSkipView   advertSkipView;
  //apk更新
  private ConstraintLayout downloadLayout;
  private RingProgressBar  loadPb;
  //启动图片
  private TextView        launch_img;

  //穿山甲广告
  private static final int         AD_TIME_OUT        = 3000;
  private              FrameLayout zjContainer;
  private              TTAdNative  mTTAdNative;
  private              String      mCodeId            = "887410529";
  private              boolean     mIsExpress         = false;
  private              TTSplashAd  loadAd;
  //是否强制跳转到主页面
  private              boolean     mForceGoMain;
  /**
   * 0.登录
   * 1.进主页
   * 2.绑定微信
   * 3.绑定手机号
   */
  private              int         mGoType            = 0;
  private              boolean     isGrand            = false;//是否已经授权
  private              boolean     isLoadConfigFinish = false;//是否已配置完成
  private              boolean     isNeedVersion      = false;//是否强制更新apk
  private              boolean     isGoShowingAd      = false;//是点击了广告
  private              boolean     isLoadAdFinish      = false;//是否加载ad成功

  private String   phone;
  private String   uid;
  private String   token;
  private String[] ps = {
      Manifest.permission.READ_EXTERNAL_STORAGE,
      Manifest.permission.WRITE_EXTERNAL_STORAGE,
      Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.READ_PHONE_STATE
  };

  private final Handler handler = new Handler(msg -> {
    switch (msg.what) {
      case 12:
        prepareFinish();
        break;
    }
    return true;
  });

  //广告初始化开屏广告
  private void initAd() {
    //step3:创建开屏广告请求参数AdSlot,具体参数含义参考文档
    int screenHeight = ScreenDimenUtil.getInstance().getScreenHeight();
    int screenWidth = ScreenDimenUtil.getInstance().getScreenWidth();
    AdSlot adSlot = null;
    if (mIsExpress) {
      //个性化模板广告需要传入期望广告view的宽、高，单位dp，请传入实际需要的大小，
      //比如：广告下方拼接logo、适配刘海屏等，需要考虑实际广告大小
      //float expressViewWidth = UIUtils.getScreenWidthDp(this);
      //float expressViewHeight = UIUtils.getHeight(this);
      adSlot = new AdSlot.Builder()
          .setCodeId(mCodeId)
          //模板广告需要设置期望个性化模板广告的大小,单位dp,代码位是否属于个性化模板广告，请在穿山甲平台查看
          //view宽高等于图片的宽高
          .setExpressViewAcceptedSize(screenWidth, screenHeight)
          .build();
    } else {
      adSlot = new AdSlot.Builder()
          .setCodeId(mCodeId)
          .setImageAcceptedSize(screenWidth, screenHeight)
          .build();
    }

    //step4:请求广告，调用开屏广告异步请求接口，对请求回调的广告作渲染处理
    mTTAdNative.loadSplashAd(adSlot, new TTAdNative.SplashAdListener() {
      @Override
      @MainThread
      public void onError(int code, String message) {
        LogUtils.e(TAG, "onError" + "code->" + code + ",message->" + message);
        goNext("");
      }

      @Override
      @MainThread
      public void onTimeout() {
        LogUtils.e(TAG, "onTimeout");
        goNext("");
      }

      @Override
      @MainThread
      public void onSplashAdLoad(TTSplashAd ad) {
        LogUtils.e(TAG, "onSplashAdLoad: 加载中...");
        isLoadAdFinish = true;
        if (ad == null) {
          return;
        }
        loadAd = ad;
        handler.sendEmptyMessage(12);
        //获取SplashView
      }
    }, AD_TIME_OUT);
  }

  public boolean isAdDismiss = false;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    window = getWindow();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
      window.setStatusBarColor(Color.TRANSPARENT);
      int option = window.getDecorView().getSystemUiVisibility()
          | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
          | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
          | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
          | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
      window.getDecorView().setSystemUiVisibility(option);
      window.setNavigationBarColor(Color.parseColor("#000000"));
    } else {
      window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
          WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
      window.getDecorView().setSystemUiVisibility(View.GONE);
    }

    setContentView(R.layout.activity_launcher);

    if (receiveGGNotification()) return;

    mContext = this;

    zjContainer = findViewById(R.id.rootView);
    mTTAdNative = TTAdManagerHolder.get().createAdNative(this);

    ui_img = findViewById(R.id.ui_img);
    ivAdvert = findViewById(R.id.ivAdvert);
    tvAdvertSkip = findViewById(R.id.tvAdvertSkip);
    mCountdownView = findViewById(R.id.count_down_view);
    mCountdownView.setCountNum(5);
    mCountdownView.setOnCountdownListener(() -> mCountdownView.setVisibility(View.GONE));

    launch_img = findViewById(R.id.launch_img);

    downloadLayout = findViewById(R.id.updateLayout);
    loadPb = findViewById(R.id.loadPb);
    loadSpData();
    //1.检查权限
    checkPermission();
  }

  /**
   * 第三方广告平台广告
   */
  private void showAd3() {
    showUI(4);
    showAd();
  }

  @Override
  protected void onResume() {
    //判断是否该跳转到主页面
    if (mForceGoMain) {
      goNext("");
    }
    super.onResume();
  }

  @Override
  protected void onStop() {
    super.onStop();
    mForceGoMain = true;
  }

  private void showAd() {

    if (loadAd != null){
      View view = loadAd.getSplashView();
      if (view != null && zjContainer != null && !LauncherActivity.this.isFinishing()) {
        zjContainer.removeAllViews();
        //把SplashView 添加到ViewGroup中,注意开屏广告view：width >=70%屏幕宽；height >=50%屏幕高
        zjContainer.addView(view);
        //设置不开启开屏广告倒计时功能以及不显示跳过按钮,如果这么设置，您需要自定义倒计时逻辑
        //ad.setNotAllowSdkCountdown();
      } else {
        goNext("");
      }

      //设置SplashView的交互监听器
      loadAd.setSplashInteractionListener(new TTSplashAd.AdInteractionListener() {
        @Override
        public void onAdClicked(View view, int type) {
          LogUtils.e(TAG, "onAdClicked: ");
        }

        @Override
        public void onAdShow(View view, int type) {
          LogUtils.e(TAG, "onAdShow: ");
        }

        @Override
        public void onAdSkip() {
          LogUtils.e(TAG, "onAdSkip: ");

          isAdDismiss = true;
          goNext("");
        }

        @Override
        public void onAdTimeOver() {
          LogUtils.e(TAG, "onAdTimeOver: ");
          if (!isAdDismiss) {
            goNext("");
          }
        }
      });
      if (loadAd.getInteractionType() == TTAdConstant.INTERACTION_TYPE_DOWNLOAD) {
        loadAd.setDownloadListener(new TTAppDownloadListener() {
          boolean hasShow = false;

          @Override
          public void onIdle() {
          }

          @Override
          public void onDownloadActive(long totalBytes, long currBytes, String fileName,
              String appName) {
            if (!hasShow) {

              LogUtils.e(TAG, "下载中...: ");
              hasShow = true;
            }
          }

          @Override
          public void onDownloadPaused(long totalBytes, long currBytes, String fileName,
              String appName) {
            LogUtils.e(TAG, "下载暂停...: ");
          }

          @Override
          public void onDownloadFailed(long totalBytes, long currBytes, String fileName,
              String appName) {
            LogUtils.e(TAG, "下载失败...: ");
          }

          @Override
          public void onDownloadFinished(long totalBytes, String fileName, String appName) {
            LogUtils.e(TAG, "下载完成...: ");
          }

          @Override
          public void onInstalled(String fileName, String appName) {
            LogUtils.e(TAG, "安装完成...: ");
          }
        });
      }
    }else {
      goNext("");
    }
  }

  private void loadSpData() {
    String[] temp = SpUtil.getInstance().getMultiStringValue(SpUtil.FIRST_LOGIN, SpUtil.USER_PHONE);
    this.phone = temp[1];
    String[] uidAndToken = SpUtil.getInstance().getMultiStringValue(SpUtil.UID, SpUtil.TOKEN);
    this.uid = uidAndToken[0];
    this.token = uidAndToken[1];
  }

  private void loadCity() {
    String cityStr = SpUtil.getInstance().getStringValue(SpUtil.CITY);
    if (!StrUtil.isEmpty(cityStr)) {
      List<PartnerCityBean> cityBeans = JSON.parseArray(cityStr, PartnerCityBean.class);
      AppConfig.getInstance().setCityBeans(cityBeans);
    } else {
      HttpUtil.getCityConfig();
    }
  }

  private boolean receiveGGNotification() {
    //下面的代码是为了防止一个bug:
    // 收到极光通知后，点击通知，如果没有启动app,则启动app。然后切后台，再次点击桌面图标，app会重新启动，而不是回到前台。
    instance = new WeakReference<>(this);
    Intent intent = getIntent();
    if (!isTaskRoot()
        && intent != null
        && intent.hasCategory(Intent.CATEGORY_LAUNCHER)
        && intent.getAction() != null
        && intent.getAction().equals(Intent.ACTION_MAIN)) {
      finish();
      return true;
    }
    return false;
  }

  private final static String TAG = "LauncherActivity";

  @SuppressLint({ "MissingPermission", "HardwareIds" })
  public void checkPermission() {
    SpUtil.getInstance().setStringValue(SpUtil.FIRST_LOGIN, "0");

    boolean p1 = PermissionX.isGranted(mContext, ps[0]);
    boolean p2 = PermissionX.isGranted(mContext, ps[1]);
    boolean p3 = PermissionX.isGranted(mContext, ps[2]);
    boolean p4 = PermissionX.isGranted(mContext, ps[3]);
    List<String> lps = new ArrayList<>();
    if (!p1) lps.add(ps[0]);
    if (!p2) lps.add(ps[1]);
    if (!p3) lps.add(ps[2]);
    if (!p4) lps.add(ps[3]);

    if (lps.size() == 0) {
      isGrand = true;
      initAd();
      getConfig();
    } else {
      //暂时没有适配android 10 后台定位
      PermissionX.init(this)
          .permissions(lps)
          .request((allGranted, grantedList, deniedList) -> {
            //获取设备唯一识别id
            String deviceId = SpUtil.getInstance().getStringValue(SpUtil.DEVICE_ID);
            if (grantedList.contains(Manifest.permission.READ_PHONE_STATE)) {
              //手机号获取已经授权
              TelephonyManager tm = (TelephonyManager) LauncherActivity.this.getSystemService(
                  Context.TELEPHONY_SERVICE);
              phone = tm.getLine1Number();
              phone = StrUtil.replace(phone, "+86", "");
              SpUtil.getInstance().setStringValue(SpUtil.USER_PHONE, phone);
              //当前没有设备id
              if (StrUtil.isEmpty(deviceId)) {
                deviceId = tm.getDeviceId();
              }
              AppConfig.getInstance().getMobileBean().setTel(tm.getLine1Number());
              AppConfig.getInstance().getMobileBean().setImei(tm.getSimSerialNumber());
              AppConfig.getInstance().getMobileBean().setImsi(tm.getSubscriberId());
            }
            if (StrUtil.isEmpty(deviceId)) {
              //仍没有拿到设备id
              deviceId = SystemUtil.getDeviceUUID().replace("-", "");
            }
            AppConfig.getInstance().setDeviceId(deviceId);
            SpUtil.getInstance().setStringValue(SpUtil.DEVICE_ID, deviceId);
            //开启定位
            if (grantedList.contains(Manifest.permission.ACCESS_FINE_LOCATION)) {
              LocationUtil.getInstance().startLocation();
            }
            if (deniedList.contains(ps[1]) || deniedList.contains(ps[0])) {
              ToastUtil.show("部分权限未授权,部分功能不能正常使用");
            }
            isGrand = true;
            initAd();
            getConfig();
          });
    }
  }

  /**
   * 1 launch_img
   * 2 ui_video
   * 3 ui_img
   * 4 zjContainer
   * 5 downloadLayout
   */
  private void showUI(int i) {
    if (i == 1) {
      launch_img.setVisibility(View.VISIBLE);
      downloadLayout.setVisibility(View.GONE);
      ui_img.setVisibility(View.GONE);
      zjContainer.setVisibility(View.GONE);
    } else if (i == 2) {
      downloadLayout.setVisibility(View.GONE);
      ui_img.setVisibility(View.GONE);
      zjContainer.setVisibility(View.GONE);
      launch_img.setVisibility(View.GONE);
    } else if (i == 3) {
      ui_img.setVisibility(View.VISIBLE);
      downloadLayout.setVisibility(View.GONE);
      zjContainer.setVisibility(View.GONE);
      launch_img.setVisibility(View.GONE);
    } else if (i == 4) {
      zjContainer.setVisibility(View.VISIBLE);
      downloadLayout.setVisibility(View.GONE);
      ui_img.setVisibility(View.GONE);
      launch_img.setVisibility(View.GONE);
    } else if (i == 5) {
      downloadLayout.setVisibility(View.VISIBLE);
      ui_img.setVisibility(View.GONE);
      zjContainer.setVisibility(View.GONE);
      launch_img.setVisibility(View.GONE);
    }
  }

  /**
   * 获取Config信息
   **/
  private void getConfig() {
    //必须获取首页导航才能登录
    loadCity();//获取市，区

    HttpUtil.getConfig(new CommonCallback<ConfigBean>() {
      @Override
      public void callback(ConfigBean bean) {
        if (bean != null) {
          if (!VersionUtil.isLatest(bean.getVersion())) {
            isNeedVersion = true;
          }
          isLoadConfigFinish = true;
          mGoType = 0;
          if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
            AppConfig.getInstance().setLoginInfo(uid, token, false);
            HttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
              @Override
              public void callback(UserBean bean) {
                if (bean != null) {
                  mGoType = 1;
                  AppConfig.getInstance().setLoginInfo(uid, token, true);
                  String isNeedAuthWx = AppConfig.getInstance().getConfig().getIsNeedAuthWx();
                  if (isNeedAuthWx.equals("1") && !"1".equals(bean.getIsWxAuth())) {
                    mGoType = 2;//需要先绑定微信
                  }
                } else {
                  LogUtils.e(TAG, "获取用户最新数据失败");
                }
                handler.sendEmptyMessage(12);
              }
            });
          } else {
            mGoType = 0;
            handler.sendEmptyMessage(12);
          }
        } else {
          ToastUtil.show("配置获取失败");
          mGoType = 0;
          handler.sendEmptyMessage(12);
        }
      }
    });
  }

  private void showAdvertise() {
    showAd3();
    //ConfigBean configBean = AppConfig.getInstance().getConfig();
    //List<LaunchAdBean> adList = configBean.getAdList();
    //if (null != adList && adList.size() == 1 && "1".equals(configBean.getIsLaunch())) {
    //  LaunchAdBean launchAdBean = adList.get(0);
    //  if ("1".equals(launchAdBean.getType()) && !StrUtil.isEmpty(launchAdBean.getThumb())) {
    //    //图片
    //    mCountdownView.setVisibility(View.GONE);
    //    setAdvert(launchAdBean.getThumb(), launchAdBean.getUrl());
    //  } else if ("2".equals(launchAdBean.getType()) && !StrUtil.isEmpty(launchAdBean.getThumb())) {
    //    showUI(2);
    //    //视频
    //    videoIndex = 2;
    //    videoAdUrl = launchAdBean.getUrl();
    //    mVideoView.release();
    //    mVideoView.setUrl(launchAdBean.getThumb());
    //    mVideoView.start();
    //  } else {
    //    showAd3();
    //  }
    //} else {
    //  showAd3();
    //}
  }

  @Override
  protected void onDestroy() {
    HttpUtil.cancel(HttpConst.IF_TOKEN);
    HttpUtil.cancel(HttpConst.GET_BASE_INFO);
    HttpUtil.cancel(HttpConst.GET_CONFIG);
    super.onDestroy();
  }

  private void setAdvert(String slidePic, String slideUrl) {
    showUI(3);
    ImgLoader.display(slidePic, ivAdvert, 0, 0, false, null, new RequestListener<Drawable>() {
      @Override
      public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target,
          boolean isFirstResource) {
        goNext("");
        return false;
      }

      @Override
      public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
          DataSource dataSource, boolean isFirstResource) {
        ivAdvert.setVisibility(View.VISIBLE);
        tvAdvertSkip.setVisibility(View.VISIBLE);
        advertSkipView = new AdvertSkipView(6000, 1000, tvAdvertSkip);
        advertSkipView.setAdvertSkipFinishListener(() -> goNext(""));
        advertSkipView.start();

        tvAdvertSkip.setOnClickListener(new NoDoubleClickListener() {
          @Override
          protected void onNoDoubleClick(View v) {
            advertSkipView.cancel();
            goNext("");
          }
        });
        ivAdvert.setOnClickListener(new NoDoubleClickListener() {
          @Override
          protected void onNoDoubleClick(View v) {
            advertSkipView.cancel();
            goNext(slideUrl);
          }
        });
        return false;
      }
    }, null);
  }

  private void goNextActivity() {
    if (mGoType == 0) {
      LoginActivity.forward(phone);
      overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    } else if (mGoType == 1) {
      MainActivity.forward(mContext);
      overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    } else if (mGoType == 2) {
      wxAuth();
    }
  }

  private void goNext(String slideUrl) {
    if (TextUtils.isEmpty(slideUrl)) {
      //是否点击了广告
      if (!isGoShowingAd) {
        goNextActivity();
      }
    } else {
      WebViewActivity.forwardForResult(mContext, slideUrl);
    }
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    goNextActivity();
  }

  @Override
  public void onBackPressed() {
    if (advertSkipView != null) {
      advertSkipView.cancel();
    }
    super.onBackPressed();
  }

  public static void finishActivity() {
    if (instance != null && instance.get() != null) {
      instance.get().finish();
    }
  }

  public void wxAuth() {
    DialogUtil.showSimpleDialog(mContext, "暂未绑定微信,请先绑定微信", new DialogUtil.SimpleCallback2() {
      @Override
      public void onCancelClick() {
        LoginActivity.forward(phone);
        overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
        finish();
      }

      @Override
      public void onConfirmClick(Dialog dialog, String content) {
        dialog.dismiss();
        UMShareAPI umShareAPI = UMShareAPI.get(LauncherActivity.this);
        umShareAPI.getPlatformInfo(LauncherActivity.this, SHARE_MEDIA.WEIXIN, new UMAuthListener() {
          @Override
          public void onStart(SHARE_MEDIA share_media) {
            LogUtils.e(TAG, "微信登录开始授权 ");
          }

          @Override
          public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
            HttpUtil.bindWx(map.get("openid"), map.get("unionid"), map.get("name"),
                map.get("iconurl"), new HttpCallback() {
                  @Override
                  public void onSuccess(int code, String msg, String[] info) {
                    ToastUtil.show(msg);
                    if (code == 0) {
                      MainActivity.forward(mContext);
                      overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
                    } else {
                      wxAuthBack(msg);
                    }
                  }

                  @Override
                  public void onError() {
                    wxAuthBack("绑定失败,请登录!");
                  }
                });
          }

          @Override
          public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
            wxAuthBack("微信授权失败");
          }

          @Override
          public void onCancel(SHARE_MEDIA share_media, int i) {
            wxAuthBack("微信授权取消");
          }
        });
      }
    });
  }

  private void wxAuthBack(String str) {
    SpUtil.getInstance().removeValue(SpUtil.UID);
    SpUtil.getInstance().removeValue(SpUtil.TOKEN);
    ToastUtil.show(str);
    LoginActivity.forward(phone);
    overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
    finish();
  }

  /**
   * 准备完成事件
   */
  public void prepareFinish() {
    if (isGrand && isLoadConfigFinish &&isLoadAdFinish) {
      ConfigBean configBean = AppConfig.getInstance().getConfig();
      if (isNeedVersion && "1".equals(configBean.getIsNeedUpdate())) {
        VersionUtil.showDialog(
            mContext,
            false,
            configBean.getVersion(),
            configBean.getUpdateDes(),
            (view, object) -> downloadNewApk(configBean));
      } else {
        showAdvertise();
      }
    }
  }

  /**
   * 新版本弹窗
   */
  private void downloadNewApk(ConfigBean configBean) {
    String url = configBean.getUpdateApkUrl();
    if (!TextUtils.isEmpty(url)) {
      try {
        DownloadUtil downloadUtil = new DownloadUtil();
        showUI(5);
        downloadUtil.download(Constants.APP_APK, mContext.getFilesDir(), Constants.APK_FILE_NAME,
            url, new DownloadUtil.Callback() {
              @Override
              public void onSuccess(File file) {
                showUI(1);
                VersionUtil.installNormal(LauncherActivity.this, file.getPath());
              }

              @Override
              public void onProgress(int progress) {
                loadPb.setProgress(progress);
              }

              @Override
              public void onError(Throwable e) {
                ToastUtil.show("下载失败");
                showUI(1);
              }
            });
      } catch (Exception e) {
        ToastUtil.show(R.string.version_download_url_error);
      }
    } else {
      ToastUtil.show(R.string.version_download_url_error);
    }
  }
}
