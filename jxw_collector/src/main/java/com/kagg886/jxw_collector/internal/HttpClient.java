package com.kagg886.jxw_collector.internal;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.kagg886.jxw_collector.internal.http
 * @className: HttpClient
 * @author: kagg886
 * @description: 访问器，维护一个Connection对象来保证cookie一致性
 * @date: 2023/4/13 12:48
 * @version: 1.0
 */
public class HttpClient {

    private final Connection connection; //连接对象

    private final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(15); //异步线程池


    public String getCookie() {
        return connection.request().header("Cookie");
    }

    public synchronized Connection.Response get() {
        try {
            return connection.method(Connection.Method.GET).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Connection.Method getMethod() {
        return connection.request().method();
    }

    public HttpClient setCookie(String k, String v) {
        connection.cookie(k, v);
        return this;
    }

    public synchronized Connection.Response post() {
        try {
            return connection.method(Connection.Method.POST).execute();
        } catch (IOException e) {
            return null;
        }
    }

    public void get(Consumer<Connection.Response> fun) {
        executor.execute(() -> {
            Connection.Response resp = get();
            fun.accept(resp);
        });
    }

    public void post(Consumer<Connection.Response> fun) {
        executor.execute(() -> {
            Connection.Response resp = post();
            fun.accept(resp);
        });
    }

    public HttpClient() {
        connection = Jsoup.newSession();
        connection.ignoreHttpErrors(true).ignoreContentType(true).timeout(10000)
                .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36 Edg/112.0.1722.48");
    }

    public HttpClient clearData() {
        connection.request().data().clear();
        return this;
    }

    public HttpClient url(String url) {
        connection.url(url);
        return this;
    }

    public HttpClient data(String k, String v) {
        connection.data(k, v);
        return this;
    }

    public Map<String, String> data() {
        Map<String, String> a = new HashMap<>();
        connection.request().data().forEach((v) -> {
            a.put(v.key(), v.value());
        });
        return a;
    }

    public HttpClient data(Map<String, String> map) {
        connection.data(map);
        return this;
    }

    public HttpClient header(String k, String v) { //不能Clear Header，因为cookie在header里
        connection.header(k, v);
        return this;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("HttpClient{");
        sb.append("connection={")
                .append("{url=").append(connection.request().url()).append("}")
                .append("{headers=").append(connection.request().headers()).append("}")
                .append("{data=").append(connection.request().data()).append("}")
                .append('}');
        return sb.toString();
    }
}
