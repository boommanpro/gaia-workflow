package cn.boommanpro.gaiaworkflow.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GaiaWorkflowDto {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 工作流编码，全局唯一
     */
    private String workflowCode;

    /**
     * 工作流名称
     */
    private String workflowName;

    /**
     * 工作流描述
     */
    private String workflowDesc;

    /**
     * 当前版本ID
     */
    private Long currentVersionId;

    /**
     * 来源模板编码
     */
    private String templateCode;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}