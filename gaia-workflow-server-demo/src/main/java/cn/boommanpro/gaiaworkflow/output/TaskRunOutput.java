package cn.boommanpro.gaiaworkflow.output;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 代码功能
 *
 * @author <a href="mailto:wangqimeng03@meituan.com">wangqimeg03</a>
 * @date 2025/08/22 13:39
 */
@Data
@NoArgsConstructor
public class TaskRunOutput {
    private String taskID;

    public TaskRunOutput(String taskID) {
        this.taskID = taskID;
    }
}
