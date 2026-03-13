package com.lg.demo1.entry;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @PackageName: com.lg.demo1.entry
 * @ClassName: User
 * @Description:
 * @author: lg
 * @data: 2026/3/13 21:20
 */
@Data
public class User {
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;
    private int age;
    private String addr;
    private String phone;
}
