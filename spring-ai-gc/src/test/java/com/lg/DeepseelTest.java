package com.lg;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

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

    @Test
    public void testLineBreakStop(@Autowired
                                  DeepSeekChatModel chatModel) {
        System.out.println("=== 测试换行符停止 ===");
        String response = chatModel.call("请介绍一下人工智能");
        System.out.println("响应: " + response);
        System.out.println("是否包含换行: " + response.contains("\n"));
    }

    @Test
    public void testPeriodStop(@Autowired
                               DeepSeekChatModel chatModel) {
        System.out.println("=== 测试句号停止 ===");
        String response = chatModel.call("今天天气如何");
        System.out.println("响应: " + response);
        System.out.println("句号数量: " + response.chars().filter(ch -> ch == '。').count());
    }


    @Test
    public void testSummaryStop(@Autowired
                                DeepSeekChatModel chatModel) {
        System.out.println("=== 测试总结短语停止 ===");
        String response = chatModel.call("解释机器学习概念，最后总结一下");
        System.out.println("响应: " + response);
        System.out.println("是否包含'最后': " + response.contains("最后"));
        System.out.println("是否包含'总结': " + response.contains("总结"));
    }

    @Test
    public void testWithoutStopConditions(@Autowired
                                          DeepSeekChatModel chatModel) {
        System.out.println("=== 无停止条件测试 ===");
        // 创建没有停止条件的配置
        ChatOptions options = ChatOptions.builder()
                .maxTokens(2048) // 增加token限制
                .temperature(0.7)
                .build();

        ChatResponse response = chatModel.call(new Prompt("请详细介绍人工智能的发展历程", options));
        String text = response.getResult().getOutput().getText();
        System.out.println("长文本响应: " + text);
        System.out.println("响应长度: " + text.length() + " 字符");
    }

    /**
     * options 配置项
     * temperature: 控制生成文本的随机性，值越大生成的文本越随机，值越小生成的文本越确定。
     *
     * @param chatModel
     */
    @Test
    public void testChatOptions(@Autowired
                                DeepSeekChatModel chatModel) {
        ChatOptions options = ChatOptions.builder().temperature(2.0).build();
        ChatResponse res = chatModel.call(new Prompt("请写一句诗描述清晨", options));
        System.out.println(res.getResult().getOutput().getText());
    }

    /**
     * 推理模型(非流式)
     */
    @Test
    public void deepSeekReasonerExample(@Autowired
                                        DeepSeekChatModel chatModel) {
        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                .model("deepseek-reasoner").build();
        Prompt prompt = new Prompt("请写一句诗描写清晨", options);
        ChatResponse call = chatModel.call(prompt);
        DeepSeekAssistantMessage assistantMessage = (DeepSeekAssistantMessage) call.getResult().getOutput();
        String reasoningContent = assistantMessage.getReasoningContent();
        String content = assistantMessage.getText();
        System.out.println("推理内容: " + reasoningContent);
        System.out.println("====================================================================");
        System.out.println("输出内容: " + content);
    }

    /**
     * 推理模型(流式)
     */
    @Test
    public void deepSeekReasonerStreamExample(@Autowired
                                              DeepSeekChatModel chatModel) {
        DeepSeekChatOptions options = DeepSeekChatOptions.builder()
                .model("deepseek-reasoner").build();
        Prompt prompt = new Prompt("请写一句诗描写清晨", options);
        Flux<ChatResponse> stream = chatModel.stream(prompt);
        stream.toIterable().forEach(response -> {
            DeepSeekAssistantMessage assistantMessage = (DeepSeekAssistantMessage) response.getResult().getOutput();
            String reasoningContent = assistantMessage.getReasoningContent();
            System.out.print(reasoningContent);
        });
        System.out.println("--------------------------------------------");
        stream.toIterable().forEach(response -> {
            DeepSeekAssistantMessage assistantMessage = (DeepSeekAssistantMessage) response.getResult().getOutput();
            String content = assistantMessage.getText();
            System.out.print(content);
        });
    }
}

