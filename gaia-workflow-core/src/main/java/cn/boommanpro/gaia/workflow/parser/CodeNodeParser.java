package cn.boommanpro.gaia.workflow.parser;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.node.CodeNode;
import cn.boommanpro.gaia.workflow.node.JsFunExecNode;
import cn.boommanpro.gaia.workflow.param.DataType;
import cn.boommanpro.gaia.workflow.param.Parameter;
import cn.boommanpro.gaia.workflow.param.RefType;
import cn.hutool.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 代码功能
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/05/17 11:35
 */
public class CodeNodeParser extends BaseNodeParser<CodeNode> {

    private static final String INPUTS_JSON_PATH = "$.data.inputs.properties";
    private static final String INPUTS_VALUES_JSON_PATH = "$.data.inputsValues";
    private static final String OUTPUTS_JSON_PATH = "$.data.outputs.properties";
    private static final String OUTPUTS_VALUES_JSON_PATH = "$.data.outputsValues";
    private static final String SCRIPT_CONTENT_PATH = "$.data.script.content";
    private static final String SCRIPT_LANGUAGE_PATH = "$.data.script.language";

    @Override
    public JsFunExecNode buildInstance(JSONObject nodeJSONObject, GaiaWorkflow workflow) {
        String scriptContent = nodeJSONObject.getByPath(SCRIPT_CONTENT_PATH).toString();

        JsFunExecNode codeNode = new JsFunExecNode(scriptContent);

        JSONObject inputsSchema = (JSONObject) nodeJSONObject.getByPath(INPUTS_JSON_PATH);
        JSONObject inputsValues = (JSONObject) nodeJSONObject.getByPath(INPUTS_VALUES_JSON_PATH);
        codeNode.setParameters(parseNodeParameters(inputsSchema, inputsValues));

        JSONObject outputsSchema = (JSONObject) nodeJSONObject.getByPath(OUTPUTS_JSON_PATH);
        JSONObject outputsValues = (JSONObject) nodeJSONObject.getByPath(OUTPUTS_VALUES_JSON_PATH);
        codeNode.setOutputParameters(parseNodeParameters(outputsSchema, outputsValues));

        return codeNode;
    }

    public static List<Parameter> parseNodeParameters(JSONObject schemaObject, JSONObject valueMapObject) {
        if (schemaObject == null) {
            return Collections.emptyList();
        }

        List<Parameter> parameters = new ArrayList<>();

        for (Map.Entry<String, Object> entry : schemaObject.entrySet()) {
            JSONObject paramJson = (JSONObject) entry.getValue();
            String key = entry.getKey();
            Parameter parameter = new Parameter();
            parameter.setName(key);
            parameter.setType(DataType.ofValue(paramJson.getStr("type")));

            // 设置是否必需
            parameter.setRequire(Optional.ofNullable(paramJson.getBool("isPropertyRequired"))
                    .orElse(Optional.ofNullable(paramJson.getBool("required")).orElse(false)));

            // 处理参数值
            if (valueMapObject != null && valueMapObject.containsKey(key)) {
                JSONObject valueObject = valueMapObject.getJSONObject(key);
                if (valueObject != null) {
                    parameter.setRefType(RefType.from(valueObject.getStr("type")));

                    if (parameter.getRefType() == RefType.REF) {
                        List<String> refValues = valueObject.getJSONArray("content").stream()
                                .map(Object::toString)
                                .collect(Collectors.toList());
                        parameter.setRefValue(refValues);

//                        // 处理额外信息
//                        if (valueObject.containsKey("extra")) {
//                            parameter.setExtra(valueObject.getJSONObject("extra"));
//                        }
                    } else {
                        parameter.setDefaultValue(valueObject.getStr("content"));
                    }
                }
            } else {
                parameter.setRefType(RefType.REF);
                parameter.setRefValue(Collections.singletonList(key));
            }

            parameters.add(parameter);
        }

        return parameters;
    }
}
