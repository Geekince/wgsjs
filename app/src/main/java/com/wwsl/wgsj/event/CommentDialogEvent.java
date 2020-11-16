package com.wwsl.wgsj.event;

import com.wwsl.wgsj.bean.VideoBean;
import com.wwsl.wgsj.bean.VideoCommentBean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor()
@NoArgsConstructor
@Builder
public class CommentDialogEvent {
    boolean openFace;
    VideoBean videoBean;
    VideoCommentBean commentBean;
}