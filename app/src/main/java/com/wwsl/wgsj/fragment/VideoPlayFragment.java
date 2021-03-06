package com.wwsl.wgsj.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.recyclerview.widget.OrientationHelper;
import androidx.viewpager.widget.ViewPager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.bytedance.sdk.openadsdk.AdSlot;
import com.bytedance.sdk.openadsdk.TTAdNative;
import com.bytedance.sdk.openadsdk.TTNativeExpressAd;
import com.dueeeke.videoplayer.player.VideoView;
import com.frame.fire.util.LogUtils;
import com.permissionx.guolindev.PermissionX;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.tencent.ugc.TXVideoEditConstants;
import com.tencent.ugc.TXVideoEditer;
import com.tencent.ugc.TXVideoJoiner;
import com.wwsl.wgsj.AppConfig;
import com.wwsl.wgsj.Constants;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.activity.MainActivity;
import com.wwsl.wgsj.activity.common.WebViewActivity;
import com.wwsl.wgsj.activity.message.ChatRoomActivity;
import com.wwsl.wgsj.activity.video.TakeVideoWithSameMusicActivity;
import com.wwsl.wgsj.activity.video.VideoReportActivity;
import com.wwsl.wgsj.ad.TTAdManagerHolder;
import com.wwsl.wgsj.adapter.VideoAdapter;
import com.wwsl.wgsj.base.BaseFragment;
import com.wwsl.wgsj.bean.KeyValueBean;
import com.wwsl.wgsj.bean.ShareBean;
import com.wwsl.wgsj.bean.UserBean;
import com.wwsl.wgsj.bean.VideoBean;
import com.wwsl.wgsj.bean.VideoCommentBean;
import com.wwsl.wgsj.bean.net.NetFriendBean;
import com.wwsl.wgsj.dialog.VideoGiftDialogFragment;
import com.wwsl.wgsj.dialog.VideoInputDialogFragment;
import com.wwsl.wgsj.event.CommentDialogEvent;
import com.wwsl.wgsj.event.DialogShowEvent;
import com.wwsl.wgsj.event.FollowEvent;
import com.wwsl.wgsj.event.MainPageChangeEvent;
import com.wwsl.wgsj.event.VideoCommentEvent;
import com.wwsl.wgsj.event.VideoLikeEvent;
import com.wwsl.wgsj.http.HttpCallback;
import com.wwsl.wgsj.http.HttpConst;
import com.wwsl.wgsj.http.HttpUtil;
import com.wwsl.wgsj.im.ImChatFacePagerAdapter;
import com.wwsl.wgsj.interfaces.CommonCallback;
import com.wwsl.wgsj.interfaces.IMainVideoPlayer;
import com.wwsl.wgsj.interfaces.OnFaceClickListener;
import com.wwsl.wgsj.share.ShareHelper;
import com.wwsl.wgsj.utils.CommonUtil;
import com.wwsl.wgsj.utils.DownloadUtil;
import com.wwsl.wgsj.utils.FileUriHelper;
import com.wwsl.wgsj.utils.FileUtil;
import com.wwsl.wgsj.utils.ScreenDimenUtil;
import com.wwsl.wgsj.utils.SnackBarUtil;
import com.wwsl.wgsj.utils.ToastUtil;
import com.wwsl.wgsj.utils.VideoEditUtil;
import com.wwsl.wgsj.utils.cache.PreloadManager;
import com.wwsl.wgsj.utils.cache.ProxyVideoCacheManager;
import com.wwsl.wgsj.utils.tiktok.OnVideoLayoutClickListener;
import com.wwsl.wgsj.views.MyRefreshHeader;
import com.wwsl.wgsj.views.VideoCommentViewHolder;
import com.wwsl.wgsj.views.dialog.OnDialogCallBackListener;
import com.wwsl.wgsj.views.viewpagerlayoutmanager.OnViewPagerListener;
import com.wwsl.wgsj.views.viewpagerlayoutmanager.ViewPagerLayoutManager;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

import com.zj.zjsdk.ad.ZjAdError;
import com.zj.zjsdk.ad.ZjSize;
import com.zj.zjsdk.ad.express.ZjExpressFeedFullVideo;
import com.zj.zjsdk.ad.express.ZjExpressFeedFullVideoAd;
import com.zj.zjsdk.ad.express.ZjExpressFeedFullVideoListener;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import cn.hutool.core.util.StrUtil;

/**
 * @author :
 * @date : 2020/6/17 16:31
 * @description : 短视频播放页
 */
public class VideoPlayFragment extends BaseFragment
    implements View.OnClickListener, OnFaceClickListener, OnDialogCallBackListener,
    IMainVideoPlayer {

  private DownloadUtil           mDownloadUtil;
  /**
   * 当前播放位置
   */
  private SmartRefreshLayout     swipeRefreshLayout;
  private SwipeRecyclerView      videoRecycler;
  private VideoAdapter           videoAdapter;
  private ViewPagerLayoutManager layoutManager;
  private int                    mCurPos         = 0;
  private List<VideoBean>        mVideoList      = new ArrayList<>();
  private int                    mPage           = 1;
  private int                    type            = HttpConst.VIDEO_TYPE_FOLLOW;
  private int                    videoStartIndex = 0;

  private PreloadManager           mPreloadManager;
  private VideoCommentViewHolder   mVideoCommentViewHolder;
  private VideoInputDialogFragment mVideoInputDialogFragment;
  private View                     mFaceView;//表情面板
  private int                      mFaceHeight;//表情面板高度

  private VideoView   curVideoView;
  private ShareDialog shareDialog;

  private String mGenerateVideoPath, mJoinVideoPath;
  private final int        onSuccessFile      = 96;
  private final int        onGenerateComplete = 98;
  private       TTAdNative mTTAdNative;

  private Handler handler = new Handler(msg -> {
    switch (msg.what) {
      case onSuccessFile:
        File file = (File) msg.obj;
        if (file == null || !file.exists()) return true;
        addWaterMask(file);
        break;
      case onGenerateComplete:
        if (TextUtils.isEmpty(mGenerateVideoPath)) return true;
        videoJoin();
        break;
    }
    return true;
  });

  // TODO: 2020/9/26 仿抖音结尾logo
  private void videoJoin() {
    TXVideoJoiner mTXVideoJoiner = new TXVideoJoiner(mContext);
    List<String> urls = new ArrayList<>();
    urls.add(mGenerateVideoPath);
    urls.add(AppConfig.joinPath);
    mTXVideoJoiner.setVideoPathList(urls);
    mTXVideoJoiner.setVideoJoinerListener(new TXVideoJoiner.TXVideoJoinerListener() {
      @Override
      public void onJoinProgress(float v) {
        //                int p = (int) (v * 100);
      }

      @Override
      public void onJoinComplete(TXVideoEditConstants.TXJoinerResult txJoinerResult) {
        if (txJoinerResult.retCode == 0) {
          LogUtils.e(TAG, "视频合成----->完成");
          new File(mGenerateVideoPath).delete();
          //刷新到最近列表
          mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
              Uri.fromFile(new File(mJoinVideoPath))));
        }
        dismissLoad();
        SnackBarUtil.ShortSnackbar(swipeRefreshLayout, mJoinVideoPath, SnackBarUtil.Info).show();
      }
    });
    mJoinVideoPath = VideoEditUtil.getInstance().generateVideoOutputPath();
    mTXVideoJoiner.joinVideo(TXVideoEditConstants.VIDEO_COMPRESSED_720P, mJoinVideoPath);
  }

  // TODO: 2020/9/26 视频加水印
  private void addWaterMask(File file) {
    TXVideoEditer videoEditer =
        VideoEditUtil.getInstance().createVideoEditer(mContext, file.getPath());
    videoEditer.setVideoProcessListener(new TXVideoEditer.TXVideoProcessListener() {
      @Override
      public void onProcessProgress(float v) {
        //                int p = (int) (v * 100);
      }

      @Override
      public void onProcessComplete(TXVideoEditConstants.TXGenerateResult txGenerateResult) {
        if (txGenerateResult.retCode == 0) {
          LogUtils.e(TAG, "视频预处理----->完成");
          mGenerateVideoPath = VideoEditUtil.getInstance().generateVideoOutputPath();
          videoEditer.generateVideo(TXVideoEditConstants.VIDEO_COMPRESSED_720P, mGenerateVideoPath);
        } else {
          dismissLoad();
        }
      }
    });
    videoEditer.setVideoGenerateListener(new TXVideoEditer.TXVideoGenerateListener() {
      @Override
      public void onGenerateProgress(float v) {
        //                int p = (int) (v * 100);
      }

      @Override
      public void onGenerateComplete(TXVideoEditConstants.TXGenerateResult txGenerateResult) {
        if (txGenerateResult.retCode == 0) {
          LogUtils.e(TAG, "视频保存----->完成");
          videoEditer.release();
          file.delete();
          handler.sendEmptyMessage(onGenerateComplete);
        } else {
          dismissLoad();
        }
      }
    });
    TXVideoEditConstants.TXRect rect = new TXVideoEditConstants.TXRect();
    rect.x = 0.1f;
    rect.y = 0.1f;
    rect.width = 0.1f;
    Bitmap bitmap =
        BitmapFactory.decodeResource(getActivity().getResources(), R.mipmap.icon_login_logo);
    videoEditer.setWaterMark(bitmap, rect);

    videoEditer.processVideo();
  }

  public void setType(int type) {
    this.type = type;
    videoStartIndex = type << 10;
  }

  @Override
  protected int setLayoutId() {
    return R.layout.fragment_video_play;
  }

  @Override
  protected void init() {
    EventBus.getDefault().register(this);
    findView();
    mPreloadManager = PreloadManager.getInstance(mContext);
    mDownloadUtil = new DownloadUtil();
    mTTAdNative = TTAdManagerHolder.get().createAdNative(mContext);
    initView();
    initListener();
  }

  private void initListener() {

    layoutManager.setOnViewPagerListener(new OnViewPagerListener() {
      @Override
      public void onInitComplete() {
        if (videoRecycler == null || videoRecycler.getChildAt(0) == null) {
          return;
        }
        curVideoView = videoRecycler.getChildAt(0).findViewById(R.id.videoView);
        if (curVideoView != null && isNeedShow()) {
          curVideoView.start();
        }
      }

      @Override
      public void onPageRelease(boolean isNext, int position) {

      }

      @Override
      public void onPageSelected(int position, boolean isBottom) {
        if (mCurPos != position) {
          //如果当前position 和 上一次固定后的position 相同, 说明是同一个, 只不过滑动了一点点, 然后又释放了
          VideoBean videoBean = videoAdapter.getData().get(position);
          if (videoBean.getItemType() != 99) {
            curVideoView = (VideoView) videoAdapter.getViewByPosition(position, R.id.videoView);
            if (curVideoView != null) {
              LogUtils.e(
                  "startPlay: " + "position: " + position + "  url: " + mVideoList.get(position)
                      .getVideoUrl());
              curVideoView.start();
            }
          }
        }

        mCurPos = position;

        if (mVideoList.size() > 5 && position == mVideoList.size() - 2) {
          loadMoreData(false);
        }
      }
    });

    swipeRefreshLayout.setRefreshHeader(new MyRefreshHeader(getContext()));
    swipeRefreshLayout.setRefreshFooter(new ClassicsFooter(getContext()));
    swipeRefreshLayout.setOnRefreshListener(this::freshData);
    swipeRefreshLayout.setOnLoadMoreListener(this::loadMore);

    shareDialog.setListener((view, object) -> {
      if (object instanceof NetFriendBean) {
        NetFriendBean friend = (NetFriendBean) object;
        ChatRoomActivity.forward(getContext(), UserBean.builder()
            .id(friend.getTouid())
            .avatar(friend.getAvatar())
            .username(friend.getUsername())
            .build(), mVideoList.get(mCurPos), true);
      } else if (object instanceof ShareBean) {
        ShareBean shareBean = (ShareBean) object;
        if (shareBean.getType() == Constants.SAVE_LOCAL) {
          saveVideo();
        } else if (shareBean.getType() == Constants.VIDEO_REPORT) {
          VideoReportActivity.forward(mContext, mVideoList.get(mCurPos).getId());
        } else {
          ShareHelper.shareVideo((MainActivity) mContext, shareBean, mVideoList.get(mCurPos));
        }
      }
    });
  }

  private void loadMore(RefreshLayout refreshLayout) {
    mPage++;
    loadMoreData(true);
  }

  private void freshData(RefreshLayout refreshLayout) {
    mPage = 1;
    loadData(true);
  }

  private void saveVideo() {
    PermissionX.init(this)
        .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_PHONE_STATE)
        .request((allGranted, grantedList, deniedList) -> {
          if (allGranted) {
            VideoBean videoBean = videoAdapter.getData().get(mCurPos);
            showLoadCancelable(false, "保存视频中...");
            mDownloadUtil.download(videoBean.getId(), AppConfig.VIDEO_PATH,
                Calendar.getInstance().getTimeInMillis() + ".mp4", videoBean.getVideoUrl(),
                new DownloadUtil.Callback() {
                  @Override
                  public void onSuccess(File file) {
                    if (file != null) {
                      FileUtil.saveVideo(mContext, file);

                      Message msg = handler.obtainMessage();
                      msg.what = onSuccessFile;
                      msg.obj = file;
                      handler.sendMessage(msg);

                      new FileUriHelper(mContext).copyFilesFromRaw(mContext, R.raw.join_video_end);
                    }
                  }

                  @Override
                  public void onProgress(int progress) {
                  }

                  @Override
                  public void onError(Throwable e) {
                    dismissLoad();
                    SnackBarUtil.ShortSnackbar(swipeRefreshLayout, "下载失败", SnackBarUtil.Alert)
                        .show();
                  }
                });
          }
        });
  }

  @SuppressLint("DefaultLocale")
  private void initView() {

    OnVideoLayoutClickListener onVideoLayoutClickListener = new OnVideoLayoutClickListener() {

      @Override
      public void onClickEvent(int type, VideoBean bean) {
        switch (type) {
          case Constants.VIDEO_CLICK_HEAD:
            EventBus.getDefault().post(new MainPageChangeEvent(2, 1));
            break;
          case Constants.VIDEO_CLICK_LIKE:
            HttpUtil.setVideoLike(
                String.format("%d%s", Constants.FOLLOW_FROM_VIDEO_LIKE, bean.getId()), bean.getId(),
                new HttpCallback() {
                  @Override
                  public void onSuccess(int code, String msg, String[] info) {
                    if (code == 0 && info.length > 0) {
                      JSONObject obj = JSON.parseObject(info[0]);
                      String likeNum = obj.getString("likes");
                      int like = obj.getIntValue("islike");
                      videoAdapter.onlike(bean.getId(), likeNum, like);
                    }
                  }
                });
            break;
          case Constants.VIDEO_CLICK_SHARE:
            shareDialog.show(getChildFragmentManager(),
                "shareDialog" + VideoPlayFragment.this.type);
            break;
          case Constants.VIDEO_CLICK_COMMENT:
            openCommentWindow(bean);
            break;
          case Constants.VIDEO_CLICK_FOLLOW:
            HttpUtil.setAttention(Constants.FOLLOW_FROM_VIDEO_PLAY, bean.getUid(),
                new CommonCallback<Integer>() {
                  @Override
                  public void callback(Integer bean) {
                    //                        ToastUtil.show(bean == 1 ? "关注成功" : "取消关注成功");
                  }
                });
            break;
          case Constants.VIDEO_CLICK_TITLE:
            break;
          case Constants.VIDEO_CLICK_MUSIC:
            if (null != bean.getMusicInfo()) {
              TakeVideoWithSameMusicActivity.forward(getContext(), bean.getMusicInfo());
            } else {
              ToastUtil.show("当前音乐不可编辑");
            }
            break;
          case Constants.VIDEO_CLICK_AD:
            if (!StrUtil.isEmpty(bean.getAdUrl())) {
              WebViewActivity.forward2(getContext(), bean.getAdUrl());
            }
            break;
          case Constants.VIDEO_CLICK_GOODS:
            if (!StrUtil.isEmpty(bean.getGoodsId()) && bean.getGoods() != null && !StrUtil.isEmpty(
                bean.getGoods().getUrl())) {
              WebViewActivity.forward(getContext(), bean.getGoods().getUrl());
            }
            break;
          case Constants.VIDEO_CLICK_DS:
            openGiftDialog(bean.getId(), bean.getUid());
            break;
        }
      }
    };

    layoutManager = new ViewPagerLayoutManager(getContext(), OrientationHelper.VERTICAL);
    videoAdapter = new VideoAdapter(mVideoList, getContext(), type);
    videoAdapter.setLayoutClickListener(onVideoLayoutClickListener);
    videoRecycler.setLayoutManager(layoutManager);
    videoRecycler.setAdapter(videoAdapter);

    shareDialog = new ShareDialog();
  }

  private void openGiftDialog(String id, String uid) {

    VideoGiftDialogFragment fragment = new VideoGiftDialogFragment();
    Bundle bundle = new Bundle();
    bundle.putString(Constants.VIDEO_DS_ID, id);
    bundle.putString(Constants.VIDEO_DS_UID, uid);
    fragment.setArguments(bundle);
    fragment.show(((MainActivity) mContext).getSupportFragmentManager(), "VideoGiftDialogFragment");
  }

  private void findView() {
    swipeRefreshLayout = (SmartRefreshLayout) findViewById(R.id.videoFreshLayout);
    videoRecycler = (SwipeRecyclerView) findViewById(R.id.videoRecycler);
  }

  @Override
  public boolean onBackPressed() {
    if (mVideoCommentViewHolder != null) {
      if (mVideoCommentViewHolder.isShowing()) {
        mVideoCommentViewHolder.hideBottom();
        return true;
      }
    }
    return super.onBackPressed();
  }

  @Override
  public void onResume() {
    super.onResume();
    LogUtils.e(TAG, "VideoPlayFragment onResume: " + type);
    if (!isNeedShow()) return;

    //返回时，推荐页面可见，则继续播放视频
    if (curVideoView != null) {
      if (curVideoView.isPlaying()) {
        curVideoView.resume();
      } else {
        curVideoView.start();
      }
    }
  }

  @Override
  public void onPause() {
    super.onPause();
    LogUtils.e(TAG, "onPause: " + type);
    if (curVideoView != null) {
      curVideoView.pause();
    }
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (curVideoView != null) {
      curVideoView.release();
    }
    HttpUtil.cancel(HttpConst.GET_VIDEO_LIST);
    mPreloadManager.removeAllPreloadTask();
    //清除缓存，实际使用可以不需要清除，这里为了方便测试
    ProxyVideoCacheManager.clearAllCache(mContext);
    EventBus.getDefault().unregister(this);
  }

  @Override
  public String getCurrentVideoUserId() {
    if (mVideoList.size() > 0) {
      return mVideoList.get(mCurPos).getUid();
    } else {
      return "";
    }
  }

  @Override
  public void getNewestVideo() {
    initialData();
  }

  public String getCurrentVideoId() {
    if (mVideoList.size() > mCurPos) {
      return mVideoList.get(mCurPos).getId();
    } else {
      return "";
    }
  }

  private final static String TAG = "VideoPlayFragment";

  /**
   * 显示评论
   */
  public void openCommentWindow(VideoBean videoBean) {
    if (mVideoCommentViewHolder == null) {
      mVideoCommentViewHolder = new VideoCommentViewHolder(mContext,
          ((MainActivity) mContext).findViewById(R.id.rootView));
      mVideoCommentViewHolder.addToParent();
    }
    mVideoCommentViewHolder.setCallBackListener(this);
    mVideoCommentViewHolder.setVideoBean(videoBean);
    mVideoCommentViewHolder.showBottom();
  }

  /**
   * 隐藏评论
   */
  public void hideCommentWindow() {
    if (mVideoCommentViewHolder != null) {
      mVideoCommentViewHolder.hideBottom();
    }
    mVideoInputDialogFragment = null;
  }

  public View getFaceView() {
    if (mFaceView == null) {
      mFaceView = initFaceView();
    }
    return mFaceView;
  }

  /**
   * 打开评论输入框
   */
  public void openCommentInputWindow(boolean openFace, VideoBean videoBean, VideoCommentBean bean) {
    if (AppConfig.getInstance().getUserBean().getCanComment() <= 0) {
      ToastUtil.show("没有评论权限");
      return;
    }

    if (mFaceView == null) {
      mFaceView = initFaceView();
    }

    VideoInputDialogFragment fragment = new VideoInputDialogFragment();
    fragment.setVideoBean(videoBean);
    fragment.setFaceView(getFaceView(), this);
    Bundle bundle = new Bundle();
    bundle.putBoolean(Constants.VIDEO_FACE_OPEN, openFace);
    bundle.putInt(Constants.VIDEO_FACE_HEIGHT, mFaceHeight);
    bundle.putParcelable(Constants.VIDEO_COMMENT_BEAN, bean);
    fragment.setArguments(bundle);
    mVideoInputDialogFragment = fragment;
    fragment.show(((MainActivity) mContext).getSupportFragmentManager(),
        "VideoInputDialogFragment");
  }

  @Override
  public void onClick(View v) {
    if (v.getId() == R.id.btn_send) {
      if (mVideoInputDialogFragment != null) {
        mVideoInputDialogFragment.sendComment();
      }
    }
  }

  @Override
  public void onFaceClick(String str, int faceImageRes) {
    if (mVideoInputDialogFragment != null) {
      mVideoInputDialogFragment.onFaceClick(str, faceImageRes);
    }
  }

  @Override
  public void onFaceDeleteClick() {
    if (mVideoInputDialogFragment != null) {
      mVideoInputDialogFragment.onFaceDeleteClick();
    }
  }

  /**
   * 初始化表情控件
   */
  private View initFaceView() {
    LayoutInflater inflater = LayoutInflater.from(mContext);
    View v = inflater.inflate(R.layout.view_chat_face, null);
    v.measure(0, 0);
    mFaceHeight = v.getMeasuredHeight();
    v.findViewById(R.id.btn_send).setOnClickListener(this);
    final RadioGroup radioGroup = (RadioGroup) v.findViewById(R.id.radio_group);
    ViewPager viewPager = (ViewPager) v.findViewById(R.id.viewPager);
    viewPager.setOffscreenPageLimit(10);

    ImChatFacePagerAdapter adapter = new ImChatFacePagerAdapter(mContext, this);
    viewPager.setAdapter(adapter);
    viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
      @Override
      public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

      }

      @Override
      public void onPageSelected(int position) {
        ((RadioButton) radioGroup.getChildAt(position)).setChecked(true);
      }

      @Override
      public void onPageScrollStateChanged(int state) {

      }
    });

    for (int i = 0, pageCount = adapter.getCount(); i < pageCount; i++) {
      RadioButton radioButton =
          (RadioButton) inflater.inflate(R.layout.view_chat_indicator, radioGroup, false);
      radioButton.setId(i + 10000);
      if (i == 0) {
        radioButton.setChecked(true);
      }
      radioGroup.addView(radioButton);
    }
    return v;
  }

  @Override
  public void onDialogViewClick(View view, Object object) {
    if (object instanceof CommentDialogEvent) {
      CommentDialogEvent event = (CommentDialogEvent) object;
      openCommentInputWindow(event.isOpenFace(), event.getVideoBean(), event.getCommentBean());
    } else if (object instanceof DialogShowEvent) {
      DialogShowEvent event = (DialogShowEvent) object;
      if (event.getTag().equals(Constants.VIDEO_COMMENT_DIALOG)) {
        hideCommentWindow();
      }
    } else if (object instanceof KeyValueBean) {
      KeyValueBean event = (KeyValueBean) object;
      switch (event.getKey()) {
        case Constants.KEY_VIDEO_COMMENT_NUM:
          //刷新短视频播放列表评论数据
          List<String> value = (List<String>) event.getValue();
          videoAdapter.onComment(value.get(0), value.get(1));
          //刷新评论页面显示
          if (mVideoCommentViewHolder != null) {
            mVideoCommentViewHolder.updateData();
          }
          break;
      }
    }
  }

  @Override
  public void initialData() {
    swipeRefreshLayout.autoRefresh();
  }

  private void refreshBack(int code, String[] info, String msg) {
    if (code == 0 && info != null && info.length > 0) {
      List<VideoBean> videoBeans = JSON.parseArray(Arrays.toString(info), VideoBean.class);
      int startIndex = mVideoList.size();
      if (startIndex > 0) {
        for (int i = 0; i < mVideoList.size(); i++) {
          mPreloadManager.removePreloadTask(mVideoList.get(i).getVideoUrl());
        }
        mVideoList.clear();
      }
      for (int i = 0, j = startIndex; i < videoBeans.size(); i++) {
        mPreloadManager.addPreloadTask(videoBeans.get(i).getVideoUrl(), (videoStartIndex + j++));
      }
      videoAdapter.addData(videoBeans);
      if (type == HttpConst.VIDEO_TYPE_HOT) {
        loadAd();
      }
      if (videoAdapter.getData().size() > 0) {
        videoRecycler.scrollToPosition(0);
      }
    } else {
      ToastUtil.show(msg);
    }
    swipeRefreshLayout.finishRefresh(true);
  }

  public void loadMoreAd(int preSize, int newDataSize) {
    int width = ScreenDimenUtil.getInstance().getScreenWidth();
    int height = ScreenDimenUtil.getInstance().getScreenHeight() - 10;
    ZjSize size = new ZjSize(width, height);
    ZjExpressFeedFullVideo expressFeedFullVideo =
        new ZjExpressFeedFullVideo(getActivity(), Constants.AD_VIDEO_LIST_ID, size,
            new ZjExpressFeedFullVideoListener() {

              @Override
              public void onZjFeedFullVideoLoad(List<ZjExpressFeedFullVideoAd> ads) {
                LogUtils.e("myth", "onZjFeedFullVideoLoad.ads.size=" + ads.size());
                if (newDataSize != 10 && newDataSize > 2) {
                  if (ads.size() >= 1) {
                    VideoBean bean = new VideoBean();
                    bean.setId(System.currentTimeMillis() + "");
                    bean.setIsZn("0");
                    bean.setIsPublic("0");
                    bean.setIsAd("99");
                    bean.setAdItem(ads.get(0));
                    videoAdapter.addData(preSize + 1, bean);
                  }
                } else {
                  if (ads.size() == 1) {
                    VideoBean bean = new VideoBean();
                    bean.setId(System.currentTimeMillis() + "");
                    bean.setIsZn("0");
                    bean.setIsPublic("0");
                    bean.setIsAd("99");
                    bean.setAdItem(ads.get(0));
                    videoAdapter.addData(preSize + 4, bean);
                  } else if (ads.size() == 2) {
                    VideoBean bean1 = new VideoBean();
                    bean1.setId(System.currentTimeMillis() + "");
                    bean1.setIsZn("0");
                    bean1.setIsPublic("0");
                    bean1.setIsAd("99");
                    bean1.setAdItem(ads.get(0));
                    videoAdapter.addData(preSize + 2, bean1);
                    VideoBean bean2 = new VideoBean();
                    bean2.setId(System.currentTimeMillis() + "");
                    bean2.setIsZn("0");
                    bean2.setIsPublic("0");
                    bean2.setIsAd("99");
                    bean2.setAdItem(ads.get(1));
                    videoAdapter.addData(preSize + 4, bean2);
                  }
                }
                //穿山甲
                loadMoreAd2(preSize, newDataSize, width, height);
              }

              @Override
              public void onZjAdError(ZjAdError error) {
                LogUtils.e("myth", "onZjFeedFullVideoLoad.error=" + error.getErrorMsg());
              }
            });
    expressFeedFullVideo.loadAd(2);
  }

  private void loadMoreAd2(int preSize, int newDataSize, int width, int height) {
    AdSlot adSlot = new AdSlot.Builder()
        .setCodeId("945661536")
        .setExpressViewAcceptedSize(width, height) //期望模板广告view的size,单位dp
        .setAdCount(2) //请求广告数量为1到3条
        .build();
    mTTAdNative.loadExpressDrawFeedAd(adSlot, new TTAdNative.NativeExpressAdListener() {
      @Override
      public void onError(int i, String s) {
        LogUtils.e("myth", "穿山甲-->onError: code->" + i + ",msg:" + s);
      }

      @Override
      public void onNativeExpressAdLoad(List<TTNativeExpressAd> list) {

        if (newDataSize != 10 && newDataSize > 2) {
          if (list.size() >= 1) {
            VideoBean bean = new VideoBean();
            bean.setId(System.currentTimeMillis() + "");
            bean.setIsZn("0");
            bean.setIsPublic("0");
            bean.setIsAd("98");
            bean.setCAdItem(list.get(0));
            videoAdapter.addData(bean);
          }
          return;
        }

        if (list.size() == 1) {
          VideoBean bean = new VideoBean();
          bean.setId(System.currentTimeMillis() + "");
          bean.setIsZn("0");
          bean.setIsPublic("0");
          bean.setIsAd("98");
          bean.setCAdItem(list.get(0));
          videoAdapter.addData(preSize, bean);
        } else if (list.size() == 2) {
          VideoBean bean1 = new VideoBean();
          bean1.setId(System.currentTimeMillis() + "");
          bean1.setIsZn("0");
          bean1.setIsPublic("0");
          bean1.setIsAd("98");
          bean1.setCAdItem(list.get(0));
          videoAdapter.addData(preSize + 7, bean1);
          VideoBean bean2 = new VideoBean();
          bean2.setId(System.currentTimeMillis() + "");
          bean2.setIsZn("0");
          bean2.setIsPublic("0");
          bean2.setIsAd("98");
          bean2.setCAdItem(list.get(1));
          videoAdapter.addData(preSize + 9, bean2);
        }
      }
    });
  }

  public void loadAd() {
    //众简
    int itemSize = videoAdapter.getData().size();
    if (itemSize <= 0) return;
    int width = ScreenDimenUtil.getInstance().getScreenWidth();
    int height = ScreenDimenUtil.getInstance().getScreenHeight() - 10;
    ZjSize size = new ZjSize(width, height);
    ZjExpressFeedFullVideo expressFeedFullVideo =
        new ZjExpressFeedFullVideo(getActivity(), Constants.AD_VIDEO_LIST_ID, size,
            new ZjExpressFeedFullVideoListener() {
              @Override
              public void onZjFeedFullVideoLoad(List<ZjExpressFeedFullVideoAd> ads) {
                LogUtils.e("myth", "onZjFeedFullVideoLoad.ads.size=" + ads.size());
                int itemSize = videoAdapter.getData().size();
                if (itemSize != 10) {
                  if (ads.size() > 0) {
                    VideoBean bean = new VideoBean();
                    bean.setId(System.currentTimeMillis() + "");
                    bean.setIsZn("0");
                    bean.setIsPublic("0");
                    bean.setIsAd("99");
                    bean.setAdItem(ads.get(0));
                    videoAdapter.addData(bean);
                  }
                } else {
                  if (ads.size() == 1) {
                    VideoBean bean = new VideoBean();
                    bean.setId(System.currentTimeMillis() + "");
                    bean.setIsZn("0");
                    bean.setIsPublic("0");
                    bean.setIsAd("99");
                    bean.setAdItem(ads.get(0));
                    videoAdapter.addData(5, bean);
                  } else if (ads.size() == 2) {
                    VideoBean bean1 = new VideoBean();
                    bean1.setId(System.currentTimeMillis() + "");
                    bean1.setIsZn("0");
                    bean1.setIsPublic("0");
                    bean1.setIsAd("99");
                    bean1.setAdItem(ads.get(0));
                    videoAdapter.addData(2, bean1);
                    VideoBean bean2 = new VideoBean();
                    bean2.setId(System.currentTimeMillis() + "");
                    bean2.setIsZn("0");
                    bean2.setIsPublic("0");
                    bean2.setIsAd("99");
                    bean2.setAdItem(ads.get(1));
                    videoAdapter.addData(6, bean2);
                  }
                }
                loadAd2(width, height);
              }

              @Override
              public void onZjAdError(ZjAdError error) {
                LogUtils.e("myth", "onZjFeedFullVideoLoad.error=" + error.getErrorMsg());
              }
            });
    expressFeedFullVideo.loadAd(2);
    //穿山甲

  }

  private void loadAd2(int width, int height) {
    AdSlot adSlot = new AdSlot.Builder()
        .setCodeId("945661536")
        .setExpressViewAcceptedSize(width, height) //期望模板广告view的size,单位dp
        .setAdCount(2) //请求广告数量为1到3条
        .build();

    mTTAdNative.loadExpressDrawFeedAd(adSlot, new TTAdNative.NativeExpressAdListener() {
      @Override
      public void onError(int i, String s) {
        LogUtils.e("myth", "穿山甲-->onError: code->" + i + ",msg:" + s);
      }

      @Override
      public void onNativeExpressAdLoad(List<TTNativeExpressAd> list) {
        if (videoAdapter.getData().size() < 10) {
          return;
        }

        if (list.size() == 1) {
          VideoBean bean = new VideoBean();
          bean.setId(System.currentTimeMillis() + "");
          bean.setIsZn("0");
          bean.setIsPublic("0");
          bean.setIsAd("98");
          bean.setCAdItem(list.get(0));
          videoAdapter.addData(bean);
        } else if (list.size() == 2) {
          VideoBean bean1 = new VideoBean();
          bean1.setId(System.currentTimeMillis() + "");
          bean1.setIsZn("0");
          bean1.setIsPublic("0");
          bean1.setIsAd("98");
          bean1.setCAdItem(list.get(0));
          videoAdapter.addData(videoAdapter.getData().size() * 3 / 4, bean1);
          VideoBean bean2 = new VideoBean();
          bean2.setId(System.currentTimeMillis() + "");
          bean2.setIsZn("0");
          bean2.setIsPublic("0");
          bean2.setIsAd("98");
          bean2.setCAdItem(list.get(1));
          videoAdapter.addData(bean2);
        }
      }
    });
  }

  private void loadMoreData(boolean showLoadTxt) {
    switch (type) {
      case HttpConst.VIDEO_TYPE_HOT:
        HttpUtil.getHotVideoList(type, mPage, new HttpCallback() {
          @Override
          public void onSuccess(int code, String msg, String[] info) {
            loadMoreBack(code, msg, info, showLoadTxt);
          }

          @Override
          public void onError() {
            swipeRefreshLayout.finishLoadMore(false);
          }
        });
        break;

      case HttpConst.VIDEO_TYPE_YXYP:
        HttpUtil.getYxYpVideoList(type, mPage, new HttpCallback() {
          @Override
          public void onSuccess(int code, String msg, String[] info) {
            loadMoreBack(code, msg, info, showLoadTxt);
          }

          @Override
          public void onError() {
            swipeRefreshLayout.finishLoadMore(false);
          }
        });
        break;
      case HttpConst.VIDEO_TYPE_ZHUNONG:
        HttpUtil.getZnVideoList(type, mPage, new HttpCallback() {
          @Override
          public void onSuccess(int code, String msg, String[] info) {
            loadMoreBack(code, msg, info, showLoadTxt);
          }

          @Override
          public void onError() {
            swipeRefreshLayout.finishLoadMore(false);
          }
        });
        break;
      case HttpConst.VIDEO_TYPE_FOLLOW:
        HttpUtil.getFollowVideoList(type, mPage, new HttpCallback() {
          @Override
          public void onSuccess(int code, String msg, String[] info) {
            loadMoreBack(code, msg, info, showLoadTxt);
          }

          @Override
          public void onError() {
            swipeRefreshLayout.finishLoadMore(false);
          }
        });
        break;
    }
  }

  private void loadMoreBack(int code, String msg, String[] info, boolean showLoadTxt) {

    if (code == 0 && info != null && info.length > 0) {
      List<VideoBean> newBeans = JSON.parseArray(Arrays.toString(info), VideoBean.class);
      for (int i = newBeans.size() - 1; i >= 0; i--) {
        if (mVideoList.contains(newBeans.get(i))) {
          newBeans.remove(i);
        }
      }
      for (int i = 0, j = mVideoList.size(); i < newBeans.size(); i++) {
        mPreloadManager.addPreloadTask(newBeans.get(i).getVideoUrl(), (videoStartIndex + j++));
      }
      //加个众简广告
      if (type == HttpConst.VIDEO_TYPE_HOT) {
        loadMoreAd(videoAdapter.getData().size(), newBeans.size());
      }
      videoAdapter.addData(newBeans);
      swipeRefreshLayout.finishLoadMore();
    } else {
      //加个众简广告
      DisplayMetrics dm = new DisplayMetrics();
      getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
      ZjSize size = new ZjSize(dm.widthPixels, CommonUtil.px2dip(mContext, dm.heightPixels) - 10);
      ZjExpressFeedFullVideo expressFeedFullVideo =
          new ZjExpressFeedFullVideo(getActivity(), Constants.AD_VIDEO_LIST_ID, size,
              new ZjExpressFeedFullVideoListener() {
                @Override
                public void onZjFeedFullVideoLoad(List<ZjExpressFeedFullVideoAd> ads) {
                  for (ZjExpressFeedFullVideoAd ksDrawAd : ads) {
                    if (ksDrawAd == null) {
                      continue;
                    }
                    VideoBean bean = new VideoBean();
                    bean.setId(System.currentTimeMillis() + "");
                    bean.setIsZn("0");
                    bean.setIsPublic("0");
                    bean.setIsAd("99");
                    bean.setAdItem(ksDrawAd);
                    videoAdapter.addData(bean);
                    break;
                  }
                }

                @Override
                public void onZjAdError(ZjAdError error) {
                  LogUtils.e("myth", "onZjFeedFullVideoLoad.error=" + error.getErrorMsg());
                }
              });
      expressFeedFullVideo.loadAd(1);
      swipeRefreshLayout.finishLoadMoreWithNoMoreData();
      if (showLoadTxt) {
        ToastUtil.show(msg);
      }
    }
  }

  //------------------------其他地方关注-喜欢-评论 操作响应------------------------------//

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onFollowEvent(FollowEvent e) {
    if (videoAdapter != null) {
      videoAdapter.onFollowChanged(e.getToUid(), e.getIsAttention());
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onFollowEvent(VideoLikeEvent e) {
    if (videoAdapter != null) {
      videoAdapter.onlike(e.getVideoId(), e.getLikeNum(), e.getIsLike());
    }
  }

  @Subscribe(threadMode = ThreadMode.MAIN)
  public void onFollowEvent(VideoCommentEvent e) {
    if (videoAdapter != null) {
      videoAdapter.onComment(e.getId(), e.getNum());
    }
  }

  //------------------------其他地方关注-喜欢-评论 操作响应------------------------------//

  private boolean isNeedShow() {
    MainActivity activity = (MainActivity) mContext;
    if (null != activity) {
      return activity.getCurrentItem() == 0;
    }
    return false;
  }

  public void loadData(boolean fresh) {
    switch (type) {
      case HttpConst.VIDEO_TYPE_HOT:
        HttpUtil.getHotVideoList(type, mPage, new HttpCallback() {
          @Override
          public void onSuccess(int code, String msg, String[] info) {
            refreshBack(code, info, msg);
          }

          @Override
          public void onError() {
            ToastUtil.show("网络请求失败,请稍后尝试");
            swipeRefreshLayout.finishRefresh(false);
          }
        });
        break;
      case HttpConst.VIDEO_TYPE_YXYP:
        HttpUtil.getYxYpVideoList(type, mPage, new HttpCallback() {
          @Override
          public void onSuccess(int code, String msg, String[] info) {
            refreshBack(code, info, msg);
          }

          @Override
          public void onError() {
            ToastUtil.show("网络请求失败,请稍后尝试");
            swipeRefreshLayout.finishRefresh(false);
          }
        });
        break;
      case HttpConst.VIDEO_TYPE_ZHUNONG:
        HttpUtil.getZnVideoList(type, mPage, new HttpCallback() {
          @Override
          public void onSuccess(int code, String msg, String[] info) {
            refreshBack(code, info, msg);
          }

          @Override
          public void onError() {
            ToastUtil.show("网络请求失败,请稍后尝试");
            swipeRefreshLayout.finishRefresh(false);
          }
        });
        break;
      case HttpConst.VIDEO_TYPE_FOLLOW:
        HttpUtil.getFollowVideoList(type, mPage, new HttpCallback() {
          @Override
          public void onSuccess(int code, String msg, String[] info) {
            refreshBack(code, info, msg);
          }

          @Override
          public void onError() {
            ToastUtil.show("网络请求失败,请稍后尝试");
            swipeRefreshLayout.finishRefresh(false);
          }
        });
        break;
    }
  }
}
