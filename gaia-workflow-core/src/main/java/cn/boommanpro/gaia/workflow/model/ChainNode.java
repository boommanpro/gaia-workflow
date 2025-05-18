package cn.boommanpro.gaia.workflow.model;

import cn.boommanpro.gaia.workflow.node.NodeTypeEnum;
import cn.boommanpro.gaia.workflow.param.Parameter;
import cn.boommanpro.gaia.workflow.status.ChainNodeStatus;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public abstract class ChainNode {

    private String id;

    private String name;

    private NodeTypeEnum nodeType;

    private List<ChainEdge> inwardEdges=new ArrayList<>();

    private List<ChainEdge> outwardEdges=new ArrayList<>();

    private ChainNodeStatus status= ChainNodeStatus.WAIT;

    private boolean parallel;

    public abstract Map<String,Object> execute(Chain chain);

    public List<Parameter> getParameters(){
        return null;
    }

    public List<Parameter> getOutputParameters(){
        return null;
    }

    public void addOutwardEdge(ChainEdge edge) {
        outwardEdges.add(edge);
    }

    public void addInwardEdge(ChainEdge edge) {
        inwardEdges.add(edge);
    }
}
