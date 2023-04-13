package com.kagg886.jxw_collector;

import com.kagg886.jxw_collector.exceptions.OfflineException;
import com.kagg886.jxw_collector.protocol.SyluSession;
import org.junit.jupiter.api.*;

import java.io.*;

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
public class APITest {
    private static final String pwd;
    static {
        try {
            pwd =new BufferedReader(new FileReader("pwd.txt")).readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Order(1)
    public void doLoginTestSuccess() throws Exception {
        SyluSession session = new SyluSession("2203050528");
        session.login(pwd);
    }

    @Test
    @Order(2)
    public void doLoginTestFailed() {
        SyluSession session = new SyluSession("2203050528");
        Assertions.assertThrows(OfflineException.LoginFailed.class,() -> {
            session.login("12345678");
        });
    }

    @Test
    @Order(3)
    public void isLoginTest() {
        SyluSession session = new SyluSession("2203050528");
        Assertions.assertThrows(OfflineException.LoginFailed.class,() -> {
            session.login("12345678");
        });
        Assertions.assertFalse(session.isLogin());
        session.login(pwd);
        Assertions.assertTrue(session.isLogin());
    }
}
