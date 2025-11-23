package cn.boommanpro.gaia.workflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 工作流定义
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/11/10
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowDefinition {

    /**
     * 工作流ID
     */
    private String id;

    /**
     * 工作流名称
     */
    private String name;

    /**
     * 工作流描述
     */
    private String description;

    /**
     * 工作流定义内容（JSON格式）
     */
    private String definition;

    /**
     * 输入参数schema
     */
    private String inputs;

    /**
     * 输出参数schema
     */
    private String outputs;

    /**
     * 标签
     */
    private String tags;

    /**
     * 是否为示例工作流
     */
    private Boolean isExample;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 版本
     */
    private String version = "1.0.0";
}

