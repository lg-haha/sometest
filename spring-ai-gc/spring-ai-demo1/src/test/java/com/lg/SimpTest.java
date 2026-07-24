package com.lg;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.Scanner;

/** 基本使用
 * @PackageName: com.lg
 * @ClassName: SimpTest
 * @Description:
 * @author: lg
 * @data: 2026/7/23 16:27
 */
@SpringBootTest
public class SimpTest {

    /**
     * 非流式
     *
     * @param chatModel
     */
    @Test
    public void call(@Autowired DeepSeekChatModel chatModel) {
        String call = chatModel.call("你好，很高兴认识你！");
        System.out.println(call);
    }

    /**
     * 流式
     *
     * @param chatModel
     */
    @Test
    public void callOfStream(@Autowired DeepSeekChatModel chatModel) {
        Flux<String> stream = chatModel.stream("你好，很高兴认识你！");
        stream.toIterable().forEach(System.out::print);
    }

    /**
     * 非流式 chatclient
     * @param chatModel
     */
    @Test
    public void callOfChatClient(@Autowired DeepSeekChatModel chatModel) {
        String content = ChatClient.builder(chatModel)
                .build()
                .prompt()
                .user("你好，很高兴认识你！")
                .call()
                .content();
        System.out.println(content);
    }

    /**
     * 流式 chatclient
     * @param chatModel
     */
    @Test
    public void callOfChatClientOfStream(@Autowired DeepSeekChatModel chatModel) {
        Flux<String> stream = ChatClient.builder(chatModel)
                .build()
                .prompt()
                .user("你好，很高兴认识你！")
                .stream()
                .content();
        stream.toIterable().forEach(System.out::print);
    }

    /**
     * 温度 0-2
     * @param chatModel
     */
    @Test
    public void temperatureTest(@Autowired DeepSeekChatModel chatModel) {
        ChatClient build = ChatClient.builder(chatModel).build();
        String content = build.prompt()
                .user("写一个关于下班的诗")
                .options(ChatOptions.builder().temperature(0.1).build())
                .call()
                .content();
        System.out.println("--------------------------------------------------------------------------");
        System.out.println(content);
    }


    /**
     * 对话测试
     * @param chatModel
     */
    @Test
    public void chatTest(@Autowired DashScopeChatModel chatModel){
        ChatClient build = ChatClient.builder(chatModel).build();
        ChatOptions options = ChatOptions.builder().temperature(0.8).build();
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入对话内容（输入 'exit' 退出）：");
        while (true) {
            String input = scanner.nextLine();
            if ("exit".equalsIgnoreCase(input)) {
                System.out.println("对话已结束。");
                break;
            }
            if (input.isBlank()) {
                continue;
            }
            try {
                String content = build.prompt()
                        .user(input)
                        .options(options)
                        .call()
                        .content();
                System.out.println(content);
            }catch (Exception e){
                System.err.println("请求失败：" + e.getMessage());
            }
        }
    }
}
