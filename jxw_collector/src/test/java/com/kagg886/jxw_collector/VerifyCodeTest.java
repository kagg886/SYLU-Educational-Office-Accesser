package com.kagg886.jxw_collector;

import com.kagg886.jxw_collector.protocol.SyluSession;
import org.junit.jupiter.api.Assertions;

import javax.imageio.ImageIO;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

/**
 * 验证码测试
 *
 * @author kagg886
 * @date 2023/7/5 16:57
 **/
public class VerifyCodeTest {

    private static final String pwd;

    static {
        System.out.println(new File("pwd.txt").getAbsolutePath());
        try {
            pwd = new BufferedReader(new FileReader("pwd.txt")).readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
        SyluSession session = new SyluSession("2203050528");
        for (int i = 0; i < 5; i++) {
            try {
                session.loginByPwd("pwd");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println(session.needVerifyCode());
        System.out.println(session.getVerifyLink());

        File f = new File("captcha.png");
        if (f.exists()) {
            f.createNewFile();
        }
        System.out.println(f.getAbsolutePath());
        ImageIO.write(
                ImageIO.read(session.getClient().url(session.getVerifyLink()).get().bodyStream()),
                "PNG",
                f
        );

        Scanner scanner = new Scanner(System.in);

        String code = scanner.nextLine();
        Assertions.assertDoesNotThrow(() -> session.loginByPwd(pwd, code));
    }
}
