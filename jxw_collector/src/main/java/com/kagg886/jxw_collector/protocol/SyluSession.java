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

import java.util.*;

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

    private long stamp; //最近一次检查登录的成果

    private boolean isLogin; //最近一次检查登录是否成功

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
        client.header("Cookie", resp.header("Set-Cookie"))
                .header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
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
        isLogin = true;
    }

    public void loginByCookie(String cookie) {
        client.header("Cookie", cookie);
        assertLogin();
    }

    public boolean isLogin() {
        if (System.currentTimeMillis() - stamp > 1000) {
            return isLogin;
        }
        stamp = System.currentTimeMillis();
        client.url(compile("/xtgl/index_initMenu.html?jsdm=xs&_t=" + new Date().getTime()));
        Connection.Response resp = client.get();
        Document document = Jsoup.parse(resp.body());
        for (Element e : document.getElementsByTag("h5")) {
            if (e.text().equals("用户登录")) {
                isLogin = false;
                return false;
            }
        }
        isLogin = true;
        return true;
    }

    public String compile(Object... p) {
        StringBuilder builder = new StringBuilder(base);
        Arrays.stream(p).forEach(builder::append);
        return builder.toString();
    }

    public List<TeacherRelate> getRelate() {
        return ExceptionUtil.executeUntilNoException(() -> {
            List<TeacherRelate> rtn = new ArrayList<>();
            Connection.Response resp = ParamUtil.addQueryListParam(getClient()
                    .url(compile("/xspjgl/xspj_cxXspjIndex.html?doType=query&gnmkdm=N401605&su=", SyluSession.this.getStuCode()))
                    .clearData()).post();
            JSON.parseObject(resp.body()).getJSONArray("items").forEach((i) -> {
                JSONObject obj = (JSONObject) i;
                //"date":"二○二三年四月二十日",
                //"dateDigit":"2023年4月20日",
                //"dateDigitSeparator":"2023-4-20",
                //"day":"20",
                //"jgh_id":"71a8f412cb8684718ba4fb699b553d433ce4109d3eebf61f4b277766a6f5afd4ebb9ee03e23b29d1a49cb2647d6ef6e436121f1460ae62485483c20d39736c3ceb8f03bb5bb12f4148e1962a2675fc68b1b3b553d7f68b21fcf2ce81c5cd5b8d0a71e8e2b5e1aebfe1cb17892ef0569bed0fc5f120f052bfb8c106fc58ee5201",
                //"jgpxzd":"1",
                //"jxb_id":"E1A0AA89B272BB03E0530100050A8EC9",
                //"jxbmc":"(2022-2023-1)-211700009-篮球1-1",
                //"jxdd":"篮球场1",
                //"jzgmc":"张绍英",
                //"kch_id":"211700009",
                //"kcmc":"体育1",
                //"listnav":"false",
                //"localeKey":"zh_CN",
                //"month":"4",
                //"pageTotal":0,
                //"pageable":true,
                //"pjmbmcb_id":"EFEF6F98795A68ECE0530200050A22D0",
                //"pjzt":"1",
                //"rangeable":true,
                //"row_id":"1",
                //"sfcjlrjs":"1",
                //"sksj":"星期三第3-4节{6-17周}",
                //"tjzt":"1",
                //"tjztmc":"提交",
                //"totalResult":"23",
                //"xnm":"2022",
                //"xqm":"3",
                //"xsdm":"01",
                //"xsmc":"讲课学时",
                //"year":"2023"
                rtn.add(new TeacherRelate(SyluSession.this,
                                obj.getString("jxb_id"), obj.getString("kch_id"),
                                obj.getString("xsdm"), obj.getString("jgh_id"),
                                obj.getString("pjmbmcb_id"), obj.getString("sfcjlrjs"),
                                obj.getString("kcmc"), obj.getString("jzgmc"),
                                Integer.parseInt(obj.getString("pjzt")), Integer.parseInt(obj.getString("tjzt"))
                        )
                );
            });

            return rtn;
        }, 30000);
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

    public SchoolCalendar getSchoolCalendar() {
        return new SchoolCalendar(this);
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

    @Override
    public String toString() {
        return new StringJoiner(", ", SyluSession.class.getSimpleName() + "[", "]")
                .add("client=" + client)
                .add("user='" + user + "'")
                .toString();
    }
}
