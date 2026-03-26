package com.lg.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 可用模型信息VO
 */
@Data
@AllArgsConstructor
public class ModelInfoVO {

    /** 模型编码 */
    private String code;

    /** 显示名称 */
    private String name;

    /** 描述 */
    private String description;
}
