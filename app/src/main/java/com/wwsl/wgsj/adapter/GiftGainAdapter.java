package com.wwsl.wgsj.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.ImageView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.wgsj.AppConfig;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.bean.GiftDetailBean;
import com.wwsl.wgsj.glide.ImgLoader;
import com.wwsl.wgsj.utils.DpUtil;

import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class GiftGainAdapter extends BaseQuickAdapter<GiftDetailBean, BaseViewHolder> {

    public GiftGainAdapter(@Nullable List<GiftDetailBean> data) {
        super(R.layout.item_gain_gift, data);
    }


    @Override
    protected void convert(@NonNull BaseViewHolder helper, GiftDetailBean item) {

        if (item.getTouid().equals(AppConfig.getInstance().getUid())) {
            helper.setText(R.id.detail_tag, "+" + new BigDecimal(item.getVotes()).toString() + AppConfig.getInstance().getConfig().getVotesName().intern());
            helper.setText(R.id.detail_name, item.getUserinfo());
        } else {
            helper.setText(R.id.detail_name, item.getTouserinfo());
            helper.setText(R.id.detail_tag, "-" + new BigDecimal(item.getTotalcoin()).toString() + AppConfig.getInstance().getConfig().getCoinName().intern());
        }
        helper.setText(R.id.detail_des, item.getGiftinfo());
        helper.setText(R.id.detail_time, item.getAddtime());

        ImageView view = helper.getView(R.id.detail_icon);
        ImgLoader.display(item.getGifticon(), view);

    }


    @Override
    public void onAttachedToRecyclerView(@NotNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        getRecyclerView().scrollBy(0, DpUtil.dp2px(80));
    }
}
