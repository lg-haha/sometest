package com.lg.demo.service.impl;

import com.lg.demo.dto.ChatRequestDTO;
import com.lg.demo.service.ChatService;
import com.lg.demo.service.NameCountsTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 聊天服务实现
 */
@Slf4j
@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private DeepSeekChatModel deepSeekChatModel;

    @Autowired
    private DashScopeChatModel dashScopeChatModel;

    @Autowired
    private OllamaChatModel ollamaChatModel;

    @Autowired
    private ChatMemory chatMemory;

    @Autowired
    private NameCountsTools nameCountsTools;

    /** 缓存 ChatClient 实例，避免重复创建 */
    private final Map<String, ChatClient> chatClientCache = new ConcurrentHashMap<>();

    /**
     * 根据模型名称获取对应的 ChatClient
     * 每个客户端挂载聊天记忆和工具调用
     */
    private ChatClient getChatClient(String model) {
        return chatClientCache.computeIfAbsent(model, key -> {
            var chatModel = switch (key) {
                case "dashscope" -> dashScopeChatModel;
                case "ollama" -> ollamaChatModel;
                default -> deepSeekChatModel;
            };
            return ChatClient.builder(chatModel)
                    .defaultAdvisors(
                            PromptChatMemoryAdvisor.builder(chatMemory).build()
                    )
                    .defaultTools(nameCountsTools)
                    .build();
        });
    }

    @Override
    public Flux<String> streamChat(ChatRequestDTO request) {
        String conversationId = resolveConversationId(request);
        ChatClient chatClient = getChatClient(request.getModel());
        log.info("流式聊天 - 模型: {}, 会话ID: {}", request.getModel(), conversationId);

        return chatClient.prompt()
                .user(request.getMessage())
                .advisors(advisorSpec -> advisorSpec
                        .param(ChatMemory.CONVERSATION_ID, conversationId))
                .stream()
                .content();
    }

    @Override
    public String chat(ChatRequestDTO request) {
        String conversationId = resolveConversationId(request);
        ChatClient chatClient = getChatClient(request.getModel());
        log.info("同步聊天 - 模型: {}, 会话ID: {}", request.getModel(), conversationId);

        return chatClient.prompt()
                .user(request.getMessage())
                .advisors(advisorSpec -> advisorSpec
                        .param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }

    private String resolveConversationId(ChatRequestDTO request) {
        if (request.getConversationId() == null || request.getConversationId().isBlank()) {
            return "default";
        }
        return request.getConversationId();
    }
}
