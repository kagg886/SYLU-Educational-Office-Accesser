package com.qlstudio.lite_kagg886.util;

import java.io.*;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.qlstudio.lite_kagg886.util
 * @className: LogCatcher
 * @author: kagg886
 * @description: TODO
 * @date: 2023/4/24 18:21
 * @version: 1.0
 */
public class LogCatcher extends Thread {

    private BufferedWriter writer;

    private BufferedReader reader;

    public LogCatcher(File write) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(write));
        reader = new BufferedReader(new InputStreamReader(Runtime.getRuntime().exec("logcat").getInputStream()));
    }

    @Override
    public void run() {
        try {
            for (String s = reader.readLine(); ; s = reader.readLine()) {
                writer.write(s);
                writer.write("\n");
                writer.flush();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
