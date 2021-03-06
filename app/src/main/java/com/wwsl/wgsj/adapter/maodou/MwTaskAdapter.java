package com.wwsl.wgsj.adapter.maodou;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.bean.maodou.NetMwTaskBean;
import com.wwsl.wgsj.glide.ImgLoader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MwTaskAdapter extends BaseQuickAdapter<NetMwTaskBean, BaseViewHolder> {

    public MwTaskAdapter(@Nullable List<NetMwTaskBean> data) {
        super(R.layout.item_mw_task, data);
        addChildClickViewIds(R.id.btnBuy);
    }


    @Override
    protected void convert(@NotNull BaseViewHolder holder, NetMwTaskBean bean) {
        holder.setText(R.id.title, bean.getTitle());
        holder.setText(R.id.buyPercent, String.format("%s/%s", bean.getProductCount(), bean.getLimit()));
        holder.setText(R.id.needNum, bean.getPrice() + "枚令牌");
        holder.setText(R.id.haveGetNum, bean.getIncome() + "枚令牌");
        ImgLoader.display(bean.getImage(), holder.getView(R.id.iv_img));

    }

    @Override
    protected void convert(@NotNull BaseViewHolder holder, NetMwTaskBean bean, @NotNull List<?> payloads) {
        for (int i = 0; i < payloads.size(); i++) {
            Integer payload = (Integer) payloads.get(i);
            if (payload == PAYLOAD_BUY_MW) {
                holder.setText(R.id.buyPercent, String.format("%s/%s", bean.getProductCount(), bean.getLimit()));
            }
        }
    }

    public static final int PAYLOAD_BUY_MW = 1001;
}
