package com.kagg886.jxw_collector.protocol.beans.abs;

import com.alibaba.fastjson.annotation.JSONField;
import com.kagg886.jxw_collector.protocol.SyluSession;
import com.kagg886.jxw_collector.util.ExceptionUtil;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Objects;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.kagg886.jxw_collector.protocol.beans
 * @className: YearSemesterSelectable
 * @author: kagg886
 * @description: TODO
 * @date: 2023/4/14 18:52
 * @version: 1.0
 */
public abstract class YearSemesterSelectable {

    @JSONField(deserialize = false, serialize = false)
    private SyluSession session;
    private HashMap<String, String> teamVal = new HashMap<>(); //键为学期，值为代码
    private HashMap<String, String> years = new HashMap<>();//键为年，值为代码

    private String defaultTeamVal, defaultYears;

    public YearSemesterSelectable() {

    }


    public YearSemesterSelectable(SyluSession session, String url) {
        this.session = session;
        session.assertLogin();
        ExceptionUtil.executeUntilNoException(() -> {
            Connection.Response resp = session.getClient()
                    .url(url)
                    .get();
            Document document = Jsoup.parse(resp.body());
            for (Element e : Objects.requireNonNull(document.getElementById("xnm")).getElementsByTag("option")) {

                if (e.attr("selected").equals("selected")) {
                    defaultYears = e.text();
                }
                years.put(e.text(), e.attr("value"));
            }

            for (Element e : Objects.requireNonNull(document.getElementById("xqm")).getElementsByTag("option")) {
                if (!e.attr("selected").equals("")) {
                    defaultTeamVal = e.text();
                }
                teamVal.put(e.text(), e.attr("value"));
            }
            return null;
        }, 30000);
    }

    public YearSemesterSelectable(HashMap<String, String> teamVal, HashMap<String, String> years, String defaultTeamVal, String defaultYears) {
        this.teamVal = teamVal;
        this.years = years;
        this.defaultTeamVal = defaultTeamVal;
        this.defaultYears = defaultYears;
    }

    public SyluSession getSession() {
        return session;
    }

    public void setSession(SyluSession session) {
        this.session = session;
    }

    public HashMap<String, String> getTeamVal() {
        return teamVal;
    }

    public void setTeamVal(HashMap<String, String> teamVal) {
        this.teamVal = teamVal;
    }

    public HashMap<String, String> getYears() {
        return years;
    }

    public void setYears(HashMap<String, String> years) {
        this.years = years;
    }

    public String getDefaultTeamVal() {
        return defaultTeamVal;
    }

    public void setDefaultTeamVal(String defaultTeamVal) {
        this.defaultTeamVal = defaultTeamVal;
    }

    public String getDefaultYears() {
        return defaultYears;
    }

    public void setDefaultYears(String defaultYears) {
        this.defaultYears = defaultYears;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("YearSemesterSelectable{");
        sb.append("session=").append(session);
        sb.append(", teamVal=").append(teamVal);
        sb.append(", years=").append(years);
        sb.append(", defaultTeamVal='").append(defaultTeamVal).append('\'');
        sb.append(", defaultYears='").append(defaultYears).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
