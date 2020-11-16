package com.wwsl.wgsj.adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.dueeeke.videoplayer.player.VideoView;
import com.frame.fire.util.LogUtils;
import com.wwsl.wgsj.Constants;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.bean.VideoBean;
import com.wwsl.wgsj.http.HttpConst;
import com.wwsl.wgsj.http.HttpUtil;
import com.wwsl.wgsj.utils.cache.PreloadManager;
import com.wwsl.wgsj.utils.tiktok.OnVideoLayoutClickListener;
import com.wwsl.wgsj.utils.tiktok.TikTokController;
import com.wwsl.wgsj.utils.tiktok.TikTokRenderViewFactory;
import com.wwsl.wgsj.views.TikTokView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

public class VideoAdapter extends BaseQuickAdapter<VideoBean, BaseViewHolder> {

    private final static String TAG = "VideoAdapter";
    private OnVideoLayoutClickListener listener;
    private TikTokRenderViewFactory tikTokRenderViewFactory;
    private Context mContext;
    private int videoType;
    private boolean hasPadding = false;

    public VideoAdapter(@Nullable List<VideoBean> data, Context context, int videoType) {
        super(R.layout.item_tik_tok, data);
        tikTokRenderViewFactory = TikTokRenderViewFactory.create();
        mContext = context;
        this.videoType = videoType;
    }

    public void setLayoutClickListener(OnVideoLayoutClickListener listener) {
        this.listener = listener;
    }

    public void setHasPadding(boolean hasPadding) {
        this.hasPadding = hasPadding;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, VideoBean item) {
        TikTokView view = helper.getView(R.id.tiktok_View);
        view.setVideoData(item, videoType);
        view.setListener(listener);
        VideoView mVideoView = helper.getView(R.id.videoView);
        mVideoView.setRenderViewFactory(tikTokRenderViewFactory);
        mVideoView.setLooping(true);
        TikTokController mController = new TikTokController(mContext);
        mVideoView.setVideoController(mController);
        String playUrl = PreloadManager.getInstance(mContext).getPlayUrl(item.getVideoUrl());
        mVideoView.setUrl(playUrl);
        mController.addControlComponent(view, true);
    }

    @Override
    public void onViewAttachedToWindow(@NonNull BaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(@NonNull BaseViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        TikTokView view = holder.getView(R.id.tiktok_View);
        view.clear();
        VideoView mVideoView = holder.getView(R.id.videoView);
        if (mVideoView.isPlaying()) {
            LogUtils.e(TAG, "onViewDetachedFromWindow: pause");
            mVideoView.pause();
        }
    }


    @Override
    protected void convert(@NotNull BaseViewHolder holder, VideoBean item, @NotNull List<?> payloads) {
        for (Object p : payloads) {
            int payload = (int) p;
            TikTokView view = holder.getView(R.id.tiktok_View);
            if (payload == PAYLOAD_FOLLOW) {
                view.updateFollow(item.getAttent());
            } else if (payload == PAYLOAD_LIKE) {
                view.updateLike(item.getLikeNum(), item.getLike());
            } else if (payload == PAYLOAD_COMMENT) {
                view.updateComment(item.getCommentNum());
            } else if (payload == PAYLOAD_SHARE) {
                view.updateShares(item.getShareNum());
            }
        }
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onViewRecycled(@NonNull BaseViewHolder holder) {
        int position = holder.getLayoutPosition();
        VideoView view = holder.getView(R.id.videoView);
        view.release();
        if (position < getData().size()) {
            //取消预加载
            PreloadManager.getInstance(mContext).removePreloadTask(getData().get(position).getVideoUrl());
            HttpUtil.cancel(String.format("%d%s", Constants.FOLLOW_FROM_VIDEO_LIKE, getData().get(position).getId()));
        }

        HttpUtil.cancel(HttpConst.SET_ATTENTION);

    }

    public void onFollowChanged(String toUid, int isAttention) {
        List<VideoBean> data = getData();
        for (int i = 0; i < data.size(); i++) {
            if (toUid.equals(data.get(i).getUid())) {
                getData().get(i).setAttent(isAttention);
                notifyItemChanged(i, PAYLOAD_FOLLOW);
            }
        }
    }


    public static final int PAYLOAD_FOLLOW = 901;
    public static final int PAYLOAD_LIKE = 902;
    public static final int PAYLOAD_COMMENT = 903;
    public static final int PAYLOAD_SHARE = 904;

    public void onlike(String id, String likeNum, int like) {
        List<VideoBean> data = getData();
        for (int i = 0; i < data.size(); i++) {
            if (id.equals(data.get(i).getId())) {
                getData().get(i).setLikeNum(likeNum);
                getData().get(i).setLike(like);
                notifyItemChanged(i, PAYLOAD_LIKE);
                break;
            }
        }
    }

    public void onComment(String id, String num) {
        List<VideoBean> data = getData();
        for (int i = 0; i < data.size(); i++) {
            if (id.equals(data.get(i).getId())) {
                getData().get(i).setCommentNum(num);
                notifyItemChanged(i, PAYLOAD_COMMENT);
                break;
            }
        }
    }


    public void onShare(String id, String num) {
        List<VideoBean> data = getData();
        for (int i = 0; i < data.size(); i++) {
            if (id.equals(data.get(i).getId())) {
                getData().get(i).setShareNum(num);
                notifyItemChanged(i, PAYLOAD_SHARE);
                break;
            }
        }
    }
}