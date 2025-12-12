package cn.boommanpro.gaiaworkflow.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GaiaWorkflowVersionDto {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 工作流编码
     */
    private String workflowCode;

    /**
     * 版本号
     */
    private String versionNumber;

    /**
     * 版本描述
     */
    private String versionDesc;

    /**
     * 工作流数据（JSON格式）
     */
    private String workflowData;

    /**
     * 创建人
     */
    private String createdBy;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 是否为当前版本（0-否，1-是）
     */
    private Integer isCurrent;
}