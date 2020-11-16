package com.wwsl.wgsj.utils.tiktok;

import android.text.TextUtils;
import android.view.View;

import com.frame.fire.util.LogUtils;
import com.wwsl.wgsj.views.autolinktextview.AutoLinkMode;
import com.wwsl.wgsj.views.autolinktextview.AutoLinkTextView;


/**
 * @author :
 * @date : 2020/6/17 15:13
 * @description : AutoLinkHerfManager
 */
public class AutoLinkHerfManager {
    /**
     * 设置正文内容
     *
     * @param content
     */
    public static void setContent(String content, AutoLinkTextView autoLinkTextView) {

        if (TextUtils.isEmpty(content)) {
            autoLinkTextView.setVisibility(View.GONE);
            return;
        }
        autoLinkTextView.setVisibility(View.VISIBLE);
        autoLinkTextView.addAutoLinkMode(AutoLinkMode.MODE_HASHTAG, AutoLinkMode.MODE_MENTION, AutoLinkMode.MODE_URL);  //设置需要富文本的模式
        autoLinkTextView.setText(content);
        autoLinkTextView.setAutoLinkOnClickListener((autoLinkMode, matchedText) -> {

            switch (autoLinkMode) {
                case MODE_HASHTAG:
                    LogUtils.e("minfo", "setContent: ");
                    break;
                case MODE_MENTION:
                    LogUtils.e("minfo", "at " + matchedText);
                    break;
            }
        });
    }

}
