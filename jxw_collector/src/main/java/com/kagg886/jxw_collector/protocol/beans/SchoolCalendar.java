package com.kagg886.jxw_collector.protocol.beans;

import com.kagg886.jxw_collector.protocol.SyluSession;
import com.kagg886.jxw_collector.util.ExceptionUtil;
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

    private LocalDate start, end;
    private int terms;


    public SchoolCalendar(SyluSession session) {
        String source = ExceptionUtil.executeUntilNoException(() -> {
            session.assertLogin();
            Connection.Response resp = session.getClient().url(session.compile("/xtgl/index_cxAreaSix.html?localeKey=zh_CN&gnmkdm=index&su=", session.getStuCode()))
                    .post();
            Document document = Jsoup.parse(resp.body());
            return document.getElementsByAttributeValue("colspan", "24").get(0).text();
        }, 20000);
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

//    @JSONCreator
//    public SchoolCalendar(LocalDate start,LocalDate end,int terms) {
//        this.start = start;
//        this.end = end;
//        this.terms = terms;
//    }

    public SchoolCalendar() {

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

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public void setTerms(int terms) {
        this.terms = terms;
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
