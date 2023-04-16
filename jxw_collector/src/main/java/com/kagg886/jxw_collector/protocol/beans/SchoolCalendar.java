package com.kagg886.jxw_collector.protocol.beans;

import com.kagg886.jxw_collector.protocol.SyluSession;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.time.LocalDate;
import java.util.StringJoiner;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.kagg886.jxw_collector.protocol.beans
 * @className: SchoolCalendar
 * @author: kagg886
 * @description: 校历的实例
 * @date: 2023/4/16 17:28
 * @version: 1.0
 */
public class SchoolCalendar {

    private final LocalDate start, end;
    private final int terms;


    public SchoolCalendar(SyluSession session) {
        session.assertLogin();
        Connection.Response resp = session.getClient().url(session.compile("/xtgl/index_cxAreaSix.html?localeKey=zh_CN&gnmkdm=index&su=", session.getStuCode()))
                .post();
        Document document = Jsoup.parse(resp.body());
        String source = document.getElementsByAttributeValue("colspan", "24").get(0).text();
        terms = Integer.parseInt(source.split("学年")[1].split("学期")[0]);

        int l, r;
        l = source.indexOf("(");
        r = source.indexOf(")");
        source = source.substring(l + 1, r);
        String[] se = source.split("至");

        String[] starts = se[0].split("-");
        start = LocalDate.of(Integer.parseInt(starts[0]), Integer.parseInt(starts[1]), Integer.parseInt(starts[2]));

        starts = se[1].split("-");
        end = LocalDate.of(Integer.parseInt(starts[0]), Integer.parseInt(starts[1]), Integer.parseInt(starts[2]));
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public int getTerms() {
        return terms;
    }

    public int getWeekFromStart() {
        LocalDate now = LocalDate.now();
        return (now.getDayOfYear() - start.getDayOfYear()) / 7 + 1;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", SchoolCalendar.class.getSimpleName() + "[", "]")
                .add("start=" + start)
                .add("end=" + end)
                .add("terms=" + terms)
                .toString();
    }
}
