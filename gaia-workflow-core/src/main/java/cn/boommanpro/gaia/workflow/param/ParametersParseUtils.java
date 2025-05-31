package cn.boommanpro.gaia.workflow.param;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;

import java.util.*;
import java.util.stream.Collectors;

public class ParametersParseUtils {
    public static List<Parameter> parse(JSONObject nodeJSONObject) {
        List<Parameter> parameters = new ArrayList<>();

        for (Map.Entry<String, Object> entry : nodeJSONObject.entrySet()) {
            JSONObject paramJson = (JSONObject) entry.getValue();

            Parameter parameter = new Parameter();
            parameter.setName(paramJson.getStr("name"));
            Object value = paramJson.get("default");
            if (value instanceof JSONObject) {
                parameter.setRefType(RefType.REF);
                JSONObject refValue = (JSONObject) value;
                parameter.setRefValue(refValue.getJSONArray("content").stream().map(Object::toString).collect(Collectors.toList()));
            } else {
                parameter.setDefaultValueString(paramJson.getStr("default"));
                parameter.setRefType(RefType.CONSTANT);
            }
            if (StrUtil.isBlank(parameter.getName())) {
                parameter.setName(entry.getKey());
            }
            parameter.setIsPropertyRequired(Optional.ofNullable(paramJson.getBool("isPropertyRequired")).orElse(false));
            parameter.setType(DataType.ofValue(paramJson.getStr("type")));
            parameters.add(parameter);
        }

        return parameters;
    }

    public static List<Parameter> parseStartNodeParameters(JSONObject nodeJSONObject) {
        List<Parameter> parameters = new ArrayList<>();

        for (Map.Entry<String, Object> entry : nodeJSONObject.entrySet()) {
            JSONObject paramJson = (JSONObject) entry.getValue();

            Parameter parameter = new Parameter();
            parameter.setName(paramJson.getStr("name"));
            parameter.setRefType(RefType.REF);
            parameter.setRefValue(Arrays.asList(paramJson.getStr("name")));
            parameter.setDefaultValueString(paramJson.getStr("default"));
            parameter.setIsPropertyRequired(Optional.ofNullable(paramJson.getBool("isPropertyRequired")).orElse(false));
            parameter.setType(DataType.ofValue(paramJson.getStr("type")));
            parameters.add(parameter);
        }

        return parameters;
    }
}
