package cn.boommanpro.gaia.workflow.parser;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.node.loop.BreakNode;
import cn.hutool.json.JSONObject;

/**
 * 中断节点解析器
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/05/17
 */
public class BreakNodeParser extends BaseNodeParser<BreakNode> {

    @Override
    public BreakNode buildInstance(JSONObject nodeJSONObject, GaiaWorkflow workflow) {
        return new BreakNode();
    }
}

