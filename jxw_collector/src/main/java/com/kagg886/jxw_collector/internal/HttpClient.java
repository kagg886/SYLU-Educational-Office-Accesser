package com.kagg886.jxw_collector.internal;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
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


    public Connection.Response get() {
        try {
            return connection.method(Connection.Method.GET).execute();
        } catch (IOException e) {
            return null;
        }
    }

    public Connection.Response post() {
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
        connection.ignoreHttpErrors(true).ignoreContentType(true).timeout(10000);
    }

    public HttpClient clearData() {
        connection.request().data().clear();
        return this;
    }

    public HttpClient url(String url) {
        connection.url(url);
        return this;
    }

    public HttpClient data(String k,String v) {
        connection.data(k,v);
        return this;
    }

    public HttpClient header(String k,String v) { //不能Clear Header，因为cookie在header里
        connection.header(k,v);
        return this;
    }
}
