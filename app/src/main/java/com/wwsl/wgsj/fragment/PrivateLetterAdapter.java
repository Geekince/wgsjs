package com.wwsl.wgsj.fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.bean.net.NetFriendBean;
import com.wwsl.wgsj.glide.ImgLoader;

import java.util.List;

public class PrivateLetterAdapter extends BaseQuickAdapter<NetFriendBean, BaseViewHolder> {

    public PrivateLetterAdapter(@Nullable List<NetFriendBean> data) {
        super(R.layout.item_private_letter, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, NetFriendBean item) {

        ImgLoader.displayAvatar(item.getAvatar(), helper.getView(R.id.iv_head));

        helper.setText(R.id.tv_nickname, item.getUsername());
    }
}
