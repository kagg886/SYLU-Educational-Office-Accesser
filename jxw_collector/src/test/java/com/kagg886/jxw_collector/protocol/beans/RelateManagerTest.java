package com.kagg886.jxw_collector.protocol.beans;

import com.kagg886.jxw_collector.protocol.SyluSession;
import com.kagg886.jxw_collector.protocol.beans.relate.RelateManager;
import com.kagg886.util.IOUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.kagg886.jxw_collector.PWD.pwd;

class RelateManagerTest {

    @Test
    void getInfos() {
        SyluSession session = new SyluSession("2203050528");
        session.loginByPwd(pwd);
        RelateManager manager = session.getMustRelates();

        System.out.println(manager.getInfos().get(1));
        manager.submit(manager.getInfos().get(1));
    }


    @Test
    void checkSafe() throws IOException {
        String[] official = IOUtil.loadStringFromFile("C:\\Users\\kagg886\\Desktop\\work\\官方.txt")
                .replace("\r","\n")
                .replace("\n\n","\n")
                .replace(": ","=")
                .split("\n");
        String[] me = IOUtil.loadStringFromFile("C:\\Users\\kagg886\\Desktop\\work\\民间.txt").replace("\r","\n").replace("\n\n","\n").split("\n");

        for (String o : me) {
            if (o.endsWith("=")) {
                o = o + "=none";
            }
            for (String i : official) {
                if (i.endsWith("=")) {
                    i = i + "=none";
                }
                String[] os = o.split("=");
                String[] is = i.split("=");

                if (os[0].equals(is[0])) {
                    System.out.printf("%s->%s%n",os[0],os[1].equals(is[1]) ? "true" : "false\nexpect:" +os[1] + "\ninfact:" + is[1] + "\n\n\n");
                }
            }
        }
    }
}