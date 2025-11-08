package cn.boommanpro.gaia.workflow.model;

import cn.boommanpro.gaia.workflow.condition.EdgeCondition;
import cn.boommanpro.gaia.workflow.status.ChainEdgeStatus;
import lombok.Data;

@Data
public class ChainEdge {
    private String id;
    private ChainEdgeStatus status=ChainEdgeStatus.READY;
    private EdgeCondition condition;
    private String target;
    private String source;
    private String sourcePortID;

    public ChainEdge() {
    }

    public ChainEdge(String id, String source, String target) {
        this.id = id;
        this.source = source;
        this.target = target;
    }

    public ChainEdge(String id, String source, String target, String sourcePortID) {
        this.id = id;
        this.source = source;
        this.target = target;
        this.sourcePortID = sourcePortID;
    }
}
