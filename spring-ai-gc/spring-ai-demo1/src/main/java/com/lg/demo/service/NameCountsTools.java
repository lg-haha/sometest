package com.lg.demo.service;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

/**
 * @PackageName: com.lg.demo.service
 * @ClassName: NameCountsTools
 * @Description:
 * @author: lg
 * @data: 2026/3/2 16:24
 */
@Service
public class NameCountsTools {

    @Tool(description="长沙有多少名字的数量")
    public String LocationNameCounts(@ToolParam(description="名字") String name){
        return "10";
    }
}
