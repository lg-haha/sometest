# ShardingSphere 学习笔记

## 一、ShardingSphere 简介

ShardingSphere 是一套开源的分布式数据库中间件解决方案组成的生态圈，它由 JDBC、Proxy 和 Sidecar（规划中）这 3 款相互独立，
却又能够混合部署使用的多款产品组成。它们均提供标准化的数据分片、分布式事务和数据库治理功能，可适用于如 Java
同构、异构语言、云原生等各种多样化的应用场景。

额~这是个ai自动生成的解释，对于我来说，ShardingSphere只是一个分库分表中间件

## 使用

**当前测试使用ShardingSphere5.5.2 和SpringBoot3.5.11 SpringBoot2.x的可能要使用较老的版本**

**在使用ShardingSphere前必须吐槽 ShardingSphere与SpringBoot的兼容以及ShardingSphere官方文档简直操蛋~~**

### 引用依赖

~~~xml

<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <spring-boot.version>3.5.11</spring-boot.version>
</properties>

<dependencyManagement>
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-dependencies</artifactId>
        <version>${spring-boot.version}</version>
        <type>pom</type>
        <scope>import</scope>
    </dependency>
</dependencies>
</dependencyManagement>

<dependencies>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.apache.shardingsphere</groupId>
    <artifactId>shardingsphere-jdbc</artifactId>
    <version>5.5.2</version>
</dependency>
<dependency>
    <groupId>org.yaml</groupId>
    <artifactId>snakeyaml</artifactId>
    <version>2.2</version>
</dependency>
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>druid</artifactId>
    <version>1.2.24</version>
</dependency>
<!-- mysql连接驱动 -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <version>8.0.33</version>
</dependency>
<!-- mybatisplus依赖 -->
<dependency>
    <groupId>com.baomidou</groupId>
    <artifactId>mybatis-plus-spring-boot3-starter</artifactId>
    <version>3.5.15</version>
</dependency>
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13.2</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>
</dependencies>
<build>
<resources>
    <resource>
        <directory>src/main/resources</directory>
        <includes>
            <include>**/*.properties</include>
            <include>**/*.xml</include>
            <include>**/*.yaml</include> <!-- 确保包含 YAML 文件 -->
            <include>**/*.yml</include>
        </includes>
    </resource>
</resources>
<plugins>
    <plugin>
        <groupId>org.apache.maven.plugins</groupId>
        <artifactId>maven-compiler-plugin</artifactId>
        <version>3.11.0</version>
        <configuration>
            <source>17</source>
            <target>17</target>
        </configuration>
    </plugin>
</plugins>
</build>
~~~

### 特殊配置

#### 配置文件加载

根据官网的配置 需要在 application.properties 中配置

~~~properties
# 配置 DataSource Driver
spring.datasource.driver-class-name=org.apache.shardingsphere.driver.ShardingSphereDriver
# 指定 YAML 配置文件
spring.datasource.url=jdbc:shardingsphere:classpath:xxx.yaml
~~~

![img.png](img.png)
如果你跟着这个来，那就恭喜了，一用一个不吱声

经过伟大的CSDN大佬提示，通过配置类来配置shardingsphere 配置类的位置

~~~ java
package com.lg.config;

import org.apache.shardingsphere.driver.api.yaml.YamlShardingSphereDataSourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;

/**
 * @PackageName: com.lg.config
 * @ClassName: ShardingConfig
 * @Description: dev
 * @author: lg
 * @data: 2026/3/10 16:20
 */
@Configuration
public class ShardingConfig {
    @Value("${spring.profiles.active}")
    private String active;

    @Bean
    public DataSource initShardingSphereDataSource() throws SQLException, IOException {
        Resource resource = new ClassPathResource("shardingsphere-config-" + active + ".yaml");
        // 检查资源是否存在
        if (!resource.exists()) {
            throw new IOException("配置文件不存在: shardingsphere-config-" + active + ".yaml");
        }
        // 通过 InputStream 读取配置,**如果通过file读取打包后会报错**
        try (InputStream inputStream = resource.getInputStream()) {
            // 使用 ShardingSphere 的 YAML 工厂创建数据源
            return YamlShardingSphereDataSourceFactory.createDataSource(inputStream.readAllBytes());
        }
    }
}
~~~

配置文件放到 resource下面 ，为了确保会加载这个配置文件，在pom中添加下面内容

~~~ xml
<resources>
    <resource>
        <directory>src/main/resources</directory>
        <includes>
            <include>**/*.properties</include>
            <include>**/*.xml</include>
            <include>**/*.yaml</include> <!-- 确保包含 YAML 文件 -->
            <include>**/*.yml</include>
        </includes>
    </resource>
</resources>
~~~

#### 配置文件内容

