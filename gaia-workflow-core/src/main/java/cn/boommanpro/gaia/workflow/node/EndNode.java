package cn.boommanpro.gaia.workflow.node;

import cn.boommanpro.gaia.workflow.model.Chain;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class EndNode extends BaseNode {

    @Override
    public Map<String, Object> execute(Chain chain) {
        return chain.getMemory();
    }
}
