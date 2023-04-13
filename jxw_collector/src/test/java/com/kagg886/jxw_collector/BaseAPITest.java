package com.kagg886.jxw_collector;

import com.kagg886.jxw_collector.exceptions.OfflineException;
import com.kagg886.jxw_collector.protocol.SyluSession;
import com.kagg886.jxw_collector.protocol.beans.ClassTable;
import com.kagg886.jxw_collector.protocol.beans.Schedule;
import com.kagg886.jxw_collector.protocol.beans.UserInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
    public void testClassTableGet() {
        SyluSession session = new SyluSession("2203050528");
        session.login(pwd);
        Schedule schedule = session.getSchedule();
        ClassTable table = schedule.queryClassByYearAndTerm("2022-2023", "2");
        System.out.println(table);
    }

    @Test
    public void testClassQueryByWeek() {
        SyluSession session = new SyluSession("2203050528");
        session.login(pwd);
        Schedule schedule = session.getSchedule();
        ClassTable table = schedule.queryClassByYearAndTerm("2022-2023", "2");
        table = table.queryClassByWeek(17);
        System.out.println(table);
        Assertions.assertEquals(3, table.size());

        table = table.queryClassByWeek(20);
        System.out.println(table);
        Assertions.assertEquals(0, table.size());
    }

    @Test
    public void testUserProfile() {
        SyluSession session = new SyluSession("2203050528");
        Assertions.assertThrows(OfflineException.class, () -> {
            UserInfo info = session.getUserInfo();
            System.out.println(info);
        });

        session.login(pwd);
        System.out.println(session.getUserInfo().toString());
    }
}
