package cn.boommanpro.gaia.workflow.parser;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.node.loop.BlockStartNode;
import cn.hutool.json.JSONObject;

/**
 * 块开始节点解析器
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/05/17
 */
public class BlockStartNodeParser extends BaseNodeParser<BlockStartNode> {

    @Override
    public BlockStartNode buildInstance(JSONObject nodeJSONObject, GaiaWorkflow workflow) {
        return new BlockStartNode();
    }
}

