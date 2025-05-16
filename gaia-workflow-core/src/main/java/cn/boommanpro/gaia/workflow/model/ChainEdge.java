package cn.boommanpro.gaia.workflow.model;

import cn.boommanpro.gaia.workflow.status.ChainEdgeStatus;
import lombok.Data;

@Data
public class ChainEdge {
    private String id;
    private ChainEdgeStatus status=ChainEdgeStatus.READY;
}
