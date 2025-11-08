package cn.boommanpro.gaia.workflow.parser;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.common.NodeParserUtils;
import cn.boommanpro.gaia.workflow.model.ChainNode;
import cn.hutool.json.JSONObject;

/**
 * 节点解析器抽象基类
 * 提供通用的节点解析功能，减少子类重复代码
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/05/17 13:00
 */
public abstract class BaseNodeParser<T extends ChainNode> implements NodeParser {

    @Override
    public ChainNode parse(JSONObject nodeJSONObject, GaiaWorkflow workflow) {
        T node = buildInstance(nodeJSONObject, workflow);
        return NodeParserUtils.parseBaseNodeProperties(nodeJSONObject, workflow, node);
    }

    /**
     * 构建节点实例（由子类实现）
     *
     * @param nodeJSONObject 节点JSON对象
     * @param workflow 工作流实例
     * @return 节点实例
     */
    public abstract T buildInstance(JSONObject nodeJSONObject, GaiaWorkflow workflow);
}
