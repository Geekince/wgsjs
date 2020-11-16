package com.wwsl.wgsj.share;

import android.app.Activity;
import android.graphics.Bitmap;

import com.wwsl.wgsj.AppConfig;
import com.wwsl.wgsj.bean.ConfigBean;
import com.wwsl.wgsj.bean.ShareBean;
import com.wwsl.wgsj.bean.VideoBean;

public class ShareHelper {

    public static void shareVideo(Activity activity, ShareBean shareBean, VideoBean videoBean) {
        if (null == shareBean || null == videoBean) return;
        ShareUtil.getInstance().shareVideo(activity, shareBean.getType(), videoBean.getTitle(),
                videoBean.getTitle(), videoBean.getVideoUrl(), videoBean.getCoverUrl(), "围观视界,开启新世界!");
    }

    public static void shareTextWithImg(Activity activity, ShareBean shareBean, Bitmap bitmap) {
        if (null == shareBean) return;
        ShareUtil.getInstance().shareTextWithImg(activity, shareBean.getType(), "围观视界,开启新世界!", bitmap);
    }

    public static void shareText(Activity activity, ShareBean shareBean, String content) {
        if (null == shareBean) return;
        ShareUtil.getInstance().shareMessage(activity, shareBean.getType(), content);
    }

    public static void shareLive(Activity activity, ShareBean shareBean,String imgUrl) {
        if (null == shareBean) return;
        ConfigBean config = AppConfig.getInstance().getConfig();
        if (config != null) {
            ShareUtil.getInstance().shareWeb(activity, shareBean.getType(), config.getLiveShareTitle(), imgUrl, config.getLiveShareDes(), config.getDownloadApkUrl());
        }
    }
}
