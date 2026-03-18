# ShardingSphere 分库分表配置与问题解决指南

## 一、项目概述

本项目演示了使用 Apache ShardingSphere 进行分库分表的完整配置，重点解决了数据不均匀分布的问题。

## 二、环境配置

### 1. 数据库结构
```
sharding1 (ds_0)
├── course_1
└── course_2

sharding2 (ds_1)
├── course_1
└── course_2
```

### 2. 分片目标
将课程表（course）数据均匀分配到4个分片：
- ds_0.course_1
- ds_0.course_2  
- ds_1.course_1
- ds_1.course_2

## 三、问题背景

### 1. 原始问题
使用雪花算法生成的 `cid` 作为分片键时，数据无法均匀分配到4张表。

### 2. 根本原因
雪花ID结构（64位）：
```
0 | 41位时间戳 | 10位工作机器ID | 12位序列号
```

原始分片策略：
```yaml
# 分库：cid % 2
# 分表：(cid / 2) % 2 + 1
```

问题：序列号低位在短时间内变化有限，导致分片计算结果模式化。

## 四、解决方案

### 方案1：使用时间戳高位分片（推荐）

#### 配置代码
```yaml
# shardingsphere-config-dev.yaml

# 分片算法定义
shardingAlgorithms:
  # 数据库分片算法：使用时间戳高位取模
  database_inline:
    type: INLINE
    props:
      algorithm-expression: "ds_${(cid >> 22) % 2}"

  # 课程表分表算法：使用时间戳高位映射到4个分片
  course_inline:
    type: INLINE
    props:
      algorithm-expression: "course_${((cid >> 22) % 4 < 2 ? 1 : 2)}"

# 分片表配置
tables:
  course:
    # 真实数据节点：2个数据库 × 2张表 = 4个分片
    actualDataNodes: ds_${0..1}.course_${1..2}
    
    # 分库策略
    databaseStrategy:
      standard:
        shardingColumn: cid
        shardingAlgorithmName: database_inline
    
    # 分表策略
    tableStrategy:
      standard:
        shardingColumn: cid
        shardingAlgorithmName: course_inline
    
    # 分布式主键生成策略
    keyGenerateStrategy:
      column: cid
      keyGeneratorName: snowflake

# 雪花算法优化配置
keyGenerators:
  snowflake:
    type: SNOWFLAKE
    props:
      # 添加振动偏移，配合时间戳高位使用
      max-vibration-offset: 3
```

#### 原理说明
- `cid >> 22`：右移22位，取时间戳的高19位
- 跳过序列号（12位）和工作机器ID（10位）的低位
- 时间戳变化快，能确保均匀分布

#### 分片映射
```
cid >> 22 结果 → 分片映射
0,1 → ds_0.course_1
2,3 → ds_0.course_2
4,5 → ds_1.course_1
6,7 → ds_1.course_2
... 循环
```

### 方案2：使用业务字段分片（备选）

```yaml
# 使用userId作为分片键
shardingColumn: userId
algorithm-expression: "ds_${userId % 2}"
algorithm-expression: "course_${(userId % 2) + 1}"
```

**优点**：分布完全可控
**缺点**：需要业务字段支持

## 五、YAML配置注意事项

### 1. 引号使用
Groovy表达式包含特殊字符时需要双引号：
```yaml
# 错误：YAML解析器将 ? 当作映射键
algorithm-expression: course_${((cid >> 22) % 4 < 2 ? 1 : 2)}

# 正确：用双引号包裹
algorithm-expression: "course_${((cid >> 22) % 4 < 2 ? 1 : 2)}"
```

### 2. 支持的表达式语法
- 算术运算：`+`, `-`, `*`, `/`, `%`
- 位运算：`>>`, `<<`, `&`, `|`
- 比较运算：`<`, `>`, `<=`, `>=`, `==`, `!=`
- 三元运算符：`? :`
- 方法调用：`Math.abs()`, `hashCode()` 等

## 六、实体类配置

### Course.java
```java
package com.lg.demo1.entry;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Course {
    @TableId(type = IdType.ASSIGN_ID)  // 使用ShardingSphere生成的主键
    private Long cid;
    private String cname;
    private Long userId;
    private String cstatus;
}
```

**关键**：必须添加 `@TableId(type = IdType.ASSIGN_ID)` 注解，否则MyBatis Plus无法获取ShardingSphere生成的主键。

## 七、测试验证

### 1. 插入测试（JDBCTest.java）
```java
@Test
public void addCourse() {
    System.out.println("开始插入100条课程数据...");
    for (int i = 0; i < 100; i++) {
        Course c = new Course();
        c.setCname("java");
        c.setUserId(1001L);
        c.setCstatus("1");
        courseMapper.insert(c);
        
        if (c.getCid() != null) {
            long cid = c.getCid();
            // 计算分片路由
            long timestampPart = cid >> 22;
            int dbShard = (int)(timestampPart % 2);
            int tableShard = (int)((timestampPart % 4) < 2 ? 1 : 2);
            
            System.out.println("插入第" + (i+1) + "条: cid=" + cid + 
                             ", timestampPart=" + timestampPart + 
                             ", dbShard=" + dbShard + " (ds_" + dbShard + ")" +
                             ", tableShard=" + tableShard + " (course_" + tableShard + ")");
        }
    }
    System.out.println("插入完成");
}
```

### 2. 分布统计测试
```java
@Test
public void analyzeDistribution() {
    System.out.println("=== 课程表数据分布分析 ===");
    
    // 查询所有课程
    List<Course> allCourses = courseMapper.selectList(null);
    System.out.println("总记录数: " + allCourses.size());
    
    // 统计分片分布
    int ds0_course1 = 0, ds0_course2 = 0, ds1_course1 = 0, ds1_course2 = 0;
    
    for (Course course : allCourses) {
        long cid = course.getCid();
        // 分库策略：(cid >> 22) % 2
        int dbIndex = (int)((cid >> 22) % 2);
        // 分表策略：((cid >> 22) % 4) < 2 ? 1 : 2
        int tableIndex = (int)(((cid >> 22) % 4) < 2 ? 1 : 2);
        
        if (dbIndex == 0 && tableIndex == 1) ds0_course1++;
        else if (dbIndex == 0 && tableIndex == 2) ds0_course2++;
        else if (dbIndex == 1 && tableIndex == 1) ds1_course1++;
        else ds1_course2++;
    }
    
    System.out.println("ds_0.course_1 记录数: " + ds0_course1);
    System.out.println("ds_0.course_2 记录数: " + ds0_course2);
    System.out.println("ds_1.course_1 记录数: " + ds1_course1);
    System.out.println("ds_1.course_2 记录数: " + ds1_course2);
    
    int total = ds0_course1 + ds0_course2 + ds1_course1 + ds1_course2;
    System.out.println("\n分布比例:");
    System.out.printf("ds_0.course_1: %.1f%%\n", ds0_course1 * 100.0 / total);
    System.out.printf("ds_0.course_2: %.1f%%\n", ds0_course2 * 100.0 / total);
    System.out.printf("ds_1.course_1: %.1f%%\n", ds1_course1 * 100.0 / total);
    System.out.printf("ds_1.course_2: %.1f%%\n", ds1_course2 * 100.0 / total);
}
```

## 八、运行测试

### 1. 运行单个测试
```bash
# 插入测试
mvn test -Dtest=JDBCTest#addCourse

# 分布统计测试
mvn test -Dtest=JDBCTest#analyzeDistribution
```

### 2. 查看SQL日志
配置文件已开启SQL日志：
```yaml
props:
  sql-show: true
```

运行测试时可以看到实际执行的SQL语句和路由结果。

## 九、常见问题解决

### 1. 数据分布不均匀
**症状**：数据集中在部分分片
**解决**：使用时间戳高位分片方案

### 2. 主键未回填
**症状**：`Course.getCid()` 返回 `null`
**解决**：实体类添加 `@TableId(type = IdType.ASSIGN_ID)`

### 3. YAML解析错误
**症状**：`mapping values are not allowed here`
**解决**：包含特殊字符的表达式用双引号包裹

### 4. 分片路由失败
**症状**：`Please check your sharding conditions`
**解决**：确保分片键和主键生成策略一致

## 十、最佳实践

1. **分片键选择**：优先使用业务字段，其次使用时间戳高位
2. **测试验证**：必须验证数据分布均匀性
3. **监控告警**：定期检查各分片数据量
4. **配置备份**：保留历史配置，便于回滚
5. **版本管理**：记录ShardingSphere版本和配置变更

## 十一、扩展阅读

1. [ShardingSphere官方文档](https://shardingsphere.apache.org/document/current/)
2. [雪花算法原理](https://developer.twitter.com/en/docs/basics/twitter-ids)
3. [YAML语法规范](https://yaml.org/spec/)

---

**最后更新**：2026-03-18  
**版本**：1.0  
**作者**：ShardingSphere学习项目组