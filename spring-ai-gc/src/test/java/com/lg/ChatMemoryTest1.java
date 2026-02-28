package com.lg;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.lg.config.ReReadingAdvisor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @PackageName: com.lg
 * @ClassName: ChatMemoryTest1
 * @Description:
 * @author: lg
 * @data: 2026/2/28 16:26
 */
@SpringBootTest
public class ChatMemoryTest1 {
    ChatClient chatClient;

    @BeforeEach
    public void init(@Autowired
                     DashScopeChatModel chatModel,
                     @Autowired
                     ChatMemory chatMemory) {
        chatClient = ChatClient
                .builder(chatModel)
                .defaultAdvisors(
                        PromptChatMemoryAdvisor.builder(chatMemory).build()
                )
                .build();
    }

    @Test
    public void testChatOptions() {
        String content = chatClient.prompt()
                .user("我叫lg，这是我的名字，很高兴认识你？")
                .call()
                .content();
        System.out.println(content);
        System.out.println("--------------------------------------------------------------------------");

        content = chatClient.prompt()
                .user("我叫什么 ？")
                .call()
                .content();
        System.out.println(content);
    }


    @Test
    public void testChatOptions1() {
        String content = chatClient.prompt()
                .user("我叫lg ？")
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID,"1"))
                .call()
                .content();
        System.out.println(content);
        System.out.println("--------------------------------------------------------------------------");

        content = chatClient.prompt()
                .user("我叫什么 ？")
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID,"1"))
                .call()
                .content();
        System.out.println(content);


        System.out.println("--------------------------------------------------------------------------");

        content = chatClient.prompt()
                .user("我叫什么 ？")
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID,"2"))
                .call()
                .content();
        System.out.println(content);
    }
}
