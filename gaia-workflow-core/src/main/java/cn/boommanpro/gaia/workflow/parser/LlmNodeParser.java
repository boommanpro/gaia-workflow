package cn.boommanpro.gaia.workflow.parser;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.common.NodeParserUtils;
import cn.boommanpro.gaia.workflow.common.ParameterParseUtils;
import cn.boommanpro.gaia.workflow.node.LlmNode;
import cn.hutool.json.JSONObject;

/**
 * LlmNode解析器
 * 使用通用工具类减少重复代码
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 */
public class LlmNodeParser extends BaseNodeParser<LlmNode> {

    private static final String INPUTS_VALUES_PATH = "$.data.inputsValues";
    private static final String TIMEOUT_CONFIG_PATH = "$.data.timeout";

    @Override
    public LlmNode buildInstance(JSONObject nodeJSONObject, GaiaWorkflow workflow) {
        LlmNode llmNode = new LlmNode();

        // 获取data对象
        JSONObject dataObj = NodeParserUtils.getDataObject(nodeJSONObject);
        if (dataObj == null) {
            return llmNode;
        }

        // 使用通用工具类解析inputsValues
        JSONObject inputsValues = NodeParserUtils.parseValuesObject(nodeJSONObject, INPUTS_VALUES_PATH);
        if (inputsValues != null) {
            llmNode.setInputsValues(inputsValues);
        }

        // 使用通用工具类解析超时配置
        NodeParserUtils.TimeoutConfig timeoutConfig = NodeParserUtils.parseTimeoutConfig(
            nodeJSONObject, TIMEOUT_CONFIG_PATH, 30000, 1);
        LlmNode.TimeoutConfig nodeTimeoutConfig = new LlmNode.TimeoutConfig();
        nodeTimeoutConfig.setTimeout(timeoutConfig.getTimeout());
        nodeTimeoutConfig.setRetryTimes(timeoutConfig.getRetryTimes());
        llmNode.setTimeout(nodeTimeoutConfig);

        return llmNode;
    }
}

