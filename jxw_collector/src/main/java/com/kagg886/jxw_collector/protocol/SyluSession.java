package com.kagg886.jxw_collector.protocol;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kagg886.jxw_collector.exceptions.OfflineException;
import com.kagg886.jxw_collector.internal.HttpClient;
import com.kagg886.jxw_collector.internal.RSA;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

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

    private final String user;//用户名

    private JSONObject rsaSession; //获取RSA信息

    public SyluSession(String user) {
        client = new HttpClient();
        this.user = user;
    }

    private void setRSA() {
        client.url(compile("/xtgl/login_getPublicKey.html?time=",
                new Date().getTime(),
                "&_=",new Date().getTime()));
        Connection.Response resp = client.get();
        client.header("Cookie",resp.header("Set-Cookie"))
                .header("Content-Type","application/x-www-form-urlencoded");
        this.rsaSession = JSON.parseObject(resp.body());
    }

    public void login(String pwd) {
        setRSA();
        client.url(compile("/xtgl/login_slogin.html?time=",new Date().getTime()))
                .data("yhm",user).data("mm",RSA.getInstance().encrypt(rsaSession,pwd));

        Connection.Response resp = client.post();

        Element test = Jsoup.parse(resp.body()).getElementById("tips");
        if (test != null) {
            throw new OfflineException.LoginFailed("登陆失败:" + test.text());
        }
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

    private String compile(Object... p) {
        StringBuilder builder = new StringBuilder(base);
        Arrays.stream(p).forEach(builder::append);
        return builder.toString();
    }
}
