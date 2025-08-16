package cn.boommanpro.gaia.workflow.parser;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.node.EndNode;
import cn.boommanpro.gaia.workflow.node.StartNode;
import cn.boommanpro.gaia.workflow.param.DataType;
import cn.boommanpro.gaia.workflow.param.Parameter;
import cn.boommanpro.gaia.workflow.param.ParametersParseUtils;
import cn.boommanpro.gaia.workflow.param.RefType;
import cn.hutool.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 代码功能
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/05/17 11:35
 */
public class EndNodeParser extends BaseNodeParser<EndNode>{

    private static final String inputJsonPath = "$.data.inputs.properties";
    private static final String inputValuesJsonPath = "$.data.inputsValues";
    @Override
    public EndNode buildInstance(JSONObject nodeJSONObject, GaiaWorkflow workflow) {
        EndNode endNode = new EndNode();
        endNode.setOutputParameters(parseStartNodeParameters((JSONObject) nodeJSONObject.getByPath(inputJsonPath),(JSONObject) nodeJSONObject.getByPath(inputValuesJsonPath)));
        return endNode;
    }

    public static List<Parameter> parseStartNodeParameters(JSONObject schemaObject, JSONObject valueMapObject) {
        List<Parameter> parameters = new ArrayList<>();

        for (Map.Entry<String, Object> entry : schemaObject.entrySet()) {
            JSONObject paramJson = (JSONObject) entry.getValue();
            String key = entry.getKey();
            Parameter parameter = new Parameter();
            parameter.setName(key);
            parameter.setType(DataType.ofValue(paramJson.getStr("type")));
            JSONObject valueObject = valueMapObject.getJSONObject(key);
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
            parameter.setRequire(Optional.ofNullable(paramJson.getBool("isPropertyRequired")).orElse(false));
            parameters.add(parameter);
        }

        return parameters;
    }
}
