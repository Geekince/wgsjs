package com.wwsl.wgsj.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.bigkoo.convenientbanner.holder.Holder;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.glide.ImgLoader;

public class NetworkImageHolderView implements Holder<String> {
    private ImageView imageView;

    @Override
    public View createView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.home_banner_item, null);
        imageView = view.findViewById(R.id.iv_banner_img);
        return view;
    }

    @Override
    public void UpdateUI(Context context, int position, String data) {
        // 图片
        ImgLoader.display(data, imageView);
    }
}