package com.kagg886.jxw_collector;

import com.kagg886.jxw_collector.protocol.SyluSession;
import com.kagg886.jxw_collector.protocol.beans.SecondClassData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.kagg886.jxw_collector
 * @className: TWTest
 * @author: kagg886
 * @description: 研究团委系统
 * @date: 2023/4/27 13:25
 * @version: 1.0
 */
public class TWTest {

    private static final String pwd;

    static {
        try {
            pwd = new BufferedReader(new FileReader("pwd.txt")).readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testTWAPI() {
        SyluSession session = new SyluSession("2203050528");
        session.loginByPwd(pwd);
        Assertions.assertDoesNotThrow(() -> {
            SecondClassData data = session.getSecondClassData("Iveour@163.com");
            System.out.println(data.toString());
        });
    }

}
