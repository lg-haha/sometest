package com.lg;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lg.demo1.entry.Course;
import com.lg.demo1.mapper.CourseMapper;
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
 * @data: 2026/2/26 16:42
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class JDBCTest {
    @Resource
    private CourseMapper courseMapper;

    @Test
    public void addCourse() {
        for (int i = 0; i < 100; i++) {
            Course c = new Course();
            c.setCname("java");
            c.setUserId(1001L);
            c.setCstatus("1");
            courseMapper.insert(c);
            System.out.printf(c.toString());
        }
    }

    @Test
    public void queryCourse() {
        QueryWrapper<Course> wrapper = new QueryWrapper<>();
        wrapper.eq("cid", "2026945990104883201");
        List<Course> courses = courseMapper.selectList(wrapper);
        courses.forEach(course -> System.out.println(course));
    }
}
