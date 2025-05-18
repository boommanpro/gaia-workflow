package cn.boommanpro.gaia.workflow.model;

import cn.boommanpro.gaia.workflow.condition.EdgeCondition;
import cn.boommanpro.gaia.workflow.exception.ChainException;
import cn.boommanpro.gaia.workflow.log.ChainNodeExecuteInfo;
import cn.boommanpro.gaia.workflow.node.StartNode;
import cn.boommanpro.gaia.workflow.param.Parameter;
import cn.boommanpro.gaia.workflow.status.ChainDepStatus;
import cn.boommanpro.gaia.workflow.status.ChainEdgeStatus;
import cn.boommanpro.gaia.workflow.status.ChainNodeStatus;
import cn.boommanpro.gaia.workflow.status.ChainStatus;
import cn.boommanpro.gaia.workflow.util.NamedThreadPools;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Phaser;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Getter
public class Chain extends ChainNode {

    private List<ChainNode> nodes;

    private List<ChainEdge> edges;

    private boolean async = true;

    private Map<String, Object> memoryContext;
    private ExecutorService asyncNodeExecutors = NamedThreadPools.newFixedThreadPool("chain-executor");
    private Map<String, ChainNodeExecuteInfo> executeInfoMap = new ConcurrentHashMap<>();

    private Map<String, Object> outputResult;
    private Map<String, Object> executeResult;
    private Map<String, Object> memory=new HashMap<>();
    private ChainStatus chainStatus = ChainStatus.READY;
    protected Exception exception;
    private String message;
    protected Phaser phaser = new Phaser(1);


    public List<ChainNode> addNode(ChainNode node) {
        if (nodes == null) {
            nodes = new ArrayList<>();
        }
        nodes.add(node);
        return nodes;
    }

    public List<ChainEdge> addEdge(ChainEdge edge) {
        if (edges == null) {
            edges = new ArrayList<>();
        }
        edges.add(edge);
        boolean findSource = false, findTarget = false;
        for (ChainNode node : nodes) {
            if (node.getId().equals(edge.getSource())) {
                node.addOutwardEdge(edge);
                findSource = true;
            } else if (node.getId().equals(edge.getTarget())) {
                node.addInwardEdge(edge);
                findTarget = true;
            }
            if (findSource && findTarget) {
                break;
            }
        }
        return edges;
    }

    public Map<String, Object> executeForResult(Map<String, Object> params) {
        ChainNode startNodes = getStartNodes(nodes);
        doExecuteChainNodes(Collections.singleton(startNodes));
        this.phaser.arriveAndAwaitAdvance();
        if (this.chainStatus == ChainStatus.FINISHED_ABNORMAL) {
            if (this.exception != null) {
                if (this.exception instanceof RuntimeException) {
                    throw (RuntimeException) this.exception;
                } else {
                    throw new ChainException(this.exception);
                }
            } else {
                if (this.message == null) this.message = "Chain execute error";
                throw new ChainException(this.message);
            }
        }
        return outputResult;
    }

    private void doExecuteChainNodes(Collection<ChainNode> list) {
        for (ChainNode chainNode : list) {
            phaser.register();
            asyncNodeExecutors.execute(() -> {
                doExecuteChainNode(chainNode);
                phaser.arriveAndDeregister();
            });
        }
    }

    private void doExecuteChainNode(ChainNode chainNode) {
        //判断上游边的状态是否都是完成或者跳过状态，如果是，则执行该节点，否则跳过
        synchronized (chainNode) {
            updateCurrentNodeStatus(chainNode);
            ChainNodeExecuteInfo chainNodeExecuteInfo = executeInfoMap.get(chainNode.getId());
            chainNode.setStatus(chainNodeExecuteInfo.getStatus());
            if (chainNode.getStatus() == ChainNodeStatus.WAIT) {
                return;
            }
            chainNodeExecuteInfo.setStartTime(System.currentTimeMillis());
            chainNodeExecuteInfo.setInwardEdges(chainNode.getInwardEdges().stream().filter(chainEdge -> chainEdge.getStatus() == ChainEdgeStatus.TRUE).map(ChainEdge::getId).collect(Collectors.toList()));

            if (chainNodeExecuteInfo.getStatus() == ChainNodeStatus.READY) {
                Map<String, Object> execute = chainNode.execute(this);
                try {
                    chainNode.setStatus(ChainNodeStatus.RUNNING);
                    chainNodeExecuteInfo.setExecuteResult(execute);
                    List<Parameter> outputParameters = chainNode.getOutputParameters();
                    Map<String, Object> outputResult = new HashMap<>();
                    chainNodeExecuteInfo.setOutputResult(outputResult);
                    chainNode.setStatus(ChainNodeStatus.FINISHED);
                } catch (Exception e) {
                    chainNode.setStatus(ChainNodeStatus.FAILED);
                }

            }
            if (chainNode.getStatus() == ChainNodeStatus.FINISHED) {
                for (ChainEdge outwardEdge : chainNode.getOutwardEdges()) {
                    EdgeCondition condition = outwardEdge.getCondition();
                    if (condition == null) {
                        outwardEdge.setStatus(ChainEdgeStatus.TRUE);
                        doExecuteChainNode(getByNodeId(outwardEdge.getTarget()));
                        continue;
                    }
                    if (condition.check(this, outwardEdge)) {
                        outwardEdge.setStatus(ChainEdgeStatus.TRUE);
                        doExecuteChainNode(getByNodeId(outwardEdge.getTarget()));
                    } else {
                        outwardEdge.setStatus(ChainEdgeStatus.FALSE);
                        doExecuteChainNode(getByNodeId(outwardEdge.getTarget()));
                    }
                }
            } else if (chainNode.getStatus() == ChainNodeStatus.SKIPPED) {
                for (ChainEdge outwardEdge : chainNode.getOutwardEdges()) {
                    outwardEdge.setStatus(ChainEdgeStatus.SKIPPED);
                    doExecuteChainNode(getByNodeId(outwardEdge.getTarget()));
                }
            }


            chainNodeExecuteInfo.setEndTime(System.currentTimeMillis());
        }
    }

    private ChainNode getByNodeId(String nodeId) {
        for (ChainNode node : nodes) {
            if (node.getId().equals(nodeId)) {
                return node;
            }
        }
        throw new RuntimeException("not found nodeId " + nodeId);
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


    @Override
    public Map<String, Object> execute(Chain parent) {
        return executeForResult(parent.getMemory());
    }
}
