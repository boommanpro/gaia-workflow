package cn.boommanpro.gaia.workflow.log;

import cn.boommanpro.gaia.workflow.model.ChainEdge;
import cn.boommanpro.gaia.workflow.model.ChainNode;
import cn.boommanpro.gaia.workflow.status.ChainNodeStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class ChainNodeExecuteInfo {
    private String id;
    private ChainNodeStatus status;
    private Long triggerTime;
    private Long startTime;
    private Long endTime;
    private String inputsResult;
    private String  executeResult;
    private String outputResult;
    private List<String> inwardEdges=new ArrayList<>();

    public void trigger() {
        if (triggerTime == null) {
            triggerTime = System.currentTimeMillis();
        }
    }
}
