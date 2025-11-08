package cn.boommanpro.gaia.workflow.parser;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.node.ContinueNode;
import cn.hutool.json.JSONObject;

/**
 * 继续节点解析器
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/05/17
 */
public class ContinueNodeParser extends BaseNodeParser<ContinueNode> {

    @Override
    public ContinueNode buildInstance(JSONObject nodeJSONObject, GaiaWorkflow workflow) {
        return new ContinueNode();
    }
}

