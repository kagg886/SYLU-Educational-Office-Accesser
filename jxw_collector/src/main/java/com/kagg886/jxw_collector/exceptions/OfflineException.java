package com.kagg886.jxw_collector.exceptions;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.kagg886.jxw_collector.exceptions
 * @className: OfflineException
 * @author: kagg886
 * @description: 教务网下线的异常
 * @date: 2023/4/13 13:57
 * @version: 1.0
 */
public class OfflineException extends RuntimeException {
    public OfflineException(String s) {
        super(s);
    }

    public static class LoginFailed extends OfflineException {

        public LoginFailed(String s) {
            super(s);
        }
    }

    public static class CookieOutOfDate extends OfflineException {
        public CookieOutOfDate(String s) {
            super(s);
        }
    }
}
