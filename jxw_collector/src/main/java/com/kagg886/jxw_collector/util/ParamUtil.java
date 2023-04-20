package com.kagg886.jxw_collector.util;

import com.kagg886.jxw_collector.internal.HttpClient;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.kagg886.jxw_collector.util
 * @className: ParamUtil
 * @author: kagg886
 * @description: 参数解析类
 * @date: 2023/4/20 22:02
 * @version: 1.0
 */
public class ParamUtil {

    public static HttpClient addQueryListParam(HttpClient client) {
        return client.data("nd", String.valueOf(System.currentTimeMillis()))
                .data("_search", "false")
                .data("queryModel.showCount", "5000")
                .data("queryModel.currentPage", "1")
                .data("queryModel.sortName:", "")
                .data("queryModel.sortOrder", "asc")
                .data("time", "0");
    }
}
