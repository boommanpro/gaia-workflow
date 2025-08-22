package cn.boommanpro.gaiaworkflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 工作流状态
 *
 * @author <a href="mailto:wangqimeng03@meituan.com">wangqimeg03</a>
 * @date 2025/08/22 13:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowStatus {
    private String status;
    private boolean terminated;
}

