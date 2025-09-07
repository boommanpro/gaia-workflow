package cn.boommanpro.gaiaworkflow.executor.impl;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.log.ChainNodeExecuteInfo;
import cn.boommanpro.gaia.workflow.model.Chain;
import cn.boommanpro.gaiaworkflow.executor.WorkflowExecutor;
import cn.boommanpro.gaiaworkflow.model.ErrorMessage;
import cn.boommanpro.gaiaworkflow.model.Messages;
import cn.boommanpro.gaiaworkflow.model.NodeReport;
import cn.boommanpro.gaiaworkflow.model.NodeStatus;
import cn.boommanpro.gaiaworkflow.model.Snapshot;
import cn.boommanpro.gaiaworkflow.model.TaskInfo;
import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Gaia工作流执行器实现
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/08/22 15:35
 */
@Component
public class GaiaWorkflowExecutor implements WorkflowExecutor, ApplicationContextAware {

    private static final Logger logger = LoggerFactory.getLogger(GaiaWorkflowExecutor.class);

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public Map<String, Object> execute(String schema, Map<String, Object> inputs) {
        long startTime = System.currentTimeMillis();
        GaiaWorkflow workflow = null;
        try {
            // 使用GaiaWorkflow执行工作流
            workflow = createWorkflow(schema);
            Map<String, Object> result = workflow.run(inputs);

            // 记录测试调用日志
            recordTestCallLog(schema, inputs, result, startTime, null, workflow);

            return result;
        } catch (Exception e) {
            logger.error("执行工作流失败", e);
            // 记录测试调用日志（包含错误信息）
            recordTestCallLog(schema, inputs, null, startTime, e, workflow);
            throw e;
        }
    }

    @Override
    public void processExecutionResult(TaskInfo taskInfo, String schema, Map<String, Object> inputs, Map<String, Object> outputs) {
        GaiaWorkflow workflow = null;
        try {
            // 使用GaiaWorkflow获取执行报告
            workflow = createWorkflow(schema);
            // 执行工作流以获取报告
            workflow.run(inputs);

            // 处理节点执行报告
            processNodeReports(taskInfo, workflow);

            // 设置执行成功状态
            updateTaskStatusToSuccess(taskInfo);

            // 设置输出结果
            taskInfo.setOutputs(outputs);
        } catch (Exception e) {
            logger.error("处理工作流执行结果失败", e);
            // 处理异常
            processExecutionException(taskInfo, e);
        }
    }

    @Override
    public void processExecutionException(TaskInfo taskInfo, Exception e) {
        logger.error("工作流执行异常", e);
        updateTaskStatusToFailed(taskInfo);
        recordErrorMessage(taskInfo, e);
    }

    /**
     * 记录测试调用日志
     *
     * @param schema    工作流Schema
     * @param inputs    输入参数
     * @param outputs   输出结果
     * @param startTime 开始时间
     * @param exception 异常信息（如果有的话）
     * @param workflow  工作流实例
     */
    private void recordTestCallLog(String schema, Map<String, Object> inputs, Map<String, Object> outputs, long startTime, Exception exception, GaiaWorkflow workflow) {
        try {
            // 解析工作流编码和版本ID（从schema中获取）
            String workflowCode = parseWorkflowCodeFromSchema(schema);
            Long versionId = parseVersionIdFromSchema(schema);
            String workflowContent = schema;

            // 计算耗时
            long costTime = System.currentTimeMillis() - startTime;

            // 准备日志字段
            String execParam = JSONUtil.toJsonStr(inputs);
            String execStatus = (exception == null) ? "SUCCESS" : "FAILED";
            String reports = "";
            if (workflow != null) {
                reports = JSONUtil.toJsonStr(workflow.getNodeReports());
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
                logger.info("Test call log - WorkflowCode: {}, VersionId: {}, CostTime: {}ms, Status: {}, Error: {}",
                           workflowCode, versionId, costTime, execStatus, errorMessage);
            }

        } catch (Exception e) {
            logger.error("记录测试调用日志失败", e);
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
     * 创建工作流实例
     *
     * @param schema 工作流Schema
     * @return 工作流实例
     */
    private GaiaWorkflow createWorkflow(String schema) {
        return new GaiaWorkflow(schema);
    }

    /**
     * 更新任务状态为成功
     *
     * @param taskInfo 任务信息
     */
    private void updateTaskStatusToSuccess(TaskInfo taskInfo) {
        taskInfo.getWorkflowStatus().setStatus(NodeStatus.SUCCESS.getValue());
        taskInfo.getWorkflowStatus().setTerminated(true);
    }

    /**
     * 更新任务状态为失败
     *
     * @param taskInfo 任务信息
     */
    private void updateTaskStatusToFailed(TaskInfo taskInfo) {
        taskInfo.getWorkflowStatus().setStatus(NodeStatus.FAIL.getValue());
        taskInfo.getWorkflowStatus().setTerminated(true);
    }

    /**
     * 记录错误信息
     *
     * @param taskInfo 任务信息
     * @param e 异常
     */
    private void recordErrorMessage(TaskInfo taskInfo, Exception e) {
        Messages messages = taskInfo.getMessages();
        if (messages == null) {
            messages = new Messages();
            taskInfo.setMessages(messages);
        }
        messages.getError().add(new ErrorMessage("workflow", e.getMessage()));
    }

    /**
     * 处理节点执行报告
     *
     * @param taskInfo 任务信息
     * @param workflow 工作流
     */
    private void processNodeReports(TaskInfo taskInfo, GaiaWorkflow workflow) {
        // 获取节点执行报告
        Map<String, GaiaWorkflow.NodeReport> nodeReports = workflow.getNodeReports();
        Map<String, NodeReport> taskNodeReports = new HashMap<>();

        // 获取Chain对象以访问执行信息
        Chain chain = workflow.toChain();
        Map<String, ChainNodeExecuteInfo> executeInfoMap = chain.getExecuteInfoMap();

        // 转换GaiaWorkflow.NodeReport为NodeReport
        nodeReports.forEach((nodeId, report) -> {
            NodeReport taskReport = createNodeReport(nodeId, report);

            // 获取节点执行信息
            ChainNodeExecuteInfo nodeInfo = executeInfoMap.get(nodeId);
            if (nodeInfo != null) {
                processNodeExecutionInfo(taskReport, nodeId, nodeInfo);
            }

            taskNodeReports.put(nodeId, taskReport);
        });

        // 设置节点报告
        taskInfo.setNodeReports(taskNodeReports);
    }

    /**
     * 创建节点报告
     *
     * @param nodeId 节点ID
     * @param report 原始报告
     * @return 节点报告
     */
    private NodeReport createNodeReport(String nodeId, GaiaWorkflow.NodeReport report) {
        return new NodeReport(
                nodeId,
                convertNodeStatus(report.getStatus()),
                report.getStartTime(),
                report.getEndTime(),
                calculateDuration(report),
                new ArrayList<>()
        );
    }

    /**
     * 计算执行时长
     *
     * @param report 原始报告
     * @return 执行时长
     */
    private long calculateDuration(GaiaWorkflow.NodeReport report) {
        if (report.getStartTime() != null && report.getEndTime() != null) {
            return report.getEndTime() - report.getStartTime();
        }
        return 0L;
    }

    /**
     * 处理节点执行信息
     *
     * @param taskReport 任务报告
     * @param nodeId 节点ID
     * @param nodeInfo 节点执行信息
     */
    private void processNodeExecutionInfo(NodeReport taskReport, String nodeId, ChainNodeExecuteInfo nodeInfo) {
        try {
            Snapshot snapshot = createSnapshot(nodeId);

            processNodeInputs(snapshot, nodeInfo);
            processNodeOutputs(snapshot, nodeInfo);
            processNodeExecuteResult(snapshot, nodeInfo);
            processNodeStatus(snapshot, nodeInfo);

            taskReport.getSnapshots().add(snapshot);
        } catch (Exception e) {
            logger.error("处理节点执行信息失败", e);
            taskReport.getSnapshots().add(createErrorSnapshot(nodeId, e));
        }
    }

    /**
     * 创建快照
     *
     * @param nodeId 节点ID
     * @return 快照
     */
    private Snapshot createSnapshot(String nodeId) {
        Snapshot snapshot = new Snapshot();
        snapshot.setId(nodeId);
        snapshot.setNodeID(nodeId);
        return snapshot;
    }

    /**
     * 创建错误快照
     *
     * @param nodeId 节点ID
     * @param e 异常
     * @return 错误快照
     */
    private Snapshot createErrorSnapshot(String nodeId, Exception e) {
        Snapshot errorSnapshot = new Snapshot();
        errorSnapshot.setId(UUID.randomUUID().toString());
        errorSnapshot.setNodeID(nodeId);
        errorSnapshot.setError("解析节点执行信息失败: " + e.getMessage());
        return errorSnapshot;
    }

    /**
     * 处理节点输入
     *
     * @param snapshot 快照
     * @param nodeInfo 节点执行信息
     */
    private void processNodeInputs(Snapshot snapshot, ChainNodeExecuteInfo nodeInfo) {
        if (nodeInfo.getInputsResult() != null) {
            Map<String, Object> inputsMap = new HashMap<>();
            inputsMap.putAll(JSONUtil.parseObj(nodeInfo.getInputsResult()));
            snapshot.setInputs(inputsMap);
        }
    }

    /**
     * 处理节点输出
     *
     * @param snapshot 快照
     * @param nodeInfo 节点执行信息
     */
    private void processNodeOutputs(Snapshot snapshot, ChainNodeExecuteInfo nodeInfo) {
        if (nodeInfo.getOutputResult() != null) {
            Map<String, Object> outputsMap = new HashMap<>();
            outputsMap.putAll(JSONUtil.parseObj(nodeInfo.getOutputResult()));
            snapshot.setOutputs(outputsMap);
        }
    }

    /**
     * 处理节点执行结果
     *
     * @param snapshot 快照
     * @param nodeInfo 节点执行信息
     */
    private void processNodeExecuteResult(Snapshot snapshot, ChainNodeExecuteInfo nodeInfo) {
        if (nodeInfo.getExecuteResult() != null) {
            snapshot.setData(JSONUtil.parseObj(nodeInfo.getExecuteResult()));
        }
    }

    /**
     * 处理节点状态
     *
     * @param snapshot 快照
     * @param nodeInfo 节点执行信息
     */
    private void processNodeStatus(Snapshot snapshot, ChainNodeExecuteInfo nodeInfo) {
        if (nodeInfo.getStatus() != null && nodeInfo.getStatus().name().contains("ERROR")) {
            snapshot.setError("执行出错: " + nodeInfo.getStatus().name());
        }
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
}