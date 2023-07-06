package com.kagg886.jxw_collector;

import com.kagg886.jxw_collector.exceptions.OfflineException;
import com.kagg886.jxw_collector.protocol.SyluSession;
import com.kagg886.jxw_collector.protocol.beans.SecondClassData;
import org.junit.jupiter.api.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static com.kagg886.jxw_collector.PWD.pwd;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.kagg886.jxw_collector
 * @className: APITest
 * @author: kagg886
 * @description: API测试
 * @date: 2023/4/13 12:44
 * @version: 1.0
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LoginTest {

    @Test
    @Order(1)
    public void doLoginTestSuccess() throws Exception {
        SyluSession session = new SyluSession("2203050528");
        session.loginByPwd(pwd);
        SecondClassData data = session.getSecondClassData(pwd);
        System.out.println(data);
    }

    @Test
    @Order(2)
    public void doLoginTestFailed() {
        SyluSession session = new SyluSession("2203050528");
        Assertions.assertThrows(OfflineException.LoginFailed.class,() -> {
            session.loginByPwd("12345678");
        });
    }

    @Test
    @Order(3)
    public void isLoginTest() {
        SyluSession session = new SyluSession("2203050528");
        Assertions.assertThrows(OfflineException.LoginFailed.class, () -> {
            session.loginByPwd("12345678");
        });
        Assertions.assertFalse(session.isLogin());
        session.loginByPwd(pwd);
        Assertions.assertTrue(session.isLogin());
    }

    @Test
    @Order(4)
    public void testLoginByCookie() {
        SyluSession session = new SyluSession("2203050528");
        Assertions.assertDoesNotThrow(() -> {
            session.loginByCookie("JSESSIONID=8AB76F0E7C772AA7D68384C98D5D61A1");
        });
        System.out.println(session.getUserInfo().toString());
    }

    @Test
    @Order(5)
    public void testLogout() {
        SyluSession session = new SyluSession("2203050528");
        session.loginByPwd(pwd);
        session.logout();
        Assertions.assertThrows(OfflineException.class,() -> {
            System.out.println(session.getUserInfo());
        });

    }
}
