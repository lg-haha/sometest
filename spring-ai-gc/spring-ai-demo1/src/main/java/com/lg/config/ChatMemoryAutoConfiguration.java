package com.lg.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @PackageName: com.lg.config
 * @ClassName: ChatMemoryAutoConfiguration
 * @Description:
 * @author: lg
 * @data: 2026/2/28 16:24
 */
@AutoConfiguration
@ConditionalOnClass({ChatMemory.class, ChatMemoryRepository.class})
public class ChatMemoryAutoConfiguration {
//    @Bean
//    @ConditionalOnMissingBean
//    ChatMemoryRepository chatMemoryRepository() {
//        return new InMemoryChatMemoryRepository();
//    }
//
//    @Bean
//    @ConditionalOnMissingBean
//    ChatMemory chatMemory(ChatMemoryRepository chatMemoryRepository) {
//        return MessageWindowChatMemory
//                .builder()
//                .maxMessages(10)
//                .chatMemoryRepository(chatMemoryRepository)
//                .build();
//    }

    @Bean
    ChatMemory chatMemory(JdbcChatMemoryRepository chatMemoryRepository) {
        return MessageWindowChatMemory
                .builder()
                .maxMessages(1)
                .chatMemoryRepository(chatMemoryRepository).build();
    }
}
