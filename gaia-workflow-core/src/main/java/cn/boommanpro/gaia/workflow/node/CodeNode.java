package cn.boommanpro.gaia.workflow.node;

import cn.boommanpro.gaia.workflow.model.Chain;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class CodeNode extends BaseNode {

    @Override
    public Map<String, Object> execute(Chain chain) {
        return chain.getMemory();
    }


    @Override
    public NodeTypeEnum getNodeType() {
        return  NodeTypeEnum.CONDITION;
    }

    @Override
    protected Map<String, Object> getParametersData(Chain chain) {
        return chain.getMemory();
    }
}
