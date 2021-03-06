package com.wwsl.wgsj.http;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;

import com.alibaba.fastjson.JSON;
import com.frame.fire.util.LogUtils;
import com.lzy.okgo.callback.AbsCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.wwsl.wgsj.AppContext;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.activity.login.LauncherActivity;
import com.wwsl.wgsj.activity.login.LoginInvalidActivity;
import com.wwsl.wgsj.utils.L;
import com.wwsl.wgsj.utils.ToastUtil;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by cxf on 2017/8/7.
 */

public abstract class HttpCallback extends AbsCallback<JsonBean> {

    private Dialog mLoadingDialog;

    @Override
    public JsonBean convertResponse(okhttp3.Response response) throws Throwable {
        return JSON.parseObject(response.body().string(), JsonBean.class);
    }

    @Override
    public void onSuccess(Response<JsonBean> response) {
        JsonBean bean = response.body();
        if (bean != null) {
            if (200 == bean.getRet()) {
                Data data = bean.getData();
                if (data != null) {
                    if (700 == data.getCode()) {
                        //token过期，重新登录
                        ActivityManager activityManager = (ActivityManager) AppContext.sInstance.getSystemService(ACTIVITY_SERVICE);
                        //获取当前栈顶的activity
                        ComponentName currentActivityName = activityManager.getRunningTasks(1).get(0).topActivity;

                        LogUtils.e("myth", "当前栈顶activity: " + currentActivityName.getClassName()+"---------LauncherActivity: "+LauncherActivity.class.getName());

                        if (LauncherActivity.class.getName().equals(currentActivityName.getClassName())) {

                        } else {
                            if (com.wwsl.wgsj.activity.common.ActivityManager.getInstance().getActivity(LoginInvalidActivity.class.getSimpleName()) == null) {
                                LoginInvalidActivity.forward(data.getMsg());
                            }
                        }
                    }
//                    else if (600 == data.getCode()) {
//                        if (ActivityManager.getInstance().getActivity(AccountInvalidActivity.class.getSimpleName()) == null) {
//                            AccountInvalidActivity.forward(data.getMsg());
//                        }
//                    }
                    else {
                        onSuccess(data.getCode(), data.getMsg(), data.getInfo());
                    }
                } else {
                    L.e("服务器返回值异常--->ret: " + bean.getRet() + " msg: " + bean.getMsg());
                }
            } else {
                L.e("服务器返回值异常--->ret: " + bean.getRet() + " msg: " + bean.getMsg());
            }

        } else {
            L.e("服务器返回值异常--->bean = null");
        }
    }

    @Override
    public void onError(Response<JsonBean> response) {
        Throwable t = response.getException();
        L.e("网络请求错误---->" + t.getClass() + " : " + t.getMessage());
        if (t instanceof SocketTimeoutException || t instanceof ConnectException || t instanceof UnknownHostException || t instanceof UnknownServiceException || t instanceof SocketException) {
            ToastUtil.show(R.string.load_failure);
        }
        if (showLoadingDialog() && mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
        onError();
    }

    public void onError() {

    }


    public abstract void onSuccess(int code, String msg, String[] info);

    @Override
    public void onStart(Request<JsonBean, ? extends Request> request) {
        onStart();
    }

    public void onStart() {
        if (showLoadingDialog()) {
            if (mLoadingDialog == null) {
                mLoadingDialog = createLoadingDialog();
            }
            mLoadingDialog.show();
        }
    }

    @Override
    public void onFinish() {
        if (showLoadingDialog() && mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
    }

    public Dialog createLoadingDialog() {
        return null;
    }

    public boolean showLoadingDialog() {
        return false;
    }

}
