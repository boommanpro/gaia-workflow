package cn.boommanpro.gaiaworkflow.executor.impl;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaiaworkflow.executor.AsyncWorkflowExecutor;
import cn.boommanpro.gaiaworkflow.executor.WorkflowExecutor;
import cn.boommanpro.gaiaworkflow.input.TaskRunInput;
import cn.boommanpro.gaiaworkflow.model.ErrorMessage;
import cn.boommanpro.gaiaworkflow.model.Messages;
import cn.boommanpro.gaiaworkflow.model.NodeReport;
import cn.boommanpro.gaiaworkflow.model.NodeStatus;
import cn.boommanpro.gaiaworkflow.model.Snapshot;
import cn.boommanpro.gaiaworkflow.model.TaskInfo;
import cn.boommanpro.gaiaworkflow.repository.TaskRepository;
import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.HashMap;
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
public class ThreadPoolAsyncWorkflowExecutor implements AsyncWorkflowExecutor, ApplicationContextAware {

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
    private ApplicationContext applicationContext;

    @Autowired
    public ThreadPoolAsyncWorkflowExecutor(WorkflowExecutor workflowExecutor, TaskRepository taskRepository) {
        this.workflowExecutor = workflowExecutor;
        this.taskRepository = taskRepository;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
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

                // 直接执行工作流并获取结果，避免重复执行
                long startTime = System.currentTimeMillis();
                GaiaWorkflow workflow = new GaiaWorkflow(input.getSchema());
                Map<String, Object> outputs = workflow.run(input.getInputs());
                long costTime = System.currentTimeMillis() - startTime;

                // 处理节点执行报告
                processNodeReports(taskId, taskInfo, workflow);

                // 设置执行成功状态
                taskInfo.getWorkflowStatus().setStatus(NodeStatus.SUCCESS.getValue());
                taskInfo.getWorkflowStatus().setTerminated(true);

                // 设置输出结果
                taskInfo.setOutputs(outputs);

                // 记录测试调用日志
                recordTestCallLog(taskId, taskInfo, input, outputs, costTime, null);

                // 更新任务信息
                taskRepository.updateTask(taskId, taskInfo);
            } catch (Exception e) {
                logger.error("异步执行工作流失败", e);
                // 处理执行异常
                processExecutionException(taskId, taskInfo, e);
                taskRepository.updateTask(taskId, taskInfo);
            }
        });
    }

    /**
     * 处理节点执行报告
     *
     * @param taskId 任务ID
     * @param taskInfo 任务信息
     * @param workflow 工作流实例
     */
    private void processNodeReports(String taskId, TaskInfo taskInfo, GaiaWorkflow workflow) {
        try {
            // 直接使用 GaiaWorkflow 提供的报告格式
            Map<String, GaiaWorkflow.NodeReport> nodeReports = workflow.getNodeReports();
            Map<String, NodeReport> taskNodeReports = new HashMap<>();

            // 转换GaiaWorkflow.NodeReport为NodeReport
            nodeReports.forEach((nodeId, report) -> {
                NodeReport taskReport = new NodeReport(
                        report.getId(),
                        convertNodeStatus(report.getStatus()),
                        report.getStartTime(),
                        report.getEndTime(),
                        report.getTimeCost(),
                        convertSnapshots(report.getSnapshots())
                );
                
                taskNodeReports.put(nodeId, taskReport);
            });

            // 设置节点报告
            taskInfo.setNodeReports(taskNodeReports);
        } catch (Exception e) {
            logger.error("处理节点报告失败, taskId: {}", taskId, e);
        }
    }

    /**
     * 转换 snapshots 格式
     *
     * @param snapshots 原始 snapshots
     * @return 转换后的 snapshots
     */
    private ArrayList<Snapshot> convertSnapshots(java.util.List<Object> snapshots) {
        ArrayList<Snapshot> convertedSnapshots = new ArrayList<>();
        
        for (Object snapshotObj : snapshots) {
            if (snapshotObj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> snapshotMap = (Map<String, Object>) snapshotObj;
                
                Snapshot snapshot = new Snapshot();
                snapshot.setId((String) snapshotMap.get("id"));
                snapshot.setNodeID((String) snapshotMap.get("nodeID"));
                
                // 处理 inputs
                Object inputs = snapshotMap.get("inputs");
                if (inputs instanceof Map) {
                    snapshot.setInputs((Map<String, Object>) inputs);
                }
                
                // 处理 outputs
                Object outputs = snapshotMap.get("outputs");
                if (outputs instanceof Map) {
                    snapshot.setOutputs((Map<String, Object>) outputs);
                }
                
                // 处理 data
                snapshot.setData(snapshotMap.get("data"));
                
                // 处理 branch
                snapshot.setBranch((String) snapshotMap.get("branch"));
                
                // 处理 error
                snapshot.setError((String) snapshotMap.get("error"));
                
                convertedSnapshots.add(snapshot);
            }
        }
        
        return convertedSnapshots;
    }

    /**
     * 转换节点状态
     *
     * @param status 原始状态
     * @return 转换后的状态
     */
    private String convertNodeStatus(String status) {
        if (status == null) {
            return NodeStatus.PENDING.getValue();
        }

        switch (status.toUpperCase()) {
            case "READY":
            case "WAIT":
                return NodeStatus.PENDING.getValue();
            case "RUNNING":
                return NodeStatus.PROCESSING.getValue();
            case "FINISHED":
                return NodeStatus.SUCCEEDED.getValue();
            case "FAILED":
                return NodeStatus.FAILED.getValue();
            case "SKIPPED":
                return NodeStatus.CANCELED.getValue();
            default:
                return NodeStatus.PENDING.getValue();
        }
    }

    /**
     * 记录测试调用日志
     *
     * @param taskId 任务ID
     * @param taskInfo 任务信息
     * @param input 输入参数
     * @param outputs 输出结果
     * @param costTime 耗时
     * @param exception 异常信息（如果有的话）
     */
    private void recordTestCallLog(String taskId, TaskInfo taskInfo, TaskRunInput input, 
                                  Map<String, Object> outputs, long costTime, Exception exception) {
        try {
            String schema = input.getSchema();
            Map<String, Object> inputs = input.getInputs();
            
            // 解析工作流编码和版本ID（从schema中获取）
            String workflowCode = parseWorkflowCodeFromSchema(schema);
            Long versionId = parseVersionIdFromSchema(schema);
            String workflowContent = schema;

            // 准备日志字段
            String execParam = JSONUtil.toJsonStr(inputs);
            String execStatus = (exception == null) ? "SUCCESS" : "FAILED";
            String reports = "";
            // 注意：这里我们不重新执行工作流来获取报告，而是从taskInfo中获取
            if (taskInfo.getNodeReports() != null) {
                reports = JSONUtil.toJsonStr(taskInfo.getNodeReports());
            }
            String callResult = (outputs != null) ? JSONUtil.toJsonStr(outputs) : "";
            String errorMessage = (exception != null) ? exception.getMessage() : "";

            // 通过ApplicationContext获取TestCallLogRecorder并记录日志
            try {
                Object testCallLogRecorder = applicationContext.getBean("testCallLogRecorderImpl");
                if (testCallLogRecorder != null) {
                    testCallLogRecorder.getClass().getMethod("recordTestCallLog",
                            String.class, Long.class, String.class, Long.class,
                            String.class, String.class, String.class, String.class, String.class)
                        .invoke(testCallLogRecorder,
                                workflowCode, versionId, workflowContent,
                                costTime, execParam, execStatus,
                                reports, callResult, errorMessage);
                }
            } catch (Exception e) {
                // 如果获取或调用Bean失败，则仅记录到日志中
                logger.info("Test call log - TaskId: {}, WorkflowCode: {}, VersionId: {}, CostTime: {}ms, Status: {}, Error: {}",
                           taskId, workflowCode, versionId, costTime, execStatus, errorMessage);
            }

        } catch (Exception e) {
            logger.error("记录测试调用日志失败, taskId: {}", taskId, e);
        }
    }

    /**
     * 从Schema中解析工作流编码
     *
     * @param schema 工作流Schema
     * @return 工作流编码
     */
    private String parseWorkflowCodeFromSchema(String schema) {
        try {
            // 简单实现，实际可能需要从Schema中提取特定字段
            return "workflow_" + System.currentTimeMillis();
        } catch (Exception e) {
            logger.warn("解析工作流编码失败", e);
            return "unknown_workflow";
        }
    }

    /**
     * 从Schema中解析版本ID
     *
     * @param schema 工作流Schema
     * @return 版本ID
     */
    private Long parseVersionIdFromSchema(String schema) {
        try {
            // 简单实现，实际可能需要从Schema中提取特定字段
            return System.currentTimeMillis();
        } catch (Exception e) {
            logger.warn("解析版本ID失败", e);
            return -1L;
        }
    }

    /**
     * 处理执行异常
     *
     * @param taskId 任务ID
     * @param taskInfo 任务信息
     * @param e 异常
     */
    private void processExecutionException(String taskId, TaskInfo taskInfo, Exception e) {
        try {
            logger.error("工作流执行异常, taskId: {}", taskId, e);
            taskInfo.getWorkflowStatus().setStatus(NodeStatus.FAIL.getValue());
            taskInfo.getWorkflowStatus().setTerminated(true);
            
            // 记录错误信息
            Messages messages = taskInfo.getMessages();
            if (messages == null) {
                messages = new Messages();
                taskInfo.setMessages(messages);
            }
            messages.getError().add(new ErrorMessage("workflow", e.getMessage()));
            
            // 记录测试调用日志（包含错误信息）
            recordTestCallLog(taskId, taskInfo, new TaskRunInput() {{
                setSchema(taskInfo.getSchema());
                setInputs(taskInfo.getInputs());
            }}, null, 0L, e);
        } catch (Exception ex) {
            logger.error("处理执行异常失败, taskId: {}", taskId, ex);
        }
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