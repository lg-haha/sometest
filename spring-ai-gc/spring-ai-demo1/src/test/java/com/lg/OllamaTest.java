package com.lg;

import org.junit.jupiter.api.Test;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

/**
 * @PackageName: com.lg
 * @ClassName: OllamaTest
 * @Description:
 * @author: lg
 * @data: 2026/2/28 12:00
 */
@SpringBootTest
public class OllamaTest {
    @Test
    public void testChat(@Autowired OllamaChatModel ollamaChatModel) {
        String text = ollamaChatModel.call("你是谁");
        System.out.println(text);
    }

    /**
     * 流式输出
     *
     * @param chatModel
     */
    @Test
    public void testStream(@Autowired OllamaChatModel chatModel) {
        Flux<String> stream = chatModel.stream("你是谁/no_think");
        // 阻塞输出
        stream.toIterable().forEach(System.out::print);
    }
}
