package com.lg;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.lg.demo.service.NameCountsTools;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @PackageName: com.lg
 * @ClassName: ToolTest
 * @Description:
 * @author: lg
 * @data: 2026/3/2 16:28
 */
@SpringBootTest
public class ToolTest {
    ChatClient chatClient;
    @BeforeEach
    public void init(@Autowired
                         DashScopeChatModel chatModel,
                     @Autowired
                     NameCountsTools nameCountsTools){
        chatClient = ChatClient.builder(chatModel)
                .defaultTools(nameCountsTools)
                .build();

    }
    @Test
    public void testChatOptions() {
        String content = chatClient.prompt()
                .user("火星有多少个叫lg的/no_think")
                // .tools() 也可以单独绑定当前对话
                .call()
                .content();
        System.out.println(content);
    }
}
