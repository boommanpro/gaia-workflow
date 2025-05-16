package cn.boommanpro.gaia.workflow.model;

import cn.boommanpro.gaia.workflow.log.ChainNodeExecuteInfo;
import cn.boommanpro.gaia.workflow.node.StartNode;
import cn.boommanpro.gaia.workflow.param.Parameter;
import cn.boommanpro.gaia.workflow.status.ChainDepStatus;
import cn.boommanpro.gaia.workflow.status.ChainEdgeStatus;
import cn.boommanpro.gaia.workflow.status.ChainNodeStatus;
import cn.boommanpro.gaia.workflow.util.NamedThreadPools;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
public class Chain {

    private List<ChainNode> nodes;

    private List<ChainEdge> edges;

    private boolean async = true;

    private Map<String, Object> memoryContext;
    private ExecutorService asyncNodeExecutors = NamedThreadPools.newFixedThreadPool("chain-executor");
    private Map<String, ChainNodeExecuteInfo> executeInfoMap = new ConcurrentHashMap<>();



    public List<ChainNode> addNodes(ChainNode node) {
        if (nodes == null) {
            nodes = new ArrayList<>();
        }
        nodes.add(node);
        return nodes;
    }

    public List<ChainEdge> addEdges(ChainEdge edge) {
        if (edges == null) {
            edges = new ArrayList<>();
        }
        edges.add(edge);
        return edges;
    }

    public Map<String,Object> executeForResult(Map<String,Object> params) {
        ChainNode startNodes = getStartNodes(nodes);
        doExecuteChainNodes(Collections.singleton(startNodes));
        return params;
    }

    private void doExecuteChainNodes(Collection<ChainNode> list) {
        for (ChainNode chainNode : list) {
            asyncNodeExecutors.execute(()->{
                doExecuteChainNode(chainNode);
            });
        }
    }

    private void doExecuteChainNode(ChainNode chainNode) {
        //判断上游边的状态是否都是完成或者跳过状态，如果是，则执行该节点，否则跳过
        synchronized (chainNode) {
            updateCurrentNodeStatus(chainNode);
            ChainNodeExecuteInfo chainNodeExecuteInfo = executeInfoMap.get(chainNode.getId());
            ChainNodeStatus status = chainNode.getStatus();
            if (status == ChainNodeStatus.WAIT) {
                return;
            }
            chainNodeExecuteInfo.setStartTime(System.currentTimeMillis());
            chainNodeExecuteInfo.setInwardEdges(chainNode.getInwardEdges().stream().filter(chainEdge -> chainEdge.getStatus() == ChainEdgeStatus.TRUE).map(ChainEdge::getId).collect(Collectors.toList()));

            if (chainNodeExecuteInfo.getStatus() == ChainNodeStatus.READY) {
                Map<String, Object> execute = chainNode.execute(this);
                chainNodeExecuteInfo.setExecuteResult(execute);
                List<Parameter> outputParameters = chainNode.getOutputParameters();

            }

            chainNodeExecuteInfo.setEndTime(System.currentTimeMillis());
        }
    }

    private void updateCurrentNodeStatus(ChainNode chainNode) {
        ChainDepStatus chainDepStatus = ChainDepStatus.calcChainNodeDep(chainNode);
        ChainNodeExecuteInfo chainNodeExecuteInfo = executeInfoMap.computeIfAbsent(chainNode.getId(), id -> {
            ChainNodeExecuteInfo info = new ChainNodeExecuteInfo();
            info.setId(id);
            return info;
        });
        chainNodeExecuteInfo.setStatus(ChainNodeStatus.fromChainDepStatus(chainDepStatus));
    }

    private ChainNode getStartNodes(List<ChainNode> nodes) {
        return nodes.stream().filter(new Predicate<ChainNode>() {
            @Override
            public boolean test(ChainNode chainNode) {
                return chainNode instanceof StartNode;
            }
        }).findFirst().orElseThrow(() -> new RuntimeException("没有找到开始节点"));
    }


}
