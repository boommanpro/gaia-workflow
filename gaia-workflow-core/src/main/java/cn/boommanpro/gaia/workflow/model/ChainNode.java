package cn.boommanpro.gaia.workflow.model;

import cn.boommanpro.gaia.workflow.param.Parameter;
import cn.boommanpro.gaia.workflow.status.ChainNodeStatus;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public abstract class ChainNode {

    private String id;
    private String name;

    private List<ChainEdge> inwardEdges;

    private List<ChainEdge> outwardEdges;

    private ChainNodeStatus status= ChainNodeStatus.WAIT;

    public abstract Map<String,Object> execute(Chain chain);

    public List<Parameter> getParameters(){
        return null;
    }

    public List<Parameter> getOutputParameters(){
        return null;
    }

}
