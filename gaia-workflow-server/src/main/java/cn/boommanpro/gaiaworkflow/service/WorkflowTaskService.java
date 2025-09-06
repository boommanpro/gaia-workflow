package cn.boommanpro.gaiaworkflow.service;

import cn.boommanpro.gaiaworkflow.input.TaskCancelInput;
import cn.boommanpro.gaiaworkflow.input.TaskRunInput;
import cn.boommanpro.gaiaworkflow.output.TaskCancelOutput;
import cn.boommanpro.gaiaworkflow.output.TaskReportOutput;
import cn.boommanpro.gaiaworkflow.output.TaskResultOutput;
import cn.boommanpro.gaiaworkflow.output.TaskRunOutput;
import cn.boommanpro.gaiaworkflow.output.TaskValidateOutput;

/**
 * 工作流任务服务接口
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/08/22 14:30
 */
public interface WorkflowTaskService {

    /**
     * 验证工作流
     *
     * @param input 工作流输入参数
     * @return 验证结果
     */
    TaskValidateOutput validateWorkflow(TaskRunInput input);

    /**
     * 运行工作流
     *
     * @param input 工作流输入参数
     * @return 运行结果
     */
    TaskRunOutput runWorkflow(TaskRunInput input);

    /**
     * 获取任务报告
     *
     * @param taskId 任务ID
     * @return 任务报告
     */
    TaskReportOutput getTaskReport(String taskId);

    /**
     * 取消任务
     *
     * @param input 取消任务输入参数
     * @return 取消结果
     */
    TaskCancelOutput cancelTask(TaskCancelInput input);

    /**
     * 获取任务结果
     *
     * @param taskId 任务ID
     * @return 任务结果
     */
    TaskResultOutput getTaskResult(String taskId);
}

