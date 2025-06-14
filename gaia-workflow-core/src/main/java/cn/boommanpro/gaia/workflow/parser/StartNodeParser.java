package cn.boommanpro.gaia.workflow.parser;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.node.StartNode;
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
public class StartNodeParser extends BaseNodeParser<StartNode>{

    private static final String inputJsonPath = "$.data.outputs.properties";

    @Override
    public StartNode buildInstance(JSONObject nodeJSONObject, GaiaWorkflow workflow) {
        StartNode startNode = new StartNode();
        startNode.setOutputParameters(parseStartNodeParameters((JSONObject) nodeJSONObject.getByPath(inputJsonPath)));
        return startNode;
    }

    public static List<Parameter> parseStartNodeParameters(JSONObject nodeJSONObject) {
        List<Parameter> parameters = new ArrayList<>();

        for (Map.Entry<String, Object> entry : nodeJSONObject.entrySet()) {
            JSONObject paramJson = (JSONObject) entry.getValue();
            String key = entry.getKey();
            Parameter parameter = new Parameter();
            parameter.setName(key);
            parameter.setRefType(RefType.REF);
            parameter.setRefValue(Arrays.asList(key));
            parameter.setDefaultValue(paramJson.getStr("default"));
            parameter.setRequire(Optional.ofNullable(paramJson.getBool("isPropertyRequired")).orElse(false));
            parameter.setType(DataType.ofValue(paramJson.getStr("type")));
            parameters.add(parameter);
        }

        return parameters;
    }
}
