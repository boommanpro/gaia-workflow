package cn.boommanpro.gaia.workflow.node;

import cn.boommanpro.gaia.workflow.model.Chain;
import cn.boommanpro.gaia.workflow.model.ChainEdge;
import cn.boommanpro.gaia.workflow.model.ChainNode;
import cn.boommanpro.gaia.workflow.param.Parameter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class StartNode extends BaseNode {

    @Override
    public Map<String, Object> execute(Chain chain) {
        log.info("exec");
        return Collections.emptyMap();
    }

    @Override
    public List<Parameter> getParameters() {
        return super.getParameters();
    }

    @Override
    public List<Parameter> getOutputParameters() {
        return super.getOutputParameters();
    }
}
