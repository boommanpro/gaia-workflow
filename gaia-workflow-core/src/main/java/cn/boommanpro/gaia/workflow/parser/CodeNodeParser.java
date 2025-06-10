package cn.boommanpro.gaia.workflow.parser;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.node.StartNode;
import cn.boommanpro.gaia.workflow.param.ParametersParseUtils;
import cn.hutool.json.JSONObject;

/**
 * 代码功能
 *
 * @author <a href="mailto:wangqimeng03@meituan.com">wangqimeg03</a>
 * @date 2025/05/17 11:35
 */
public class CodeNodeParser extends BaseNodeParser<StartNode>{

    private static final String jsonPath = "$.data.outputs.properties";

    @Override
    public StartNode buildInstance(JSONObject nodeJSONObject, GaiaWorkflow workflow) {
        StartNode startNode = new StartNode();
        startNode.setOutputParameters(ParametersParseUtils.parseStartNodeParameters((JSONObject) nodeJSONObject.getByPath(jsonPath)));
        return startNode;
    }
}
