package com.lg;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import net.bytebuddy.asm.Advice;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @PackageName: com.lg
 * @ClassName: DascopeTest
 * @Description:
 * @author: lg
 * @data: 2026/2/28 10:49
 */
@SpringBootTest
public class DashScopeTest {
    @Autowired
    private DashScopeChatModel chatModel;

    @Autowired
    private DashScopeImageModel imageModel;

    @Test
    public void testQwen() {
        String content = chatModel.call("你好你是谁");
        System.out.println(content);
    }

    /**
     * 文生图
     */
    @Test
    public void testImg() {
        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .model("wan2.2-t2i-plus").build();
        ImagePrompt prompt = new ImagePrompt("画一只小狗", options);
        ImageResponse imgResponse = imageModel.call(prompt);
        String url = imgResponse.getResult().getOutput().getUrl();
        System.out.println(url);
    }

}
