package cn.boommanpro.gaiaworkflow.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GaiaWorkflowLogDto {

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
     * 执行ID，全局唯一
     */
    private String executionId;

    /**
     * 开始执行时间
     */
    private LocalDateTime startTime;

    /**
     * 执行结束时间
     */
    private LocalDateTime endTime;

    /**
     * 执行状态（success-成功，failed-失败，running-执行中）
     */
    private String status;

    /**
     * 输入参数（JSON格式）
     */
    private String inputParams;

    /**
     * 输出参数（JSON格式）
     */
    private String outputParams;

    /**
     * 错误信息
     */
    private String errorMessage;

    /**
     * 执行时长（毫秒）
     */
    private Long executionDuration;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}