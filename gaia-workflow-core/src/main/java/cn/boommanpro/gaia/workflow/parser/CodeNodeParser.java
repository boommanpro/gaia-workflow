package cn.boommanpro.gaia.workflow.parser;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.node.CodeNode;
import cn.boommanpro.gaia.workflow.node.JsFunExecNode;
import cn.boommanpro.gaia.workflow.param.DataType;
import cn.boommanpro.gaia.workflow.param.Parameter;
import cn.boommanpro.gaia.workflow.param.ParametersParseUtils;
import cn.boommanpro.gaia.workflow.param.RefType;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 代码功能
 *
 * @author <a href="mailto:wangqimeng03@meituan.com">wangqimeg03</a>
 * @date 2025/05/17 11:35
 */
public class CodeNodeParser extends BaseNodeParser<CodeNode> {

    private static final String inputsJsonPath = "$.data.inputs.properties";
    private static final String outputsJsonPath = "$.data.outputs.properties";

    @Override
    public JsFunExecNode buildInstance(JSONObject nodeJSONObject, GaiaWorkflow workflow) {
        JsFunExecNode codeNode = new JsFunExecNode((String) nodeJSONObject.getByPath("$.data.config.code"));
        codeNode.setParameters(ParametersParseUtils.parse((JSONObject) nodeJSONObject.getByPath(inputsJsonPath)));
        codeNode.setOutputParameters(parse((JSONObject) nodeJSONObject.getByPath(outputsJsonPath)));
        return codeNode;
    }

    public static List<Parameter> parse(JSONObject nodeJSONObject) {
        List<Parameter> parameters = new ArrayList<>();

        for (Map.Entry<String, Object> entry : nodeJSONObject.entrySet()) {
            JSONObject paramJson = (JSONObject) entry.getValue();

            Parameter parameter = new Parameter();
            parameter.setName(paramJson.getStr("name"));
            Object value = paramJson.get("default");
            parameter.setRefType(RefType.REF);
            JSONObject refValue = (JSONObject) value;
            parameter.setRefValue(Arrays.asList(entry.getKey()));

            if (StrUtil.isBlank(parameter.getName())) {
                parameter.setName(entry.getKey());
            }
            parameter.setRequire(Optional.ofNullable(paramJson.getBool("isPropertyRequired")).orElse(false));
            parameter.setType(DataType.ofValue(paramJson.getStr("type")));
            parameters.add(parameter);
        }

        return parameters;
    }
}
