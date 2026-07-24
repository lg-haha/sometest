package com.lg.demo.service;

import lombok.Getter;

/** 意图枚举类
 * @PackageName: com.lg.demo.service
 * @ClassName: Intent
 * @Description:
 * @author: lg
 * @data: 2026/7/24 10:33
 */
@Getter
public enum Intent {
    CHAT("普通聊天", "用户进行日常对话、闲聊、问候等，不涉及具体业务功能"),
    NAME_QUERY("名字数量查询", "用户询问某个名字在长沙的数量、分布等信息"),
    USER_QUERY("用户查询", "用户询问用户信息"),
    // 新增意图只需加一行，无需改其他代码
    // WEATHER_QUERY("天气查询", "用户询问天气相关信息")
    ;

    private final String name;
    private final String description;

    Intent(String name, String description) {
        this.name = name;
        this.description = description;
    }
    public record IntentClassification(Intent intent, String summary) {}
}
