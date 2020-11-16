package com.wwsl.wgsj.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.bean.LiveShopWindowBean;
import com.wwsl.wgsj.glide.ImgLoader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AudienceShopWindowAdapter extends BaseQuickAdapter<LiveShopWindowBean, BaseViewHolder> {


    public AudienceShopWindowAdapter(@Nullable List<LiveShopWindowBean> data) {
        super(R.layout.item_live_all_goods, data);
    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, LiveShopWindowBean bean) {
        holder.setText(R.id.title, bean.getTitle());
        holder.setText(R.id.tvPriceNow, String.format("￥%s", bean.getPrice()));
        holder.setText(R.id.tvPriceOld, String.format("原价:￥%s", bean.getOldPrice()));
        ImgLoader.display(bean.getThumb(), holder.getView(R.id.ivThumb));
    }
}
