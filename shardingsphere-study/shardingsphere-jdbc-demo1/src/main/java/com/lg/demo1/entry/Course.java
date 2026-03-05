package com.lg.demo1.entry;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @PackageName: com.lg.demo1.entry
 * @ClassName: Course
 * @Description:
 * @author: lg
 * @data: 2026/2/26 16:35
 */
@Data
public class Course {
    @TableId
    private Long cid;
    private String cname;
    private Long userId;
    private String cstatus;
}
