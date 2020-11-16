package com.wwsl.wgsj.upload;

public interface PictureUploadCallback {
    void onSuccess(String url);

    void onFailure();
}
