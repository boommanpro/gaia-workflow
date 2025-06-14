package cn.boommanpro.gaia.workflow.parser;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.node.CodeNode;
import cn.boommanpro.gaia.workflow.node.JsFunExecNode;
import cn.boommanpro.gaia.workflow.param.DataType;
import cn.boommanpro.gaia.workflow.param.Parameter;
import cn.boommanpro.gaia.workflow.param.RefType;
import cn.hutool.json.JSONObject;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 代码功能
 *
 * @author <a href="mailto:wangqimeng03@meituan.com">wangqimeg03</a>
 * @date 2025/05/17 11:35
 */
public class CodeNodeParser extends BaseNodeParser<CodeNode> {

    private static final String inputsJsonPath = "$.data.inputs.properties";
    private static final String inputsValuesJsonPath = "$.data.inputsValues";
    private static final String outputsJsonPath = "$.data.outputs.properties";
    private static final String outputsValuesJsonPath = "$.data.outputsValues";

    @Override
    public JsFunExecNode buildInstance(JSONObject nodeJSONObject, GaiaWorkflow workflow) {
        JsFunExecNode codeNode = new JsFunExecNode((String) nodeJSONObject.getByPath("$.data.config.code"));
        codeNode.setParameters(parseNodeParameters((JSONObject) nodeJSONObject.getByPath(inputsJsonPath),(JSONObject) nodeJSONObject.getByPath(inputsValuesJsonPath)));
        codeNode.setOutputParameters(parseNodeParameters((JSONObject) nodeJSONObject.getByPath(outputsJsonPath),(JSONObject) nodeJSONObject.getByPath(outputsValuesJsonPath)));
        return codeNode;
    }
    public static List<Parameter> parseNodeParameters(JSONObject schemaObject, JSONObject valueMapObject) {
        List<Parameter> parameters = new ArrayList<>();

        for (Map.Entry<String, Object> entry : schemaObject.entrySet()) {
            JSONObject paramJson = (JSONObject) entry.getValue();
            String key = entry.getKey();
            Parameter parameter = new Parameter();
            parameter.setName(key);
            parameter.setType(DataType.ofValue(paramJson.getStr("type")));
            JSONObject valueObject = valueMapObject.getJSONObject(key);
            if (valueObject == null) {
                parameter.setRefType(RefType.REF);
                parameter.setRefValue(Arrays.asList(key));
            }else {
                parameter.setRefType(RefType.from(valueObject.getStr("type")));
                if (parameter.getRefType() == RefType.REF) {
                    parameter.setRefValue(valueObject.getJSONArray("content").stream().map(new Function<Object, String>() {
                        @Override
                        public String apply(Object o) {
                            return o.toString();
                        }
                    }).collect(Collectors.toList()));
                }else {

                    parameter.setDefaultValue(valueObject.getStr("content"));
                }
            }

            parameter.setRequire(Optional.ofNullable(paramJson.getBool("isPropertyRequired")).orElse(false));
            parameters.add(parameter);
        }

        return parameters;
    }
}
