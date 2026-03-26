package com.lg.demo.controller;

import com.lg.demo.dto.ChatRequestDTO;
import com.lg.demo.dto.ModelInfoVO;
import com.lg.demo.service.ChatService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

/**
 * AI聊天控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    private static final List<ModelInfoVO> MODELS = Arrays.asList(
            new ModelInfoVO("deepseek", "DeepSeek", "DeepSeek大模型（默认）"),
            new ModelInfoVO("dashscope", "通义千问", "阿里云DashScope通义千问"),
            new ModelInfoVO("ollama", "Ollama", "本地Ollama模型(gemma3:4b)")
    );

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * 流式聊天 - SSE
     */
    @PostMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamChat(@Valid @RequestBody ChatRequestDTO request) {
        return chatService.streamChat(request);
    }

    /**
     * 同步聊天
     */
    @PostMapping("/send")
    public String chat(@Valid @RequestBody ChatRequestDTO request) {
        return chatService.chat(request);
    }

    /**
     * 获取可用模型列表
     */
    @GetMapping("/models")
    public List<ModelInfoVO> getModels() {
        return MODELS;
    }
}
