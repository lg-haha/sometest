package com.lg.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 聊天请求DTO
 */
@Data
public class ChatRequestDTO {

    /** 用户消息 */
    @NotBlank(message = "消息不能为空")
    private String message;

    /** 会话ID，用于隔离聊天记忆 */
    private String conversationId;

    /** 模型选择：deepseek / dashscope / ollama */
    private String model = "deepseek";
}
