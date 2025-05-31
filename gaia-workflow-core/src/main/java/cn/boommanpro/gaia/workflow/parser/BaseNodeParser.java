package cn.boommanpro.gaia.workflow.parser;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.model.ChainNode;
import cn.boommanpro.gaia.workflow.node.NodeTypeEnum;
import cn.hutool.json.JSONObject;

/**
 * 代码功能
 *
 * @author <a href="mailto:wangqimeng03@meituan.com">wangqimeg03</a>
 * @date 2025/05/17 13:00
 */
public abstract class BaseNodeParser<T extends ChainNode> implements NodeParser{
    @Override
    public ChainNode parse(JSONObject nodeJSONObject, GaiaWorkflow workflow) {
        T t = buildInstance(nodeJSONObject,workflow);
        t.setId(nodeJSONObject.getStr("id"));
        t.setName(nodeJSONObject.getStr("name"));
        t.setNodeType(NodeTypeEnum.of(nodeJSONObject.getStr("type")));
        return t;
    }

    public abstract T buildInstance(JSONObject nodeJSONObject, GaiaWorkflow workflow);
}
