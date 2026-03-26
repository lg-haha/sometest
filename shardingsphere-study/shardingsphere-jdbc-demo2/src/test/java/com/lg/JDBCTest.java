package com.lg;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lg.demo1.entry.Course;
import com.lg.demo1.entry.User;
import com.lg.demo1.mapper.CourseMapper;
import com.lg.demo1.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @PackageName: com.lg
 * @ClassName: JDBCTest
 * @Description:
 * @author: lg
 * @data: 2026/3/8 20:45
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class JDBCTest {
    @Resource
    private CourseMapper courseMapper;

    @Resource
    private UserMapper userMapper;

    @Test
    public void addCourse() {
        System.out.println("开始插入100条课程数据...");
        for (int i = 0; i < 100; i++) {
            Course c = new Course();
            c.setCname("java");
            c.setUserId(1001L);
            c.setCstatus("1");
            int result = courseMapper.insert(c);
            
            if (c.getCid() != null) {
                long cid = c.getCid();
                // 计算分片路由
                long timestampPart = cid >> 22; // 取时间戳部分
                int dbShard = (int)(timestampPart % 2); // 数据库分片
                int tableShard = (int)((timestampPart % 4) < 2 ? 1 : 2); // 表分片
                
                System.out.println("插入第" + (i+1) + "条: cid=" + cid + 
                                 ", timestampPart=" + timestampPart + 
                                 ", dbShard=" + dbShard + " (ds_" + dbShard + ")" +
                                 ", tableShard=" + tableShard + " (course_" + tableShard + ")");
            } else {
                System.out.println("插入第" + (i+1) + "条失败: cid为null");
            }
        }
        System.out.println("插入完成");
    }

    @Test
    public void addUser() {
        for (int i = 0; i < 10; i++) {
            User u = new User();
            u.setAddr("上海");
            u.setAge(18);
            u.setPhone("18579845631");
            userMapper.insert(u);
        }
    }

    @Test
    public void queryUser() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("id", "2032483989562302466");
        List<User> users = userMapper.selectList(wrapper);
        users.forEach(user -> System.out.println(user));
    }

    @Test
    public void queryCourse() {
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.eq("cid", "2026945990104883201");
        List<Course> courses = courseMapper.selectList(wrapper);
        courses.forEach(course -> System.out.println(course));
    }

    @Test
    public void analyzeDistribution() {
        // 统计4张表的数据分布
        System.out.println("=== 课程表数据分布分析 ===");
        
        // 查询所有课程
        List<Course> allCourses = courseMapper.selectList(null);
        System.out.println("总记录数: " + allCourses.size());
        
        // 统计分片分布
        int ds0_course1 = 0, ds0_course2 = 0, ds1_course1 = 0, ds1_course2 = 0;
        
        for (Course course : allCourses) {
            long cid = course.getCid();
            // 分库策略：cid % 2
            int dbIndex = (int)(cid % 2); // 0: ds_0, 1: ds_1
            // 分表策略：(cid / 2 % 2)
            int tableIndex = (int)((cid / 2) % 2); // 0: course_1, 1: course_2
            
            if (dbIndex == 0 && tableIndex == 0) ds0_course1++;
            else if (dbIndex == 0 && tableIndex == 1) ds0_course2++;
            else if (dbIndex == 1 && tableIndex == 0) ds1_course1++;
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
}
