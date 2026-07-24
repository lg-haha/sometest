package com.lg.demo.tools;

import com.lg.demo.entity.User;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @PackageName: com.lg.demo.tools
 * @ClassName: SomeTools
 * @Description:
 * @author: lg
 * @data: 2026/7/24 11:04
 */
@Component
public class UserTools {

    //假设这里获取了用户数据
    @Tool(description = "查询用户数据")
    public List<User> getSomeList() {
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setName("lg");
        user.setAge(18);
        user.setSex("男");
        user.setAddress("长沙");
        users.add(user);
        User user2 = new User();
        user2.setName("lglglg");
        user2.setAge(19);
        user2.setSex("女");
        user2.setAddress("上海");
        users.add(user2);
        User user3 = new User();
        user3.setName("lglg");
        user3.setAge(20);
        user3.setSex("男");
        user3.setAddress("北京");
        users.add(user3);
        return users;
    }

    @Tool(description = "添加用户")
    public User addUser(User user) {
        return user;
    }

}
