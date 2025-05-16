package cn.boommanpro.gaia.workflow.node;

import cn.boommanpro.gaia.workflow.model.Chain;
import cn.boommanpro.gaia.workflow.model.ChainNode;

import java.util.Collections;
import java.util.Map;

public class StartNode extends ChainNode {

    @Override
    public Map<String, Object> execute(Chain chain) {
        return Collections.emptyMap();
    }
}
