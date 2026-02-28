package com.lg.config;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.AdvisorChain;
import org.springframework.ai.chat.client.advisor.api.BaseAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Map;

/**
 * @PackageName: com.lg.config
 * @ClassName: ReReadingAdvisor
 * @Description: 重读
 * @author: lg
 * @data: 2026/2/28 15:19
 */
public class ReReadingAdvisor implements BaseAdvisor {

    private static final String DEFAULT_USER_TEXT_ADVISE = """
            {re2_input_query}
            Read the question again: {re2_input_query}
            """;

    @Override
    public ChatClientRequest before(ChatClientRequest chatClientRequest, AdvisorChain advisorChain) {
        //获取用户输入文本
        String inputQuery = chatClientRequest.prompt().getUserMessage().getText();
        //定义重复输入模板
        String augmentedSystemText = PromptTemplate.builder().template(DEFAULT_USER_TEXT_ADVISE).build()
                .render(Map.of("re2_input_query", inputQuery));
        //设置请求的提示词
        ChatClientRequest processedChatClientRequest = chatClientRequest.builder()
                .prompt(Prompt.builder().content(augmentedSystemText).build())
                .build();

        return processedChatClientRequest;
    }

    @Override
    public ChatClientResponse after(ChatClientResponse chatClientResponse, AdvisorChain advisorChain) {
        return chatClientResponse;
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
