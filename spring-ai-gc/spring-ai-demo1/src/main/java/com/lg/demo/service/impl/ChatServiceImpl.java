package com.lg.demo.service.impl;
import com.lg.demo.dto.ChatRequestDTO;
import com.lg.demo.service.ChatService;
import com.lg.demo.service.Intent;
import com.lg.demo.tools.NameCountsTools;
import com.lg.demo.tools.UserTools;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
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

    @Autowired
    private UserTools userTools;

    /**
     * 缓存 ChatClient 实例，避免重复创建
     */
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
                    .defaultSystem("""
                            你是一个专业的智能AI客服。
                            # 规则
                            -回答客户问题是，前确认理解正确再回答客户问题
                            -每次回答只针对用户当前的问题
                            """)
                    .defaultTools(nameCountsTools)
                    .build();
        });
    }

    @Override
    public Flux<String> streamChat(ChatRequestDTO request) {
        String conversationId = resolveConversationId(request);
        Intent.IntentClassification classification = classifyIntent(request);
        log.info("意图分类: {}, 摘要: {}", classification.intent(), classification.summary());
        ChatClient chatClient = getChatClient(request.getModel());
        return switch (classification.intent()) {
            case CHAT -> {
                log.info("普通聊天 - 模型: {}, 会话ID: {}", request.getModel(), conversationId);
                yield chatClient.prompt()
                        .user(request.getMessage())
                        .advisors(advisorSpec -> advisorSpec
                                .param(ChatMemory.CONVERSATION_ID, conversationId))
                        .stream()
                        .content();
            }
            case NAME_QUERY -> {
                log.info("名字数量查询 - 模型: {}, 会话ID: {}", request.getModel(), conversationId);
                yield chatClient.prompt()
                        .user(request.getMessage())
                        .advisors(advisorSpec -> advisorSpec
                                .param(ChatMemory.CONVERSATION_ID, conversationId))
                        .stream()
                        .content();
            }
            case USER_QUERY -> {
                log.info("用户查询 - 模型: {}, 会话ID: {}", request.getModel(), conversationId);
                yield chatClient.prompt()
                        .user(request.getMessage())
                        .advisors(advisorSpec -> advisorSpec
                                .param(ChatMemory.CONVERSATION_ID, conversationId))
                        .tools(userTools)
                        .stream()
                        .content();
            }
        };
    }

    private Intent.IntentClassification classifyIntent(ChatRequestDTO request) {
        ChatClient intentClient = getIntentChatClient(request.getModel());
        return intentClient.prompt()
                .user(request.getMessage())
                .call()
                .entity(Intent.IntentClassification.class);
    }


    private ChatClient getIntentChatClient(String model) {
        return chatClientCache.computeIfAbsent("intent-" + model, key -> {
            var chatModel = switch (model) {
                case "dashscope" -> dashScopeChatModel;
                case "ollama" -> ollamaChatModel;
                default -> deepSeekChatModel;
            };
            return ChatClient.builder(chatModel)
                    .defaultSystem(buildIntentSystemPrompt())
                    .build();
        });
    }

    private String buildIntentSystemPrompt() {
        StringBuilder sb = new StringBuilder();
        sb.append("你是一个意图分类器。根据用户消息判断其意图类别。\n\n");
        sb.append("可选意图如下：\n");
        for (Intent intent : Intent.values()) {
            sb.append(String.format("- %s（%s）：%s\n", intent.name(), intent.getName(), intent.getDescription()));
        }
        sb.append("\n只返回JSON格式：{\"intent\":\"意图枚举名\",\"summary\":\"用户意图摘要\"}");
        return sb.toString();
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
