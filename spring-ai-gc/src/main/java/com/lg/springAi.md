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
*  temperature（温度）
~~~text
0-2  浮点数值 
控制生成文本的随机性。值越高，生成的文本越随机，值越低，生成的文本越确定。默认值是0.9。
实战用法一般建议选 0.5~0.8 作为日常生产起点，需要根据业务不断测试调整。
~~~
![img.png](img.png)

