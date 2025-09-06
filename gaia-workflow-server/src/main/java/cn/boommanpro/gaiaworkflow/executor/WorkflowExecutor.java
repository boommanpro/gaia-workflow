package cn.boommanpro.gaiaworkflow.executor;

import cn.boommanpro.gaiaworkflow.model.TaskInfo;

import java.util.Map;

/**
 * 工作流执行器接口
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/08/22 15:30
 */
public interface WorkflowExecutor {

    /**
     * 执行工作流
     *
     * @param schema 工作流Schema
     * @param inputs 输入参数
     * @return 执行结果
     */
    Map<String, Object> execute(String schema, Map<String, Object> inputs);

    /**
     * 处理工作流执行结果
     *
     * @param taskInfo 任务信息
     * @param schema 工作流Schema
     * @param inputs 输入参数
     * @param outputs 输出结果
     */
    void processExecutionResult(TaskInfo taskInfo, String schema, Map<String, Object> inputs, Map<String, Object> outputs);

    /**
     * 处理工作流执行异常
     *
     * @param taskInfo 任务信息
     * @param e 异常
     */
    void processExecutionException(TaskInfo taskInfo, Exception e);
}

