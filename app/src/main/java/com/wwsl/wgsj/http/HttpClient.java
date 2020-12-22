package com.wwsl.wgsj.http;

import com.frame.fire.util.LogUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.MemoryCookieStore;
import com.lzy.okgo.request.GetRequest;
import com.lzy.okgo.request.PostRequest;
import com.wwsl.wgsj.AppConfig;
import com.wwsl.wgsj.AppContext;
import com.wwsl.wgsj.utils.StringUtil;
import com.wwsl.wgsj.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static cn.jmessage.biz.httptask.task.RegisterTask.TAG;

/**
 * Created by cxf on 2018/9/17.
 */

public class HttpClient {

    private static final int TIMEOUT = 10000;
    private static HttpClient sInstance;
    private OkHttpClient mOkHttpClient;
    private String mLanguage;//语言
    private String mUrl;
    private String mUrl2;

    private HttpClient() {
        mUrl2 = AppConfig.HOST + "/apis/";
        mUrl = AppConfig.HOST + "/api/public/?service=";
    }

    public static HttpClient getInstance() {
        if (sInstance == null) {
            synchronized (HttpClient.class) {
                if (sInstance == null) {
                    sInstance = new HttpClient();
                }
            }
        }
        return sInstance;
    }

    public void init() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        builder.readTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        builder.writeTimeout(TIMEOUT, TimeUnit.MILLISECONDS);
        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));
        builder.retryOnConnectionFailure(true);
//        Dispatcher dispatcher = new Dispatcher();
//        dispatcher.setMaxRequests(20000);
//        dispatcher.setMaxRequestsPerHost(10000);
//        builder.dispatcher(dispatcher);

        //输出HTTP请求 响应信息
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("http");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        builder.addInterceptor(loggingInterceptor);
        mOkHttpClient = builder.build();

        OkGo.getInstance().init(AppContext.sInstance)
                .setOkHttpClient(mOkHttpClient)
                .setCacheMode(CacheMode.NO_CACHE)
                .setRetryCount(1);

    }

    public GetRequest<JsonBean> get(String serviceName, String tag) {
        return OkGo.<JsonBean>get(mUrl + serviceName)
                .headers("Connection", "keep-alive")
                .headers(AppConfig.getInstance().getHttpHeaders())
                .tag(tag)
                .params(HttpConst.LANGUAGE, mLanguage);

    }


    public PostRequest<JsonBean> post(String serviceName, String tag) {
        return OkGo.<JsonBean>post(mUrl + serviceName)
                .headers("Connection", "keep-alive")
                .headers(AppConfig.getInstance().getHttpHeaders())
                .tag(tag)
                .params(HttpConst.LANGUAGE, mLanguage);
    }

    public void cancel(String tag) {
        OkGo.cancelTag(mOkHttpClient, tag);
    }

    public void setLanguage(String language) {
        mLanguage = language;
    }
}
