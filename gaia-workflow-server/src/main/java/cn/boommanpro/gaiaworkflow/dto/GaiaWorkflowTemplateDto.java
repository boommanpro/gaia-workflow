package cn.boommanpro.gaiaworkflow.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GaiaWorkflowTemplateDto {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 模板编码
     */
    private String templateCode;

    /**
     * 模板名称
     */
    private String templateName;

    /**
     * 模板描述
     */
    private String templateDesc;

    /**
     * 模板数据（JSON格式）
     */
    private String templateData;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}