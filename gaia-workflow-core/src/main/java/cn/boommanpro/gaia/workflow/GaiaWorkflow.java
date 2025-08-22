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
        Chain chain = toChain();
        Map<String, ChainNodeExecuteInfo> executeInfoMap = chain.getExecuteInfoMap();
        Map<String, NodeReport> nodeReports = new HashMap<>();

        executeInfoMap.forEach((nodeId, info) -> {
            String status = info.getStatus() != null ? info.getStatus().name() : "UNKNOWN";
            NodeReport report = new NodeReport(
                    nodeId,
                    status,
                    info.getStartTime(),
                    info.getEndTime(), new ArrayList<>()
            );
            nodeReports.put(nodeId, report);
        });

        return nodeReports;
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
        private List<Object> snapshots = new ArrayList<>();

    }
}
