package cn.boommanpro.gaia.workflow.parser;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.condition.EdgeCondition;
import cn.boommanpro.gaia.workflow.model.Chain;
import cn.boommanpro.gaia.workflow.model.ChainEdge;
import cn.boommanpro.gaia.workflow.model.ChainNode;
import cn.boommanpro.gaia.workflow.node.NodeTypeEnum;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 代码功能
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/05/17 11:31
 */
public class ChainParser {
    private Map<String, NodeParser> nodeParserMap = new HashMap<>();

    public ChainParser() {
        initParserMap();
    }


    public void initParserMap() {
        nodeParserMap.put(NodeTypeEnum.START.getCode(), new StartNodeParser());
        nodeParserMap.put(NodeTypeEnum.END.getCode(), new EndNodeParser());
        nodeParserMap.put(NodeTypeEnum.CODE.getCode(), new CodeNodeParser());
    }

    public Chain parse(JSONArray nodes, JSONArray edges, GaiaWorkflow workflow) {
        Chain chain = new Chain();
        for (int i = 0; i < nodes.size(); i++) {
            JSONObject nodeJSONObject = nodes.getJSONObject(i);
            String type = nodeJSONObject.getStr("type");
            if (NodeTypeEnum.NOTE.getCode().equals(type)) {
                continue;
            }
            ChainNode node = nodeParserMap.get(type).parse(nodeJSONObject, workflow);
            chain.addNode(node);
        }
        for (int i = 0; i < edges.size(); i++) {
            JSONObject edgeJSONObject = edges.getJSONObject(i);
            ChainEdge edge = parseEdge(edgeJSONObject);
            chain.addEdge(edge);
        }
        validateChain(chain);
        return chain;
    }

    private void validateChain(Chain chain) {
        List<ChainNode> nodes = chain.getNodes();
        Map<String, ChainNode> nodeMap = nodes.stream().collect(Collectors.toMap(ChainNode::getId, Function.identity()));

        // 存储节点的上游节点集合
        Map<String, Set<String>> nodeUpstreams = new HashMap<>();
        // 存储节点的下游节点集合
        Map<String, Set<String>> nodeDownstreams = new HashMap<>();

        // 构建节点依赖关系
        for (ChainEdge edge : chain.getEdges()) {
            String sourceId = edge.getSource();
            String targetId = edge.getTarget();

            // 记录下游节点
            nodeDownstreams.computeIfAbsent(sourceId, k -> new HashSet<>()).add(targetId);
            // 记录上游节点
            nodeUpstreams.computeIfAbsent(targetId, k -> new HashSet<>()).add(sourceId);
        }

        // 查找开始节点
        ChainNode startNode = findStartNode(chain, nodeUpstreams);
        if (startNode == null) {
            throw new IllegalStateException("No start node found in the chain");
        }

        //并发分支的出线不允许进入另一个并发分支

        // 设置并行执行标记并验证多输入节点
        for (Map.Entry<String, Set<String>> entry : nodeDownstreams.entrySet()) {
            String nodeId = entry.getKey();
            Set<String> downstreams = entry.getValue();

            // 如果一个节点有多个下游，将下游节点设置为异步执行
            if (downstreams.size() > 1) {
                for (String downstreamId : downstreams) {
                    ChainNode downstreamNode = nodeMap.get(downstreamId);
                    if (nodeMap.get(nodeId).isParallel() && downstreamNode.isParallel()) {
                        throw new RuntimeException("并发分支的出线不允许进入另一个并发分支");
                    }
                    downstreamNode.setParallel(true);
                }
            }
        }
    }

    private ChainEdge parseEdge(JSONObject edgeObject) {
        if (edgeObject == null) return null;
        ChainEdge edge = new ChainEdge();
        edge.setId(String.format("%s_%s_%s", edgeObject.getStr("sourceNodeID"), edgeObject.getStr("targetNodeID"), edgeObject.getStr("sourcePortID")));
        edge.setSource(edgeObject.getStr("sourceNodeID"));
        edge.setTarget(edgeObject.getStr("targetNodeID"));


        String conditionString = edgeObject.getStr("sourcePortID");
        if (StrUtil.isNotBlank(conditionString)) {
//            edge.setCondition(new JavascriptStringCondition(conditionString.trim()));
            edge.setCondition(new EdgeCondition() {
                @Override
                public boolean check(Chain chain, ChainEdge edge) {
                    Object o = chain.getMemory().get(String.format(edgeObject.getStr("sourceNodeID") + "." + edgeObject.getStr("sourcePortID")));
                    return Optional.ofNullable(o).map(new Function<Object, Boolean>() {
                        @Override
                        public Boolean apply(Object o) {
                            return Boolean.parseBoolean(o.toString());
                        }
                    }).orElse(false);
                }
            });
        }
        return edge;
    }


    private ChainNode findStartNode(Chain chain, Map<String, Set<String>> nodeUpstreams) {
        for (ChainNode node : chain.getNodes()) {
            // 开始节点没有上游节点
            if (node.getInwardEdges().isEmpty()) {
                return node;
            }
        }
        return null;
    }
}
