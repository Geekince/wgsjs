package com.wwsl.wgsj.activity.common;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;

import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.wwsl.wgsj.AppConfig;
import com.wwsl.wgsj.Constants;
import com.wwsl.wgsj.R;
import com.wwsl.wgsj.activity.me.user.MyBalanceActivity;
import com.wwsl.wgsj.pay.PayCallback;
import com.wwsl.wgsj.pay.ali.AliPayBuilder;
import com.wwsl.wgsj.pay.wx.WxPayBuilder;
import com.wwsl.wgsj.utils.L;
import com.wwsl.wgsj.utils.ToastUtil;
import com.wwsl.wgsj.utils.WordUtil;
import com.wwsl.wgsj.wxapi.PayCallbackBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by cxf on 2018/9/25.
 */

public class WebViewActivity extends AbsActivity {

    private ProgressBar mProgressBar;
    private WebView mWebView;
    private final int CHOOSE = 100;//Android 5.0以下的
    private final int CHOOSE_ANDROID_5 = 200;//Android 5.0以上的
    private ValueCallback<Uri> mValueCallback;
    private ValueCallback<Uri[]> mValueCallback2;
    private int type = 0;//default
    private FrameLayout header;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_webview;
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void main() {
        Window window = this.getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.status_color));//#1E222D
        process();
        EventBus.getDefault().register(this);
        String url = getIntent().getStringExtra(Constants.URL);
        L.e("H5--->" + url);
        LinearLayout rootView = (LinearLayout) findViewById(R.id.rootView);
        mProgressBar = (ProgressBar) findViewById(R.id.progressbar);
        header = (FrameLayout) findViewById(R.id.header);

        mWebView = findViewById(R.id.webView);
        mWebView.setBackgroundColor(Color.parseColor("#151922"));
        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView webView, WebResourceRequest request) {
                String mUrl = request.getUrl().toString();
                if (WebUrlHelper.checkUrl(request.getUrl())) {
                    //走协议
                    String host = request.getUrl().getHost();
                    if ("pay".equals(host)) {
                        //支付
                        payGoods(request.getUrl());
                    }

                } else if (mUrl.equals("callapp:pay")) {
                    //跳至"我的账户"
                    startActivity(new Intent(mContext, MyBalanceActivity.class));
                    finish();
                } else if (mUrl.startsWith(Constants.COPY_PREFIX)) {
                    String content = mUrl.substring(Constants.COPY_PREFIX.length());
                    if (!TextUtils.isEmpty(content)) {
                        copy(content);
                    }
                } else if (mUrl.startsWith("alipay")) {
                    Intent intent = new Intent();
                    intent.setAction("android.intent.action.VIEW");
                    Uri content_url = Uri.parse(mUrl);
                    intent.setData(content_url);
                    startActivity(intent);
                } else {
                    if (!mUrl.contains("token")) {
                        if (mUrl.contains("?")) {
                            mUrl += "&uid=" + AppConfig.getInstance().getUid() + "&token=" + AppConfig.getInstance().getToken();
                        } else {
                            mUrl += "?uid=" + AppConfig.getInstance().getUid() + "&token=" + AppConfig.getInstance().getToken();
                        }
                    }
                    L.e("H5-------->" + mUrl);
//                    webView.loadUrl(mUrl);
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                setTitle(view.getTitle());
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    mProgressBar.setProgress(newProgress);
                }
            }

            //以下是在各个Android版本中 WebView调用文件选择器的方法
            // For Android < 3.0
            public void openFileChooser(ValueCallback<Uri> valueCallback) {
                openImageChooserActivity(valueCallback);
            }

            // For Android  >= 3.0
            public void openFileChooser(ValueCallback valueCallback, String acceptType) {
                openImageChooserActivity(valueCallback);
            }

            //For Android  >= 4.1
            public void openFileChooser(ValueCallback<Uri> valueCallback,
                                        String acceptType, String capture) {
                openImageChooserActivity(valueCallback);
            }

            // For Android >= 5.0
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             WebChromeClient.FileChooserParams fileChooserParams) {
                mValueCallback2 = filePathCallback;
                Intent intent = fileChooserParams.createIntent();
                startActivityForResult(intent, CHOOSE_ANDROID_5);
                return true;
            }

        });

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setAllowFileAccess(true);// 设置允许访问文件数据
        mWebView.getSettings().setSupportZoom(true);
        mWebView.getSettings().setBuiltInZoomControls(true);
        mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setDatabaseEnabled(true);

        mWebView.loadUrl(url);
        type = getIntent().getIntExtra("type", 0);
//        if (type == 1) {
//            header.setVisibility(View.GONE);
//        }
    }

    private void payGoods(Uri url) {
        String type = url.getQueryParameter("type");
        String order = url.getQueryParameter("order_sn");
        String payType = url.getQueryParameter("pay_type");
        if ("wxpay".equals(payType)) {
            WxPayBuilder builder = new WxPayBuilder(this);
            builder.payGoods(order, type, payType);
        } else if ("alipay".equals(payType)) {
            AliPayBuilder builder = new AliPayBuilder(this);
            builder.setPayCallback(new PayCallback() {
                @Override
                public void onSuccess() {
                    //支付成功
                    ToastUtil.show("支付成功");
                    mWebView.clearHistory();
                    mWebView.loadUrl(AppConfig.getInstance().getMarketUrl());
                }

                @Override
                public void onFailed() {

                }
            });
            builder.payGoods(order, type, payType);
        }
    }

    private void openImageChooserActivity(ValueCallback<Uri> valueCallback) {
        mValueCallback = valueCallback;
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, WordUtil.getString(R.string.choose_flie)), CHOOSE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (intent == null) return;
        switch (requestCode) {
            case CHOOSE://5.0以下选择图片后的回调
                processResult(resultCode, intent);
                break;
            case CHOOSE_ANDROID_5://5.0以上选择图片后的回调
                processResultAndroid5(resultCode, intent);
                break;
        }
    }

    private void processResult(int resultCode, Intent intent) {
        if (mValueCallback == null) {
            return;
        }
        if (resultCode == RESULT_OK && intent != null) {
            Uri result = intent.getData();
            mValueCallback.onReceiveValue(result);
        } else {
            mValueCallback.onReceiveValue(null);
        }
        mValueCallback = null;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void processResultAndroid5(int resultCode, Intent intent) {
        if (mValueCallback2 == null) {
            return;
        }
        if (resultCode == RESULT_OK && intent != null) {
            mValueCallback2.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
        } else {
            mValueCallback2.onReceiveValue(null);
        }
        mValueCallback2 = null;
    }

    protected boolean canGoBack() {
        return mWebView != null && mWebView.canGoBack();
    }

    @Override
    public void onBackPressed() {
        if (isNeedExitActivity()) {
            finish();
        } else {
            if (canGoBack()) {
                mWebView.goBack();
            } else {
                setResult(RESULT_OK);
                finish();
            }
        }
    }


    private boolean isNeedExitActivity() {
        if (mWebView != null) {
            String url = mWebView.getUrl();
            if (!TextUtils.isEmpty(url)) {
                return url.contains("g=Appapi&m=Auth&a=success")//身份认证成功页面
                        || url.contains("g=Appapi&m=Family&a=home");//家族申请提交成功页面

            }
        }
        return false;
    }

    public static void forward(Context context, String url) {
        if (url.contains("?")) {
            url += "&uid=" + AppConfig.getInstance().getUid() + "&token=" + AppConfig.getInstance().getToken();
        } else {
            url += "?uid=" + AppConfig.getInstance().getUid() + "&token=" + AppConfig.getInstance().getToken();
        }

        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Constants.URL, url);
        context.startActivity(intent);
    }

    public static void forward2(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Constants.URL, url);
        context.startActivity(intent);
    }

    /**
     * 自己是是否有header
     *
     * @param context
     * @param url
     * @param type
     */
    public static void forward3(Context context, String url, int type) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Constants.URL, url);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    public static void forwardForResult(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(Constants.URL, url);
        ((Activity) context).startActivityForResult(intent, 0);
    }

    @Override
    protected void onDestroy() {
        if (mWebView != null) {
            ViewGroup parent = (ViewGroup) mWebView.getParent();
            if (parent != null) {
                parent.removeView(mWebView);
            }
        }
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    /**
     * 复制到剪贴板
     */
    private void copy(String content) {
        ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("text", content);
        cm.setPrimaryClip(clipData);
        ToastUtil.show(getString(R.string.copy_success));
    }


    @Subscribe
    public void chargeBack(PayCallbackBean bean) {
        if (null != bean) {
            switch (bean.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    ToastUtil.show("支付成功");
                    if (mWebView != null) {
                        mWebView.clearHistory();
                        mWebView.loadUrl(AppConfig.getInstance().getMarketUrl());
                    }
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    ToastUtil.show("用户取消");
                    break;
                case BaseResp.ErrCode.ERR_COMM:
                default:
                    ToastUtil.show("支付失败");
                    break;
            }
        }
    }


}
