package cn.boommanpro.gaia.workflow.service.impl;

import cn.boommanpro.gaia.workflow.model.WorkflowDefinition;
import cn.boommanpro.gaia.workflow.service.WorkflowStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 内存实现的工作流存储服务
 * 仅用于开发和测试，生产环境应使用数据库实现
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/11/10
 */
@Slf4j
@Service
public class InMemoryWorkflowStorageService implements WorkflowStorageService {

    private final Map<String, WorkflowDefinition> workflowStore = new ConcurrentHashMap<>();

    public InMemoryWorkflowStorageService() {
        // 初始化示例数据
        initSampleData();
    }

    @Override
    public List<WorkflowDefinition> getAllWorkflows() {
        return new ArrayList<>(workflowStore.values());
    }

    @Override
    public WorkflowDefinition getWorkflowById(String id) {
        return workflowStore.get(id);
    }

    @Override
    public WorkflowDefinition saveWorkflow(WorkflowDefinition workflow) {
        if (workflow.getId() == null || workflow.getId().isEmpty()) {
            workflow.setId(generateId());
        }

        LocalDateTime now = LocalDateTime.now();
        if (workflow.getCreatedAt() == null) {
            workflow.setCreatedAt(now);
        }
        workflow.setUpdatedAt(now);

        workflowStore.put(workflow.getId(), workflow);
        log.info("保存工作流定义: {}", workflow.getId());
        return workflow;
    }

    @Override
    public WorkflowDefinition updateWorkflow(String id, WorkflowDefinition workflow) {
        if (!workflowStore.containsKey(id)) {
            throw new IllegalArgumentException("工作流不存在: " + id);
        }

        workflow.setId(id);
        workflow.setUpdatedAt(LocalDateTime.now());
        workflowStore.put(id, workflow);
        log.info("更新工作流定义: {}", id);
        return workflow;
    }

    @Override
    public boolean deleteWorkflow(String id) {
        WorkflowDefinition removed = workflowStore.remove(id);
        boolean deleted = removed != null;
        if (deleted) {
            log.info("删除工作流定义: {}", id);
        }
        return deleted;
    }

    @Override
    public List<WorkflowDefinition> searchWorkflowsByTag(String tag) {
        return workflowStore.values().stream()
                .filter(w -> tag != null &&
                           tag.equals(w.getTags()))
                .collect(Collectors.toList());
    }

    @Override
    public List<WorkflowDefinition> searchWorkflowsByName(String name) {
        return workflowStore.values().stream()
                .filter(w -> name != null &&
                           (w.getName() != null && w.getName().toLowerCase().contains(name.toLowerCase())))
                .collect(Collectors.toList());
    }

    /**
     * 生成唯一ID
     */
    private String generateId() {
        return "workflow_" + System.currentTimeMillis() + "_" + (int)(Math.random() * 1000);
    }

    /**
     * 初始化示例数据
     */
    private void initSampleData() {
        // 示例工作流1：简单的workflow
        WorkflowDefinition simpleWorkflow = new WorkflowDefinition();
        simpleWorkflow.setId("simple-workflow-1");
        simpleWorkflow.setName("简单的workflow");
        simpleWorkflow.setDescription("简单的workflow");
        simpleWorkflow.setTags("demo");
        simpleWorkflow.setIsExample(true);
        simpleWorkflow.setDefinition(getSimpleWorkflowDefinition());
        simpleWorkflow.setInputs("{\"type\":\"object\",\"properties\":{\"success\":{\"type\":\"boolean\"},\"query\":{\"type\":\"string\"}}}");
        simpleWorkflow.setOutputs("{\"type\":\"object\",\"properties\":{\"query\":{\"type\":\"string\",\"default\":\"Hello Flow.\"},\"enable\":{\"type\":\"boolean\",\"default\":true},\"array_obj\":{\"type\":\"array\",\"items\":{\"type\":\"integer\"},\"default\":\"[1,2,3]\"}}}");
        simpleWorkflow.setCreatedAt(LocalDateTime.now());
        simpleWorkflow.setUpdatedAt(LocalDateTime.now());
        workflowStore.put(simpleWorkflow.getId(), simpleWorkflow);

        // 示例工作流2：数据处理工作流
        WorkflowDefinition dataProcessingWorkflow = new WorkflowDefinition();
        dataProcessingWorkflow.setId("data-processing-workflow");
        dataProcessingWorkflow.setName("数据处理工作流");
        dataProcessingWorkflow.setDescription("用于数据清洗和转换的工作流");
        dataProcessingWorkflow.setTags("data,processing");
        dataProcessingWorkflow.setIsExample(true);
        dataProcessingWorkflow.setDefinition("{}");
        dataProcessingWorkflow.setInputs("{\"type\":\"object\",\"properties\":{\"rawData\":{\"type\":\"array\",\"items\":{\"type\":\"object\"}},\"filterCriteria\":{\"type\":\"object\"}}}");
        dataProcessingWorkflow.setOutputs("{\"type\":\"object\",\"properties\":{\"processedData\":{\"type\":\"array\",\"items\":{\"type\":\"object\"}},\"statistics\":{\"type\":\"object\"}}}");
        dataProcessingWorkflow.setCreatedAt(LocalDateTime.now());
        dataProcessingWorkflow.setUpdatedAt(LocalDateTime.now());
        workflowStore.put(dataProcessingWorkflow.getId(), dataProcessingWorkflow);

        log.info("初始化示例工作流数据完成，共{}个工作流", workflowStore.size());
    }

    /**
     * 获取简单工作流的定义
     */
    private String getSimpleWorkflowDefinition() {
        return "{\n" +
                "  \"nodes\": [\n" +
                "    {\n" +
                "      \"id\": \"start_0\",\n" +
                "      \"type\": \"start\",\n" +
                "      \"meta\": {\n" +
                "        \"position\": { \"x\": 180, \"y\": 0 }\n" +
                "      },\n" +
                "      \"data\": {\n" +
                "        \"title\": \"开始节点\",\n" +
                "        \"outputs\": {\n" +
                "          \"type\": \"object\",\n" +
                "          \"properties\": {\n" +
                "            \"query\": {\n" +
                "              \"type\": \"string\",\n" +
                "              \"default\": \"Hello Flow.\"\n" +
                "            },\n" +
                "            \"enable\": {\n" +
                "              \"type\": \"boolean\",\n" +
                "              \"default\": true\n" +
                "            },\n" +
                "            \"array_obj\": {\n" +
                "              \"type\": \"array\",\n" +
                "              \"items\": { \"type\": \"integer\" },\n" +
                "              \"default\": \"[1,2,3]\"\n" +
                "            }\n" +
                "          },\n" +
                "          \"required\": []\n" +
                "        }\n" +
                "      }\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"end_0\",\n" +
                "      \"type\": \"end\",\n" +
                "      \"meta\": {\n" +
                "        \"position\": { \"x\": 640, \"y\": 0 }\n" +
                "      },\n" +
                "      \"data\": {\n" +
                "        \"title\": \"结束节点\",\n" +
                "        \"inputsValues\": {\n" +
                "          \"success\": {\n" +
                "            \"type\": \"constant\",\n" +
                "            \"content\": true,\n" +
                "            \"schema\": { \"type\": \"boolean\" },\n" +
                "            \"extra\": { \"index\": 0 }\n" +
                "          },\n" +
                "          \"query\": {\n" +
                "            \"type\": \"ref\",\n" +
                "            \"content\": [\"start_0\", \"query\"],\n" +
                "            \"extra\": { \"index\": 1 }\n" +
                "          }\n" +
                "        },\n" +
                "        \"inputs\": {\n" +
                "          \"type\": \"object\",\n" +
                "          \"properties\": {\n" +
                "            \"success\": { \"type\": \"boolean\" },\n" +
                "            \"query\": { \"type\": \"string\" }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  ],\n" +
                "  \"edges\": [\n" +
                "    {\n" +
                "      \"sourceNodeID\": \"start_0\",\n" +
                "      \"targetNodeID\": \"end_0\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }
}

