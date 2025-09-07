package cn.boommanpro.gaia.workflow;

import cn.boommanpro.gaia.workflow.log.ChainNodeExecuteInfo;
import cn.boommanpro.gaia.workflow.model.Chain;
import cn.boommanpro.gaia.workflow.parser.ChainParser;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GaiaWorkflow {

    private String data;

    private ChainParser chainParser = new ChainParser();

    private Chain chain;

    public GaiaWorkflow(String data) {
        this.data = data;
    }

    public Chain toChain() {
        if (chain == null) {
            JSONObject jsonObject = JSONUtil.parseObj(data);
            JSONArray nodes = jsonObject.getJSONArray("nodes");
            JSONArray edges = jsonObject.getJSONArray("edges");
            chain = chainParser.parse(nodes, edges, this);
        }
        return chain;
    }

    /**
     * 执行工作流
     *
     * @param inputs 输入参数
     * @return 执行结果
     */
    public Map<String, Object> run(Map<String, Object> inputs) {
        Chain chain = toChain();
        return chain.executeForResult(inputs);
    }

    /**
     * 执行工作流（无输入参数）
     *
     * @return 执行结果
     */
    public Map<String, Object> run() {
        return run(new HashMap<>());
    }

    /**
     * 获取节点执行报告
     *
     * @return 节点执行报告
     */
    public Map<String, NodeReport> getNodeReports() {
        // 确保工作流已经执行过，如果chain还未初始化则先执行一次
        Chain chain = toChain();
        Map<String, ChainNodeExecuteInfo> executeInfoMap = chain.getExecuteInfoMap();
        Map<String, NodeReport> nodeReports = new HashMap<>();

        executeInfoMap.forEach((nodeId, info) -> {
            String status = info.getStatus() != null ? info.getStatus().name() : "UNKNOWN";
            
            // 计算执行时长
            long timeCost = 0;
            if (info.getStartTime() != null && info.getEndTime() != null) {
                timeCost = info.getEndTime() - info.getStartTime();
            }
            
            // 创建 snapshots 列表
            List<Object> snapshots = new ArrayList<>();
            
            // 创建与 controller 中格式一致的 snapshot 对象
            Map<String, Object> snapshotData = new HashMap<>();
            snapshotData.put("id", nodeId);
            snapshotData.put("nodeID", nodeId);
            snapshotData.put("inputs", parseJsonStringToMap(info.getInputsResult()));
            snapshotData.put("outputs", parseJsonStringToMap(info.getOutputResult()));
            snapshotData.put("data", parseJsonStringToObject(info.getExecuteResult()));
            snapshotData.put("branch", ""); // 暂时为空，根据需要可以填充
            snapshotData.put("error", status.contains("FAILED") ? "执行失败" : "");
            
            snapshots.add(snapshotData);
            
            NodeReport report = new NodeReport(
                    nodeId,
                    status,
                    info.getStartTime(),
                    info.getEndTime(),
                    timeCost,
                    snapshots
            );
            nodeReports.put(nodeId, report);
        });

        return nodeReports;
    }

    /**
     * 将 JSON 字符串解析为 Map 对象
     *
     * @param jsonString JSON 字符串
     * @return Map 对象
     */
    private Map<String, Object> parseJsonStringToMap(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return JSONUtil.parseObj(jsonString);
        } catch (Exception e) {
            // 解析失败时返回空 Map
            return new HashMap<>();
        }
    }

    /**
     * 将 JSON 字符串解析为 Object 对象
     *
     * @param jsonString JSON 字符串
     * @return Object 对象
     */
    private Object parseJsonStringToObject(String jsonString) {
        if (jsonString == null || jsonString.isEmpty()) {
            return new HashMap<>();
        }
        try {
            return JSONUtil.parse(jsonString);
        } catch (Exception e) {
            // 解析失败时返回空对象
            return new HashMap<>();
        }
    }

    /**
     * 节点报告类
     */
    @Data
    @AllArgsConstructor
    public static class NodeReport {
        private String id;
        private String status;
        private Long startTime;
        private Long endTime;
        private Long timeCost;
        private List<Object> snapshots = new ArrayList<>();

    }
}