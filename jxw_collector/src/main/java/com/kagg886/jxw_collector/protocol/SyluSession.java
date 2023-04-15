package com.kagg886.jxw_collector.protocol;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kagg886.jxw_collector.exceptions.OfflineException;
import com.kagg886.jxw_collector.internal.HttpClient;
import com.kagg886.jxw_collector.internal.RSA;
import com.kagg886.jxw_collector.protocol.beans.ExamResult;
import com.kagg886.jxw_collector.protocol.beans.Schedule;
import com.kagg886.jxw_collector.protocol.beans.UserInfo;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Arrays;
import java.util.Date;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.kagg886.jxw_collector.internal.protocol
 * @className: SyluSession
 * @author: kagg886
 * @description: 代表一个教务网登录账户
 * @date: 2023/4/13 13:12
 * @version: 1.0
 */
public class SyluSession {
    private final HttpClient client; //访问客户端

    private static final String base = "https://jxw.sylu.edu.cn";

    private String user;//用户名

    private JSONObject rsaSession; //获取RSA信息

    public SyluSession(String user) {
        client = new HttpClient();
        this.user = user;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof SyluSession)) {
            return false;
        }
        return ((SyluSession) obj).user.equals(user);
    }

    public SyluSession() {
        client = new HttpClient();
    }

    public void setUser(String user) {
        if (this.user != null) {
            throw new IllegalStateException("已初始化的用户禁止调用此方法");
        }
        this.user = user;
    }

    public HttpClient getClient() {
        return client;
    }

    private void initRSAClient() {
        client.url(compile("/xtgl/login_getPublicKey.html?time=",
                new Date().getTime(),
                "&_=", new Date().getTime()));
        Connection.Response resp = client.get();
        client.header("Cookie", resp.header("Set-Cookie"))
                .header("Content-Type", "application/x-www-form-urlencoded");
        this.rsaSession = JSON.parseObject(resp.body());
    }

    public String getStuCode() {
        return user;
    }


    public void loginByPwd(String pwd) {
        initRSAClient();
        client.url(compile("/xtgl/login_slogin.html?time=", new Date().getTime()))
                .data("yhm", user).data("mm", RSA.getInstance().encrypt(rsaSession, pwd));

        Connection.Response resp = client.post();

        Element test = Jsoup.parse(resp.body()).getElementById("tips");
        if (test != null) {
            throw new OfflineException.LoginFailed("登陆失败:" + test.text());
        }
    }

    public void loginByCookie(String cookie) {
        client.header("Cookie", cookie);
        assertLogin();
    }

    public boolean isLogin() {
        client.url(compile("/xtgl/index_initMenu.html?jsdm=xs&_t=" + new Date().getTime()));
        Connection.Response resp = client.get();
        Document document = Jsoup.parse(resp.body());
        for (Element e : document.getElementsByTag("h5")) {
            if (e.text().equals("用户登录")) {
                return false;
            }
        }
        return true;
    }

    public String compile(Object... p) {
        StringBuilder builder = new StringBuilder(base);
        Arrays.stream(p).forEach(builder::append);
        return builder.toString();
    }

    public UserInfo getUserInfo() {
        assertLogin();
        client.url(compile("/xtgl/index_cxYhxxIndex.html?xt=jw&localeKey=zh_CN&_=", new Date().getTime(), "&gnmkdm=index&su=", user));
        Connection.Response resp = client.get();
        Document document = Jsoup.parse(resp.body());
        String avt = document.getElementsByTag("img").attr("src");
        String name = document.getElementsByTag("h4").text();
        String clazz = document.getElementsByTag("p").text();
        return new UserInfo(compile(avt), name, clazz);
    }

    public ExamResult getExamResult() {
        return new ExamResult(this);
    }

    public Schedule getSchedule() {
        return new Schedule(this);
    }

    public void assertLogin() {
        if (!isLogin()) {
            throw new OfflineException("登录状态为未登录或踢下线");
        }
    }
}
