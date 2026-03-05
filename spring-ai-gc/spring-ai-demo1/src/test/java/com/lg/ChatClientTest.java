package com.lg;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

/**
 * @PackageName: com.lg
 * @ClassName: ChatClientTest
 * @Description:
 * @author: lg
 * @data: 2026/2/28 12:12
 */
@SpringBootTest
public class ChatClientTest {
    @Autowired
    private DashScopeChatModel dashScopeChatModel;
    @Autowired
    private DeepSeekChatModel deepSeekChatModel;
    @Autowired
    private OllamaChatModel ollamaChatModel;

    @Test
    public void testChatOptions() {
        ChatClient chatClient = ChatClient.builder(ollamaChatModel).build();
        String content = chatClient.prompt()
                .user("你好")
                .call()
                .content();
        System.out.println(content);
    }

    @Test
    public void testChatStream() {
        ChatClient chatClient = ChatClient.builder(deepSeekChatModel).build();
        Flux<String> content = chatClient.prompt()
                .user("你好")
                .stream()
                .content();
        content.toIterable().forEach(System.out::print);
    }
}
