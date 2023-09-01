package com.kagg886.jxw_collector.protocol;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.kagg886.jxw_collector.exceptions.OfflineException;
import com.kagg886.jxw_collector.internal.HttpClient;
import com.kagg886.jxw_collector.internal.RSA;
import com.kagg886.jxw_collector.protocol.beans.*;
import com.kagg886.jxw_collector.util.ExceptionUtil;
import com.kagg886.jxw_collector.util.ParamUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

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

    private String captchaLink; //验证码链接

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

    public SyluSession(HttpClient client) {
        this.client = client;
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
        if (resp == null) {
            throw new IllegalStateException("无法获得通信密钥，这可能是教务网的问题\n请检查教务网网页端能够正常进入。");
        }
        String cookie = resp.header("Set-Cookie");
        if (cookie != null) {
            client.header("Cookie", cookie);
        }
        client.header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        this.rsaSession = JSON.parseObject(resp.body());
    }

    public String getStuCode() {
        return user;
    }


    public void loginByPwd(String pwd) {
        loginByPwd(pwd, null);
    }

    public boolean needVerifyCode() {
        return captchaLink != null;
    }

    public String getVerifyLink() {
        return "https://jxw.sylu.edu.cn" + captchaLink;
    }

    public void logout() {
        client.clearData()
                .url("https://jxw.sylu.edu.cn/logout?t=1688629994486&login_type=")
                .post();
    }

    public void loginByPwd(String pwd, String captchaCode) {
        initRSAClient();
        client.url(compile("/xtgl/login_slogin.html?time=", new Date().getTime()))
                .data("yhm", user).data("mm", RSA.getInstance().encrypt(rsaSession, pwd));

        if (captchaCode != null) {
            client.data("yzm", captchaCode);
        }
        Connection.Response resp = client.post();

        Document doc = Jsoup.parse(resp.body());
        Element test = doc.getElementById("tips");
        if (test != null) {
            Element captcha = doc.getElementById("yzmPic");
            if (captcha != null) {
                captchaLink = "/kaptcha?time=" + new Date().getTime();
            }
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

    public SecondClassData getSecondClassData(String pass) {
        HttpClient client = new HttpClient();
        Connection.Response resp = client.url("http://xg.sylu.edu.cn/SyluTW/Sys/UserLogin.aspx")
                .get();
        Document dom = null;
        try {
            dom = resp.parse();
        } catch (IOException ignored) {
        }
        client.header("Cookie", resp.header("Set-Cookie"));

        String user = getStuCode();
        String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC3hzrH91c0OKgtaSB7GWGfDuUJ" +
                "sMrtiYThDXtJdrCr7exKt2fmIZngoFk71Dv/BPVQCHSuohNNvEV9VVDFSBhsP9xK" +
                "EDAM4/2Lv+wlzN9CuZtLpV3Elo8VacjwMHcjTRmTchRBmijQzZRFrA2LM+qsH3U5" +
                "tRM1uJFbfRMkBq24AwIDAQAB";
        //---------------根据公钥加密-----------------
        resp = client.data("UserName", user)
                .data("__VIEWSTATE", dom.getElementById("__VIEWSTATE").attr("value"))
                .data("__VIEWSTATEGENERATOR", dom.getElementById("__VIEWSTATEGENERATOR").attr("value"))
                .data("__EVENTVALIDATION", dom.getElementById("__EVENTVALIDATION").attr("value"))
                .data("Password", pass)
                .data("pwd", RSA.getInstance().encrypt(pass, pubKey))
                .data("pubKey", pubKey)
                .data("codeInput", "KHG6")
                .data("queryBtn", "%B5%C7++++++++++%C2%BC")
                .post();

        try {
            dom = resp.parse();
        } catch (IOException ignored) {
        }

        AtomicBoolean isSuccess = new AtomicBoolean(false);

        dom.getElementsByTag("script").forEach((v) -> {
            if (v.html().startsWith("layer.alert('")) {
                int l = v.html().indexOf("'") + 1;
                int r = v.html().indexOf("'", l);
                throw new OfflineException.LoginFailed(v.html().substring(l, r));
            }

            if (v.html().equals("window.location.href='SystemForm/main.htm';")) {
                isSuccess.set(true);
            }
        });

        if (!isSuccess.get()) {
            throw new OfflineException("Check State Failed!\n" + dom.html());
        }

        //fetch("http://xg.sylu.edu.cn/SyluTW/Sys/SystemForm/FinishExam/StuFinishStudentScore.aspx", {
        //  "headers": {
        //    "accept": "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7",
        //    "accept-language": "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6",
        //    "cache-control": "no-cache",
        //    "pragma": "no-cache",
        //    "upgrade-insecure-requests": "1",
        //    "cookie": "ASP.NET_SessionId=xog130uka5dmv1x314gq5gdx; CenterSoft=FBA84D75C17E79A16FCB2FC108C2CAA830EC75D1C2D9933E3FEE8F41DC2C280470D75ABBB25AD0059BF6EE3798BCCD79ED5E3573A15F4A23B827BBD7F731B7FBA287524B6649BC9C39CECEC21519265ABE7A9888006E865CA683A68B1C7716D686C4E98887B081CAD6B03E6922E4127C2A37443293D5D9AF611F8CB34188418DE1FB1DA9E638F9E4B053F70DABB6C6F5077E1BF7E779B03A268BC9E70D143B2F953F3E7BBD2FEBA95CB97D1C06E21369E9683ABF97C3EB2CDB214E79FC930DD78DF4F3B6CA9DF17DDD965E624B26A2E5AE3EACB548910A8DC5159365E6536D4A2B08955ADDB0553BBE376260C0D4B378",
        //    "Referer": "http://xg.sylu.edu.cn/SyluTW/Sys/SystemForm/Navigation.aspx",
        //    "Referrer-Policy": "strict-origin-when-cross-origin"
        //  },
        //  "body": null,
        //  "method": "GET"
        //});

        client = new HttpClient()
                .url("http://xg.sylu.edu.cn/SyluTW/Sys/SystemForm/FinishExam/StuFinishStudentScore.aspx")
                .setCookie("ASP.NET_SessionId", resp.cookie("ASP.NET_SessionId"))
                .setCookie("CenterSoft", Objects.requireNonNull(resp.header("Set-Cookie")).split("CenterSoft=")[2].split("; ")[0]);
        resp = client.get();
        try {
            dom = resp.parse();
        } catch (IOException ignored) {
        }

        SecondClassData data = new SecondClassData();
        for (char a = 'A'; a <= 'E'; a++) {
            Double min = Double.parseDouble(dom.getElementById("Count" + a).text());
            String e = dom.getElementById("Count" + a + "1").text();
            Double now = Double.parseDouble(e.isEmpty() ? "0.00" : e);
            try {
                SecondClassData.class.getMethod("set" + a, double.class).invoke(data, min);
                SecondClassData.class.getMethod("set" + a + "1", double.class).invoke(data, now);
            } catch (Exception ignored) {
            }

        }
        data.setSum(Double.parseDouble(dom.getElementById("SunCount").text()));

        String e = dom.getElementById("SunCount1").text();
        data.setSum1(Double.parseDouble(e.isEmpty() ? "0.00" : e));
        return data;
    }

    public HashMap<String, BigInnovation> getBigInnovations() {
        assertLogin();
        return ExceptionUtil.executeUntilNoException(() -> {
            HashMap<String, BigInnovation> map = new HashMap<>();
            Connection.Response resp = ParamUtil.addQueryListParam(client.clearData()
                    .url(compile("/xmfzgl/xshdfzcx_cxXshdfzcxIndex.html?doType=query&gnmkdm=N4780&su=", getStuCode()))
            ).post();

            JSON.parseObject(resp.body()).getJSONArray("items").forEach((i) -> {
                JSONObject object = (JSONObject) i;
                String name = object.getString("xmlbmc");
                map.put(name,
                        ExceptionUtil.executeUntilNoException(
                                () -> getInnovationByTag(name)
                                , 30000)
                );
            });
            return map;
        }, 30000);
    }

    private BigInnovation getInnovationByTag(String name) {
        BigInnovation innovation = new BigInnovation();
        Connection.Response resp = ParamUtil.addQueryListParam(client.url(compile("/xmfzgl/xshdfzcx_cxXmfzqr.html?gnmkdm=N4780&su=", getStuCode())).clearData())
                .data("xmlbmc", name)
                .post();
        JSON.parseObject(resp.body()).getJSONArray("items").forEach((i) -> {
            JSONObject object = (JSONObject) i;
            innovation.add(new BigInnovation.Item(object.getString("xmnr"), object.getString("yxfz")));
        });
        return innovation;
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

    //获取校历
    public SchoolCalendar getSchoolCalendar() {
        return new SchoolCalendar(this);
    }

    //获取考试结果
    public ExamResult getExamResult() {
        return new ExamResult(this);
    }

    //获取课程表
    public Schedule getSchedule() {
        return new Schedule(this);
    }

    public void assertLogin() {
        if (!isLogin()) {
            throw new OfflineException("登录状态为未登录或踢下线");
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SyluSession.class.getSimpleName() + "[", "]")
                .add("client=" + client)
                .add("user='" + user + "'")
                .toString();
    }
}
