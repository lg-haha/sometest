package com.lg;

import com.lg.config.ReReadingAdvisor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @PackageName: com.lg
 * @ClassName: ChatMemory
 * @Description: 记忆功能
 * @author: lg
 * @data: 2026/2/28 15:53
 */
@SpringBootTest
public class ChatMemoryTest {
    ChatMemory chatMemory;
    ChatClient chatClient;

    @BeforeEach
    public void init(@Autowired
                     DeepSeekChatModel chatModel) {
        chatMemory = MessageWindowChatMemory
                .builder()
                .maxMessages(10)
                .build();
        chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(
                        PromptChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

    @Test
    public void memoryTest(){
        String content = chatClient.prompt()
                .user("你好，我叫龙港")
                .advisors(new ReReadingAdvisor())
                .call()
                .content();
        System.out.println(content);
        System.out.println("--------------------------------------------------------------------------");

        content = chatClient.prompt()
                .user("我叫什么 ？")
                .advisors(new ReReadingAdvisor())
                .call()
                .content();
        System.out.println(content);
    }


}
