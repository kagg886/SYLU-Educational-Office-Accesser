package com.kagg886.jxw_collector;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author kagg886
 * @date 2023/7/6 10:45
 **/
public class PWD {
    public static final String pwd;
    static {
        try {
            System.out.println(new File("pwd.txt").getAbsolutePath());
            pwd =new BufferedReader(new FileReader("pwd.txt")).readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
