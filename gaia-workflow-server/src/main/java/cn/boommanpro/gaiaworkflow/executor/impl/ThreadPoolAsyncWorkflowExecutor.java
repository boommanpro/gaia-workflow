package cn.boommanpro.gaiaworkflow.executor.impl;

import cn.boommanpro.gaiaworkflow.executor.AsyncWorkflowExecutor;
import cn.boommanpro.gaiaworkflow.executor.WorkflowExecutor;
import cn.boommanpro.gaiaworkflow.input.TaskRunInput;
import cn.boommanpro.gaiaworkflow.model.NodeStatus;
import cn.boommanpro.gaiaworkflow.model.TaskInfo;
import cn.boommanpro.gaiaworkflow.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * 基于线程池的异步工作流执行器实现
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/08/22 16:15
 */
@Component
public class ThreadPoolAsyncWorkflowExecutor implements AsyncWorkflowExecutor {

    private static final Logger logger = LoggerFactory.getLogger(ThreadPoolAsyncWorkflowExecutor.class);

    /**
     * 线程池大小
     */
    private static final int THREAD_POOL_SIZE = 10;

    /**
     * 线程池
     */
    private ExecutorService executorService;

    private final WorkflowExecutor workflowExecutor;
    private final TaskRepository taskRepository;

    @Autowired
    public ThreadPoolAsyncWorkflowExecutor(WorkflowExecutor workflowExecutor, TaskRepository taskRepository) {
        this.workflowExecutor = workflowExecutor;
        this.taskRepository = taskRepository;
    }

    @PostConstruct
    public void init() {
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        logger.info("异步工作流执行器线程池已初始化，大小：{}", THREAD_POOL_SIZE);
    }

    @PreDestroy
    public void destroy() {
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
            logger.info("异步工作流执行器线程池已关闭");
        }
    }

    @Override
    public void executeAsync(String taskId, TaskInfo taskInfo, TaskRunInput input) {
        executorService.submit(() -> {
            try {
                // 更新工作流状态为处理中
                updateTaskStatus(taskId, taskInfo, NodeStatus.PROCESSING);

                // 使用工作流执行器执行工作流
                Map<String, Object> outputs = workflowExecutor.execute(input.getSchema(), input.getInputs());

                // 处理执行结果
                workflowExecutor.processExecutionResult(taskInfo, input.getSchema(), input.getInputs(), outputs);

                // 更新任务信息
                taskRepository.updateTask(taskId, taskInfo);
            } catch (Exception e) {
                logger.error("异步执行工作流失败", e);
                // 处理执行异常
                workflowExecutor.processExecutionException(taskInfo, e);
                taskRepository.updateTask(taskId, taskInfo);
            }
        });
    }

    /**
     * 更新任务状态
     *
     * @param taskId 任务ID
     * @param taskInfo 任务信息
     * @param status 状态
     */
    private void updateTaskStatus(String taskId, TaskInfo taskInfo, NodeStatus status) {
        taskInfo.getWorkflowStatus().setStatus(status.getValue());
        taskRepository.updateTask(taskId, taskInfo);
    }
}

