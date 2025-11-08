package cn.boommanpro.gaia.workflow.node.loop;

import cn.boommanpro.gaia.workflow.model.Chain;
import cn.boommanpro.gaia.workflow.node.BaseNode;
import cn.boommanpro.gaia.workflow.node.NodeTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 块开始节点，用于标记循环或条件块的开始
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/05/17
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class BlockStartNode extends BaseNode {

    @Override
    public Map<String, Object> execute(Chain chain) {
        log.debug("Executing BlockStart node: {}", getId());
        // BlockStart节点本身不执行任何逻辑，只是一个标记
        return new HashMap<>();
    }

    @Override
    public NodeTypeEnum getNodeType() {
        return NodeTypeEnum.BLOCK_START;
    }

    @Override
    protected Map<String, Object> getParametersData(Chain chain) {
        return chain.getMemory();
    }
}

