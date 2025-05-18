package cn.boommanpro.gaia.workflow.parser;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.model.ChainNode;
import cn.boommanpro.gaia.workflow.node.StartNode;
import cn.hutool.json.JSONObject;

/**
 * 代码功能
 *
 * @author <a href="mailto:wangqimeng03@meituan.com">wangqimeg03</a>
 * @date 2025/05/17 11:35
 */
public class StartNodeParser extends BaseNodeParser<StartNode>{

    @Override
    public StartNode buildInstance(GaiaWorkflow workflow) {
        return new StartNode();
    }
}
