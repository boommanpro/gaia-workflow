package cn.boommanpro.gaia.workflow.parser;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.node.loop.BlockEndNode;
import cn.hutool.json.JSONObject;

/**
 * 块结束节点解析器
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/05/17
 */
public class BlockEndNodeParser extends BaseNodeParser<BlockEndNode> {

    @Override
    public BlockEndNode buildInstance(JSONObject nodeJSONObject, GaiaWorkflow workflow) {
        return new BlockEndNode();
    }
}

