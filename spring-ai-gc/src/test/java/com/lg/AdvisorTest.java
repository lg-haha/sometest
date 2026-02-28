package com.lg;

import com.lg.config.ReReadingAdvisor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @PackageName: com.lg
 * @ClassName: AdvisorTest
 * @Description: 对话拦截测试
 * @author: lg
 * @data: 2026/2/28 14:47
 */
@SpringBootTest
public class AdvisorTest {
    ChatClient chatClient;

    @BeforeEach
    public void init(@Autowired
                     DeepSeekChatModel chatModel) {
        chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(
                        new SimpleLoggerAdvisor()
                )
                .build();
    }

    @Test
    public void testChatOptions() {
        String content = chatClient.prompt()
                .user("你好")
                .call()
                .content();
        System.out.println(content);
    }

    @Test
    public void testChatoptions1() {
        String content = chatClient.prompt()
                .user("中国有多大？")
                .advisors(new ReReadingAdvisor())
                .call()
                .content();
        System.out.println(content);
    }
}
