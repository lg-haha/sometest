# Spring Ai

## 基本使用

### 1.引入依赖

目前(2026年1月30日)的最新版本是1.1.2，spring ai 支持 spring Boot 3.4.x 和 3.5.x.

~~~xml

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-bom</artifactId>
            <version>1.1.2</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
~~~

### 2.引入需要使用的模型 如 deepseek

~~~xml

<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-deepseek</artifactId>
</dependency>
~~~

### 3、配置相关参数

~~~yaml
spring:
  ai:
    deepseek:
      api-key: ${DEEP_SEEK_KEY}
      chat:
        options:
          model: deepseek-chat
~~~

### 4、基本使用

~~~java

@Test
public void testChat(@Autowired
                     DeepSeekChatModel chatModel) {
    String call = chatModel.call("你好");
    System.out.println(call);
}
~~~

#### options 配置项

* temperature（温度）

~~~text
0-2  浮点数值 
控制生成文本的随机性。值越高，生成的文本越随机，值越低，生成的文本越确定。默认值是0.9。
实战用法一般建议选 0.5~0.8 作为日常生产起点，需要根据业务不断测试调整。
~~~

![img.png](img.png)

#### maxTokens（最大token数）

~~~text
默认低 token
maxTokens：限制AI模型生成的最大token数（近似理解为字数上限）。
● 需要简洁回复、打分、列表、短摘要等，建议小值（如10~50）。
● 防止用户跑长对话导致无关内容或花费过多token费用。
● 如果遇到生成内容经常被截断，可以适当配置更大maxTokens。
~~~

#### stop （停止条件）

截断不想生成的内容 如下

~~~yaml
server:
  port: 8666
spring:
  application:
    name: spring-ai-test
  ai:
    deepseek:
      api-key: ${DEEP_SEEK_KEY}
      chat:
        options:
          model: deepseek-chat
          max-tokens: 1024 # 生成文本的最大长度
          stop:
            - "\n" # 生成文本的结束标志 只想一行
            - "。" # 生成文本的结束标志 只想一句话
            - "政治" #敏感词
            - "最后最总结一下"  #这种AI惯用的模板词， 减少AI词汇， 让文章更拟人
~~~

#### 模型推理

设置深度思考，思考的内容有个专业名词: Chain of Thought (CoT)
在deepseek中， deepseek-reasoner模型是深度思考模型
可以在代码中设置

~~~java
 /**
 * 推理模型(非流式)
 */
@Test
public void deepSeekReasonerExample() {
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
public void deepSeekReasonerStreamExample() {
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
~~~

也可以在配置文件中直接配置

~~~text
spring.ai.deepseek.chat.options.model= deepseek-reasoner
~~~