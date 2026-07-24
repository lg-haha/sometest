package com.lg;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import java.util.Scanner;

/**
 * @PackageName: com.lg
 * @ClassName: ApplicationStart
 * @Description:
 * @author: lg
 * @data: 2026/1/30 09:57
 */
@SpringBootApplication
public class ApplicationStart {
    public static void main(String[] args){
        SpringApplication.run(ApplicationStart.class,args);
    }

//    @Bean
//    public CommandLineRunner chatRunner(DashScopeChatModel chatModel, ChatMemory chatMemory) {
//        return args -> {
//            ChatClient chatClient = ChatClient.builder(chatModel)
//                    .defaultAdvisors(
//                            PromptChatMemoryAdvisor.builder(chatMemory).build()
//                    )
//                    .build();
//            ChatOptions options = ChatOptions.builder().temperature(0.8).build();
//            Scanner scanner = new Scanner(System.in);
//            String conversationId = "interactive-" + System.currentTimeMillis();
//            System.out.println("请输入对话内容（输入 'exit' 退出）：");
//            while (true) {
//                String input = scanner.nextLine();
//                if ("exit".equalsIgnoreCase(input)) {
//                    System.out.println("对话已结束。");
//                    break;
//                }
//                if (input.isBlank()) {
//                    continue;
//                }
//                try {
//                    String content = chatClient.prompt()
//                            .system("""
//                                    你是一个经验丰富的中国导游，只回答关于旅游相关的问题
//                                    """)
//                            .user(input)
//                            .options(options)
//                            .advisors(advisorSpec -> advisorSpec
//                                    .param(ChatMemory.CONVERSATION_ID,conversationId))
//                            .call()
//                            .content();
//                    System.out.println(content);
//                } catch (Exception e) {
//                    System.err.println("请求失败：" + e.getMessage());
//                }
//            }
//        };
//    }
}
