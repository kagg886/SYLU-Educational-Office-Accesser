package com.kagg886.jxw_collector.protocol.beans.abs;

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

    private final SyluSession session;
    private final HashMap<String, String> teamVal = new HashMap<>(); //键为学期，值为代码
    private final HashMap<String, String> years = new HashMap<>();//键为年，值为代码

    private String defaultTeamVal, defaultYears;


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

    public String getDefaultTeamVal() {
        return defaultTeamVal;
    }

    public String getDefaultYears() {
        return defaultYears;
    }

    public SyluSession getSession() {
        return session;
    }

    public HashMap<String, String> getTeamVal() {
        return teamVal;
    }

    public HashMap<String, String> getYears() {
        return years;
    }

}
