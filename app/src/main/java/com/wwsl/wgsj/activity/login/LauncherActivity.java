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
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.dueeeke.videoplayer.controller.BaseVideoController;
import com.dueeeke.videoplayer.controller.ControlWrapper;
import com.dueeeke.videoplayer.controller.IControlComponent;
import com.dueeeke.videoplayer.exo.ExoMediaPlayerFactory;
import com.dueeeke.videoplayer.player.VideoView;
import com.dueeeke.videoplayer.util.L;
import com.frame.fire.util.LogUtils;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.RawResourceDataSource;
import com.permissionx.guolindev.PermissionX;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wwsl.wgsj.AppConfig;
import com.wwsl.wgsj.Constants;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.activity.MainActivity;
import com.wwsl.wgsj.activity.common.WebViewActivity;
import com.wwsl.wgsj.bean.ConfigBean;
import com.wwsl.wgsj.bean.LaunchAdBean;
import com.wwsl.wgsj.bean.MobileBean;
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
import com.wwsl.wgsj.utils.SpUtil;
import com.wwsl.wgsj.utils.StringUtil;
import com.wwsl.wgsj.utils.SystemUtil;
import com.wwsl.wgsj.utils.ToastUtil;
import com.wwsl.wgsj.utils.VersionUtil;
import com.wwsl.wgsj.views.CountdownView;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import io.netopen.hotbitmapgg.library.view.RingProgressBar;


/**
 * @author :
 * @date : 2020/6/11 19:51
 * @description : LauncherActivity
 */
public class LauncherActivity extends AppCompatActivity {

    private static WeakReference<LauncherActivity> instance;
    private ImageView ivAdvert;
    private TextView tvAdvertSkip;
    private AdvertSkipView advertSkipView;
    protected Context mContext;

    //0 进主页；1 进登录
    private int mGoType = 1;//0.登录,1.进主页.2.绑定微信.3.绑定手机号
    private CountdownView mCountdownView;
    private String phone;
    private String uid;
    private String token;
    private VideoView mVideoView;

    private View clickView;

    private ConstraintLayout downloadLayout;
    private RingProgressBar loadPb;

    private boolean isGrand = false;//是否已经授权
    private boolean isPlayEnd = false;//视频是否播放完毕
    private boolean isLoadConfigFinish = false;//是否已经开始登录
    private boolean isNeedVersion = false;//是否已经开始登录

    private boolean isGoShowingAd = false;
    private ControlWrapper mControlWrapper;
    private int videoIndex = 0;
    private String adUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
            int option = window.getDecorView().getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            window.getDecorView().setSystemUiVisibility(option);
            window.setNavigationBarColor(Color.parseColor("#000000"));
        } else {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        setContentView(R.layout.activity_launcher);

        if (receiveGGNotification()) return;

        mVideoView = findViewById(R.id.videoView);
        clickView = findViewById(R.id.clickView);

        downloadLayout = findViewById(R.id.updateLayout);
        loadPb = findViewById(R.id.loadPb);
        mContext = this;

        ivAdvert = findViewById(R.id.ivAdvert);
        tvAdvertSkip = findViewById(R.id.tvAdvertSkip);
        mCountdownView = findViewById(R.id.count_down_view);

        mVideoView = findViewById(R.id.videoView);

        mCountdownView.setCountNum(5);

        mCountdownView.setOnCountdownListener(() -> mCountdownView.setVisibility(View.GONE));

        initVideoView();

        loadSpData();

        //1.检查权限
        checkPermission();

        //2. 获取配置
        getConfig();

        //3. 播放视频
        videoIndex = 1;
        mVideoView.start();

        clickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtil.isEmpty(adUrl)) {
                    isGoShowingAd = true;
                    goNext(adUrl);
                }
            }
        });
    }

    private void loadSpData() {
        String[] temp = SpUtil.getInstance().getMultiStringValue(
                SpUtil.FIRST_LOGIN, SpUtil.USER_PHONE);
        phone = temp[1];

        String[] uidAndToken = SpUtil.getInstance().getMultiStringValue(
                SpUtil.UID, SpUtil.TOKEN);

        uid = uidAndToken[0];
        token = uidAndToken[1];
    }


    private void initVideoView() {

        mVideoView.setPlayerFactory(ExoMediaPlayerFactory.create());
        mVideoView.setScreenScaleType(VideoView.SCREEN_SCALE_MATCH_PARENT);
        DataSpec dataSpec = new DataSpec(RawResourceDataSource.buildRawResourceUri(R.raw.launch_video));
        RawResourceDataSource rawResourceDataSource = new RawResourceDataSource(this);
        try {
            rawResourceDataSource.open(dataSpec);
        } catch (RawResourceDataSource.RawResourceDataSourceException e) {
            e.printStackTrace();
        }

        String url = rawResourceDataSource.getUri().toString();
//        videoView.setUrl(HtmlConfig.LAUNCH_VIDEO_URL);

        mVideoView.setUrl(url);

        mVideoView.setLooping(false);
        BaseVideoController baseVideoController = new BaseVideoController(this) {
            @Override
            protected int getLayoutId() {
                return 0;
            }
        };

        baseVideoController.addControlComponent(new IControlComponent() {
            @Override
            public void attach(@NonNull ControlWrapper controlWrapper) {
                mControlWrapper = controlWrapper;
            }

            @Override
            public View getView() {
                return null;
            }

            @Override
            public void onVisibilityChanged(boolean isVisible, Animation anim) {
            }

            @Override
            public void onPlayStateChanged(int playState) {
            }

            @Override
            public void onPlayerStateChanged(int playerState) {
            }

            @Override
            public void setProgress(int duration, int position) {
            }

            @Override
            public void onLockStateChanged(boolean isLocked) {
            }
        });

        mVideoView.setVideoController(baseVideoController);
        mVideoView.setOnStateChangeListener(new VideoView.OnStateChangeListener() {
            @Override
            public void onPlayerStateChanged(int playerState) {

            }

            @Override
            public void onPlayStateChanged(int playState) {
                switch (playState) {
                    case VideoView.STATE_PLAYING://3
                        int[] videoSize = mVideoView.getVideoSize();
                        L.d("视频宽：" + videoSize[0]);
                        L.d("视频高：" + videoSize[1]);
                        mControlWrapper.startProgress();
                        break;
                    case VideoView.STATE_PREPARING://1
                        //在STATE_PREPARING时设置setMute(true)可实现静音播放
//                        mVideoView.setMute(true);
                        break;
                    case VideoView.STATE_PLAYBACK_COMPLETED://5
                        if (videoIndex == 1) {
                            isPlayEnd = true;
                            prepareFinish();
                        } else if (videoIndex == 2) {
                            goNext("");
                        }
                        break;
                }
            }
        });
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

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public void checkPermission() {
        SpUtil.getInstance().setStringValue(SpUtil.FIRST_LOGIN, "0");

        //暂时没有适配android 10 后台定位
        PermissionX.init(this)
                .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_PHONE_STATE)
                .request((allGranted, grantedList, deniedList) -> {
                    //获取设备唯一识别id
                    String deviceId = SpUtil.getInstance().getStringValue(SpUtil.DEVICE_ID);

                    if (grantedList.contains(Manifest.permission.READ_PHONE_STATE)) {
                        //手机号获取已经授权
                        TelephonyManager tm = (TelephonyManager) LauncherActivity.this.getSystemService(Context.TELEPHONY_SERVICE);
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

                        LogUtils.e(TAG, "MobileBean: " + MobileBean.builder()
                                .deviceId(tm.getDeviceId())
                                .tel(tm.getLine1Number())
                                .imei(tm.getSimSerialNumber())
                                .imsi(tm.getSubscriberId()).build().toString());
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

                    if (deniedList.contains(Manifest.permission.WRITE_EXTERNAL_STORAGE) || deniedList.contains(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                        ToastUtil.show("部分权限未授权,部分功能不能正常使用");
                    }

                    isGrand = true;

                    prepareFinish();

                });

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
//                    mCountdownView.start();
                    if (!VersionUtil.isLatest(bean.getVersion())) {
                        isNeedVersion = true;
                    }
                    if (!TextUtils.isEmpty(uid) && !TextUtils.isEmpty(token)) {
                        HttpUtil.getBaseInfo(new CommonCallback<UserBean>() {
                            @Override
                            public void callback(UserBean bean) {
                                if (bean != null) {
                                    AppConfig.getInstance().setLoginInfo(uid, token, false);
                                    mGoType = 0;
                                    String isNeedAuthWx = AppConfig.getInstance().getConfig().getIsNeedAuthWx();
                                    if (isNeedAuthWx.equals("1") && !"1".equals(bean.getIsWxAuth())) {
                                        mGoType = 2;//需要先绑定微信
                                    }
//                                    else if (StrUtil.isEmpty(bean.getMobile())) {
//                                        mGoType = 3;//需要先绑定手机号
//                                    }
                                } else {
                                    LogUtils.e(TAG, "获取用户最新数组失败");
                                    mGoType = 1;
                                }
                                isLoadConfigFinish = true;
                                prepareFinish();
                            }
                        });
                    } else {
                        isLoadConfigFinish = true;
                        mGoType = 1;
                        prepareFinish();
                    }
                } else {
                    ToastUtil.show("配置获取失败");
                }
            }
        });
    }


    private void showAdvertise() {
        ConfigBean configBean = AppConfig.getInstance().getConfig();
        List<LaunchAdBean> adList = configBean.getAdList();
        if (null != adList && adList.size() == 1 && "1".equals(configBean.getIsLaunch())) {
            LaunchAdBean launchAdBean = adList.get(0);
            if ("1".equals(launchAdBean.getType()) && !StrUtil.isEmpty(launchAdBean.getThumb())) {
                //图片
                mCountdownView.setVisibility(View.GONE);
                setAdvert(launchAdBean.getThumb(), launchAdBean.getUrl());
            } else if ("2".equals(launchAdBean.getType()) && !StrUtil.isEmpty(launchAdBean.getThumb())) {
                //视频
                videoIndex = 2;
                adUrl = launchAdBean.getUrl();
                mVideoView.release();
                mVideoView.setUrl(launchAdBean.getThumb());
                mVideoView.start();
            } else {
                goNext("");
            }

        } else {
            goNext("");
        }
    }


    @Override
    protected void onDestroy() {
        if (mVideoView != null) {
            mVideoView.release();
        }
        HttpUtil.cancel(HttpConst.IF_TOKEN);
        HttpUtil.cancel(HttpConst.GET_BASE_INFO);
        HttpUtil.cancel(HttpConst.GET_CONFIG);

        super.onDestroy();
    }

    private void setAdvert(String slidePic, String slideUrl) {
        ivAdvert.setVisibility(View.INVISIBLE);
        ImgLoader.display(slidePic, ivAdvert, 0, 0, false, null, new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                goNext("");
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                ivAdvert.setVisibility(View.VISIBLE);
                tvAdvertSkip.setVisibility(View.VISIBLE);
                advertSkipView = new AdvertSkipView(6000, 1000, tvAdvertSkip);
                advertSkipView.setAdvertSkipFinishListener(new AdvertSkipView.AdvertSkipFinishListener() {
                    @Override
                    public void onFinish() {
                        goNext("");
                    }
                });
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
            MainActivity.forward(mContext);
            overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
        } else if (mGoType == 1) {
            LoginActivity.forward(phone);
            overridePendingTransition(R.anim.anim_fade_in, R.anim.anim_fade_out);
        } else if (mGoType == 2) {
            wxAuth();
        }
//        else if (mGoType == 3) {
//            ToastUtil.show("暂未注册,请先绑定手机号");
//            AppConfig.getInstance().getUserBean();
//            BindPhoneActivity.forward(this, wxUid);
//        }
    }

    private void goNext(String slideUrl) {
        if (TextUtils.isEmpty(slideUrl)) {
            //广告url 为空 直接跳转
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
        //广告webActivity  跳转
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
        DialogUtil.showSimpleDialog(LauncherActivity.this, "暂未绑定微信,请先绑定微信", new DialogUtil.SimpleCallback2() {
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
                        HttpUtil.bindWx(map.get("openid"), map.get("unionid"), map.get("name"), map.get("iconurl"), new HttpCallback() {
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


    public void prepareFinish() {
        if (isGrand && isPlayEnd && isLoadConfigFinish) {
            ConfigBean configBean = AppConfig.getInstance().getConfig();
            if (isNeedVersion && "1".equals(configBean.getIsNeedUpdate())) {
                VersionUtil.showDialog(mContext, false, configBean.getVersion(), configBean.getUpdateDes(), (view, object) -> {
                    downloadNewApk(configBean);
                });
            } else {
                showAdvertise();
            }
        }
    }

    private void downloadNewApk(ConfigBean configBean) {
        String url = configBean.getUpdateApkUrl();
        if (!TextUtils.isEmpty(url)) {
            try {
                DownloadUtil downloadUtil = new DownloadUtil();
                downloadLayout.setVisibility(View.VISIBLE);
                downloadUtil.download(Constants.APP_APK, mContext.getFilesDir(), Constants.APK_FILE_NAME, url, new DownloadUtil.Callback() {
                    @Override
                    public void onSuccess(File file) {
                        downloadLayout.setVisibility(View.GONE);
                        VersionUtil.installNormal(LauncherActivity.this, file.getPath());
                    }

                    @Override
                    public void onProgress(int progress) {
                        loadPb.setProgress(progress);
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.show("下载失败");
                        downloadLayout.setVisibility(View.GONE);
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
