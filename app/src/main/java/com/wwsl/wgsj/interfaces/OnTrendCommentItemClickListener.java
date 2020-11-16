package com.wwsl.wgsj.interfaces;

import com.wwsl.wgsj.bean.CommentBean;

public interface OnTrendCommentItemClickListener {
    void onItemClick(CommentBean commentBean);

    void onUserName(CommentBean commentBean);

    void onToUserName(CommentBean commentBean);
}
