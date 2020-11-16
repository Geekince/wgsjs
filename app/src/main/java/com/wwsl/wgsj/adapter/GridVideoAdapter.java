package com.wwsl.wgsj.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.bean.VideoBean;
import com.wwsl.wgsj.glide.ImgLoader;
import com.wwsl.wgsj.utils.StringUtil;

import java.util.List;

public class GridVideoAdapter extends BaseQuickAdapter<VideoBean, BaseViewHolder> {
    public GridVideoAdapter(@Nullable List<VideoBean> data) {
        super(R.layout.item_gridvideo, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, VideoBean item) {


        helper.setText(R.id.tv_content, item.getTitle());
        helper.setText(R.id.tv_name, item.getUserBean().getUsername());
        helper.setText(R.id.tv_distance, StringUtil.getDistance(item.getDistance()));
        ImgLoader.displayAvatar(item.getUserBean().getAvatar(), helper.getView(R.id.iv_head));
        ImgLoader.display(item.getCoverUrl(), helper.getView(R.id.iv_cover));
    }
}
