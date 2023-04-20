package com.kagg886.jxw_collector;

import com.kagg886.jxw_collector.exceptions.OfflineException;
import com.kagg886.jxw_collector.protocol.SyluSession;
import com.kagg886.jxw_collector.protocol.beans.ClassTable;
import com.kagg886.jxw_collector.protocol.beans.ExamResult;
import com.kagg886.jxw_collector.protocol.beans.Schedule;
import com.kagg886.jxw_collector.protocol.beans.UserInfo;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.kagg886.jxw_collector
 * @className: BaseAPITest
 * @author: kagg886
 * @description: 个人信息相关API测试
 * @date: 2023/4/13 14:56
 * @version: 1.0
 */
public class BaseAPITest {
    private static final String pwd;

    static {
        try {
            pwd = new BufferedReader(new FileReader("pwd.txt")).readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testRelateDecode() {
        SyluSession session = new SyluSession("2203050528");
        session.loginByPwd(pwd);
        session.getRelate().forEach(System.out::println);
    }

    @Test
    void testBigInnovation() {
        SyluSession session = new SyluSession("2203050528");
        session.loginByPwd(pwd);
        session.getBigInnovations().forEach((K, V) -> {
            System.out.printf("%s:\n%s\n", K, V.toString());
        });
    }

    @Test
    public void testClassTableGet() {
        SyluSession session = new SyluSession("2203050528");
        session.loginByPwd(pwd);
        Schedule schedule = session.getSchedule();
        ClassTable table = schedule.queryClassByYearAndTerm("2022-2023", "2");
        System.out.println(table);
    }

    @Test
    public void testExamResult() {
        SyluSession session = new SyluSession("2203050528");
        session.loginByPwd(pwd);
        ExamResult result = session.getExamResult();

        System.out.println(result.getDefaultTeamVal());
        System.out.println(result.getDefaultYears());

        result.queryResultByYearAndTerm(result.getDefaultYears(), result.getDefaultTeamVal())
                .forEach(System.out::println);
    }

    @Test
    public void testExamDetails() {
        SyluSession session = new SyluSession("2203050528");
        session.loginByPwd(pwd);
        ExamResult result = session.getExamResult();

        ExamResult.ExamInfo info = result.queryResultByYearAndTerm(result.getDefaultYears(), result.getDefaultTeamVal()).get(0);

        List<List<String>> k = result.queryDetailsByExamInfo(info);

        System.out.println(session);
        Assertions.assertTrue(k.size() != 0);
        k.forEach((col) -> {
            col.forEach((unit) -> System.out.print(unit + " "));
            System.out.println();
        });
    }

    @Test
    public void testExamOrigin() throws IOException {
        Connection connection = Jsoup.connect("https://jxw.sylu.edu.cn/cjcx/cjcx_cxCjxqGjh.html?time=" + new Date().getTime() + "&gnmkdm=N305005&su=2203050528");
        connection.data("jxb_id", "F75019F3F5DA44D8E0530100050A0316");
        connection.data("xnm", "2022");
        connection.data("xqm", "12");
        connection.data("kcmc", "大学外语1");

        connection.header("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
        connection.header("Cookie", "JSESSIONID=615A80A0981FA2B3558599BE99AA087F");
        connection.header("Host", "jxw.sylu.edu.cn");
        connection.header("Origin", "https://jxw.sylu.edu.cn");
        connection.header("Referer", "https://jxw.sylu.edu.cn/cjcx/cjcx_cxDgXscj.html?gnmkdm=N305005&layout=default&su=2203050528");
        connection.header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/112.0.0.0 Safari/537.36 Edg/112.0.1722.48");
        connection.header("X-Requested-With", "XMLHttpRequest");

        System.out.println(connection.method(Connection.Method.POST).execute().body());
    }

    @Test
    public void testClassQueryByWeekDay() {
        SyluSession session = new SyluSession("2203050528");
        session.loginByPwd(pwd);
        Schedule schedule = session.getSchedule();
        ClassTable table = schedule.queryClassByYearAndTerm("2022-2023", "2");
        ClassTable table0;
        table0 = table.queryClassByWeek(17);
        System.out.println(table0);
        Assertions.assertEquals(3, table0.size());

        table0 = table.queryClassByWeek(20);
        System.out.println(table0);
        Assertions.assertEquals(0, table0.size());

        table0 = table.queryClassByWeek(14).queryClassByDay(1);
        System.out.println(table0);
        Assertions.assertEquals(3, table0.size());
    }

    @Test
    public void testClassQueryByLesson() {
        SyluSession session = new SyluSession("2203050528");
        session.loginByPwd(pwd);
        Schedule schedule = session.getSchedule();
        ClassTable table = schedule.queryClassByYearAndTerm("2022-2023", "2");
        System.out.println(table.queryClassByLesson("1-2").toString());
    }

    @Test
    public void testUserProfile() {
        SyluSession session = new SyluSession("2203050528");
        Assertions.assertThrows(OfflineException.class, () -> {
            UserInfo info = session.getUserInfo();
            System.out.println(info);
        });

        session.loginByPwd(pwd);
        System.out.println(session.getUserInfo().toString());
    }

    @Test
    public void testSchoolCalendar() {
        SyluSession session = new SyluSession("2203050528");
        Assertions.assertThrows(OfflineException.class, () -> {
            UserInfo info = session.getUserInfo();
            System.out.println(info);
        });

        session.loginByPwd(pwd);
        System.out.println(session.getSchoolCalendar().toString());
    }
}
