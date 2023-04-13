package com.kagg886.jxw_collector.protocol.beans;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kagg886.jxw_collector.internal.HttpClient;
import com.kagg886.jxw_collector.protocol.SyluSession;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.kagg886.jxw_collector.protocol.beans
 * @className: Schedule
 * @author: kagg886
 * @description: 课程表实例
 * @date: 2023/4/13 15:07
 * @version: 1.0
 */
public class Schedule {

    private final SyluSession session;
    private final HashMap<String,String> teamVal = new HashMap<>(); //键为学期值为代码
    private final HashMap<String,String> years = new HashMap<>();//键为年值为代码
    public Schedule(SyluSession session) {
        this.session = session;
        session.assertLogin();
        Connection.Response resp = session.getClient()
                .url(session.compile("/kbcx/xskbcx_cxXskbcxIndex.html?gnmkdm=ssss&layout=default&su=",session.getStuCode()))
                .get();
        Document document = Jsoup.parse(resp.body());
        for (Element e : Objects.requireNonNull(document.getElementById("xnm")).getElementsByTag("option")) {
            years.put(e.text(),e.attr("value"));
        }
        for (Element e : Objects.requireNonNull(document.getElementById("xqm")).getElementsByTag("option")) {
            teamVal.put(e.text(),e.attr("value"));
        }
    }

    public SyluSession getSession() {
        return session;
    }

    public ClassTable queryClassByYearAndTerm(String years,String teams) {
        String xnm = Objects.requireNonNull(this.years.get(years));
        String xqm = Objects.requireNonNull(this.teamVal.get(teams));
        return new ClassTable(xnm,xqm,session);
    }

    public HashMap<String, String> getTeamVal() {
        return teamVal;
    }

    public HashMap<String, String> getYears() {
        return years;
    }


}
