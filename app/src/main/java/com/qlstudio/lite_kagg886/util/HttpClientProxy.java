package com.qlstudio.lite_kagg886.util;

import android.util.Log;
import com.kagg886.jxw_collector.internal.HttpClient;
import org.jsoup.Connection;

import java.util.Random;
import java.util.stream.Stream;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.qlstudio.lite_kagg886.util
 * @className: HttpClientProxy
 * @author: kagg886
 * @description: TODO
 * @date: 2023/4/24 17:41
 * @version: 1.0
 */
public class HttpClientProxy extends HttpClient {

    public static final String TAG = HttpClientProxy.class.getSimpleName();

    private static final Random ran = new Random();

    @Override
    public synchronized Connection.Response get() {
        int a = ran.nextInt();
        log("GET", "_", a, "->", this);
        Connection.Response resp = super.get();
        if (resp == null || resp.statusCode() != 200) {
            log("GET_",a,"->","err not response");
            return resp;
        }
        if (!resp.header("Content-Type").contains("image")) {
            log("GET_", a, "_RESP->", resp.body());
        } else {
            log("GET_", a, "_RESP->[Image:", resp.url().toString(), "]");
        }
        return resp;
    }

    @Override
    public synchronized Connection.Response post() {
        int a = ran.nextInt();
        log("POST_", a, "->", this);
        Connection.Response resp = super.post();
        if (resp == null || resp.statusCode() != 200) {
            log("POST_",a,"->","err not response");
            return resp;
        }
        if (!resp.header("Content-Type").contains("image")) {
            log("POST_", a, "_RESP->", resp.body());
        } else {
            log("POST_", a, "_RESP->[Image:", resp.url().toString(), "]");
        }
        return resp;
    }

    private void log(Object... msg) {
        StringBuilder b = new StringBuilder();
        Stream.of(msg).forEach(b::append);
        Log.d(TAG, b.toString());
    }
}
