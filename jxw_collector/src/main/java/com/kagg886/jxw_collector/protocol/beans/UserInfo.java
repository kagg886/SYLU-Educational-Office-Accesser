package com.kagg886.jxw_collector.protocol.beans;

import java.util.StringJoiner;

/**
 * @projectName: 掌上沈理青春版
 * @package: com.kagg886.jxw_collector.protocol.beans
 * @className: UserInfo
 * @author: kagg886
 * @description: 用户信息
 * @date: 2023/4/13 14:48
 * @version: 1.0
 */
public class UserInfo {
    private final String avatar;
    private final String name;
    private final String college;

    public UserInfo(String avatar, String name, String college) {
        this.avatar = avatar;
        this.name = name;
        this.college = college;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getName() {
        return name;
    }

    public String getCollege() {
        return college;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", UserInfo.class.getSimpleName() + "[", "]")
                .add("avatar='" + avatar + "'")
                .add("name='" + name + "'")
                .add("college='" + college + "'")
                .toString();
    }
}
