package com.wwsl.wgsj.interfaces;

import com.wwsl.wgsj.bean.WishBillBean;

public interface OnWishBillItemClickListener {
    void onItemClick(WishBillBean bean, int position);

    void onAvatarClick(WishBillBean bean, int position);

    void onDeleteClick(int position);
}
