package cn.boommanpro.gaiaworkflow.output;

import cn.boommanpro.gaiaworkflow.model.Messages;
import cn.boommanpro.gaiaworkflow.model.NodeReport;
import cn.boommanpro.gaiaworkflow.model.WorkflowStatus;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 任务报告输出
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/08/22 13:41
 */
@Data
public class TaskReportOutput {
    private WorkflowStatus workflowStatus;
    private Map<String, Object> inputs;
    private Map<String, Object> outputs;
    private Messages messages;
    private Map<String, NodeReport> reports;

    public TaskReportOutput() {
        this.workflowStatus = new WorkflowStatus("idle", false);
        this.inputs = new HashMap<>();
        this.outputs = new HashMap<>();
        this.messages = new Messages();
        this.reports = new HashMap<>();
    }

}
