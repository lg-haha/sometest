package com.lg.demo.service;

import com.lg.demo.dto.ChatRequestDTO;
import reactor.core.publisher.Flux;

/**
 * 聊天服务接口
 */
public interface ChatService {

    /**
     * 流式聊天（SSE）
     */
    Flux<String> streamChat(ChatRequestDTO request);

    /**
     * 同步聊天
     */
    String chat(ChatRequestDTO request);
}
