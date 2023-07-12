package com.kagg886.jxw_collector.util;

import com.kagg886.jxw_collector.exceptions.OfflineException;

import java.util.function.Supplier;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.kagg886.jxw_collector.util
 * @className: ExceptionUtil
 * @author: kagg886
 * @description: TODO
 * @date: 2023/4/19 17:46
 * @version: 1.0
 */
public class ExceptionUtil {

    //在同线程中运行一段代码，出错重复运行直到没有bug
    public static <T> T executeUntilNoException(Supplier<T> func, int delays) {
        long now = System.currentTimeMillis();
        while (System.currentTimeMillis() - now < delays) {
            try {
                return func.get();
            } catch (Exception e) {
                e.printStackTrace();
                if (e instanceof OfflineException) {
                    throw e;
                }
                if (e instanceof Ignored) {
                    throw ((RuntimeException) e.getCause());
                }
            }
        }
        return null;
    }

    public static class Ignored extends RuntimeException {
        public Ignored(Throwable cause) {
            super(cause);
        }
    }
}
