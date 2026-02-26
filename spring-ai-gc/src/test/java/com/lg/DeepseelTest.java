package com.lg;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @PackageName: com.lg
 * @ClassName: DeepseelTest
 * @Description:
 * @author: lg
 * @data: 2026/1/30 10:13
 */
@SpringBootTest
public class DeepseelTest {
    // 对话测试
    @Test
    public void testChat(@Autowired
                         DeepSeekChatModel chatModel) {
        String call = chatModel.call("你好");
        System.out.println(call);
    }

    /**
     * options 配置项
     * temperature: 控制生成文本的随机性，值越大生成的文本越随机，值越小生成的文本越确定。
     * @param chatModel
     */
    @Test
    public void testChatOptions(@Autowired
                         DeepSeekChatModel chatModel) {
        ChatOptions options = ChatOptions.builder().temperature(2.0).build();
        ChatResponse res = chatModel.call(new Prompt("请写一句诗描述清晨", options));
        System.out.println( res.getResult().getOutput().getText());
    }


}
