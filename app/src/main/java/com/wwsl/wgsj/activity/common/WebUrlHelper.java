package com.wwsl.wgsj.activity.common;

import android.net.Uri;

public class WebUrlHelper {
    static final String PROTOCOL_PREFIX = "wgsj";

    public static boolean checkUrl(Uri url) {
        if (null != url.getScheme()) {
            return url.getScheme().startsWith(PROTOCOL_PREFIX);
        }
        return false;
    }
}
