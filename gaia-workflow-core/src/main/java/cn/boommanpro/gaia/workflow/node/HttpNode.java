package cn.boommanpro.gaia.workflow.node;

import cn.boommanpro.gaia.workflow.model.Chain;
import cn.boommanpro.gaia.workflow.param.Parameter;
import cn.boommanpro.gaia.workflow.param.RefType;
import cn.boommanpro.gaia.workflow.tools.SpringExpressionParser;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.Method;
import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * HTTP请求节点，用于发起HTTP请求
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class HttpNode extends BaseNode {

    // API配置
    private ApiConfig api;

    // 请求头
    private Map<String, Object> headersValues = new HashMap<>();

    // 查询参数
    private Map<String, Object> paramsValues = new HashMap<>();

    // 请求体配置
    private BodyConfig body;

    // 超时配置
    private TimeoutConfig timeout;

    @Override
    public Map<String, Object> execute(Chain chain) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 构建HTTP请求
            HttpRequest request = buildHttpRequest(chain);

            // 设置超时
            if (timeout != null) {
                request.timeout(timeout.getTimeout());
            }

            // 发起请求
            HttpResponse response = request.execute();

            // 构建结果
            result.put("body", response.body());
            result.put("headers", response.headers());
            result.put("statusCode", response.getStatus());

        } catch (Exception e) {
            log.error("HTTP请求执行失败", e);
            result.put("error", e.getMessage());
        }

        return result;
    }

    /**
     * 构建HTTP请求
     * @param chain 工作流链
     * @return HttpRequest对象
     */
    private HttpRequest buildHttpRequest(Chain chain) {
        // 解析URL
        String url = parseUrl(api.getUrl(), chain);

        // 创建请求
        HttpRequest request = new HttpRequest(url);
        request.setMethod(Method.valueOf(api.getMethod()));

        // 设置请求头
        Map<String, String> headers = parseHeaders(chain);
        request.addHeaders(headers);

        // 设置查询参数
        Map<String, Object> params = parseParams(chain);
        request.form(params);

        // 设置请求体
        if (body != null && !"none".equals(body.getBodyType())) {
            String bodyContent = parseBody(chain);
            if (StrUtil.isNotBlank(bodyContent)) {
                request.body(bodyContent);
            }
        }

        return request;
    }

    /**
     * 解析URL
     * @param urlValue URL配置
     * @param chain 工作流链
     * @return 解析后的URL
     */
    private String parseUrl(Object urlValue, Chain chain) {
        if (urlValue instanceof Map) {
            Map<String, Object> urlMap = (Map<String, Object>) urlValue;
            String type = (String) urlMap.get("type");
            Object content = urlMap.get("content");

            if ("template".equals(type) && content instanceof String) {
                // 使用简单模板解析
                return parseTemplate((String) content, chain.getMemory());
            }
        } else if (urlValue instanceof String) {
            return (String) urlValue;
        }
        return "";
    }

    /**
     * 解析请求头
     * @param chain 工作流链
     * @return 解析后的请求头
     */
    private Map<String, String> parseHeaders(Chain chain) {
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : headersValues.entrySet()) {
            String key = entry.getKey();
            Object valueObj = entry.getValue();

            String value = parseValue(valueObj, chain);
            if (value != null) {
                result.put(key, value);
            }
        }
        return result;
    }

    /**
     * 解析查询参数
     * @param chain 工作流链
     * @return 解析后的查询参数
     */
    private Map<String, Object> parseParams(Chain chain) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : paramsValues.entrySet()) {
            String key = entry.getKey();
            Object valueObj = entry.getValue();

            String value = parseValue(valueObj, chain);
            if (value != null) {
                result.put(key, value);
            }
        }
        return result;
    }

    /**
     * 解析请求体
     * @param chain 工作流链
     * @return 解析后的请求体
     */
    private String parseBody(Chain chain) {
        if (body == null) {
            return "";
        }

        if ("JSON".equals(body.getBodyType()) && body.getJson() != null) {
            Object jsonValue = body.getJson();
            if (jsonValue instanceof Map) {
                Map<String, Object> jsonMap = (Map<String, Object>) jsonValue;
                String type = (String) jsonMap.get("type");
                Object content = jsonMap.get("content");

                if ("template".equals(type) && content instanceof String) {
                    // 使用简单模板解析
                    return parseTemplate((String) content, chain.getMemory());
                }
            }
        }

        return "";
    }

    /**
     * 解析值（支持常量和引用）
     * @param valueObj 值对象
     * @param chain 工作流链
     * @return 解析后的字符串值
     */
    private String parseValue(Object valueObj, Chain chain) {
        if (valueObj instanceof Map) {
            Map<String, Object> valueMap = (Map<String, Object>) valueObj;
            String type = (String) valueMap.get("type");
            Object content = valueMap.get("content");

            if (RefType.CONSTANT.name().toLowerCase().equals(type)) {
                return content != null ? content.toString() : null;
            } else if (RefType.REF.name().toLowerCase().equals(type) && content instanceof List) {
                List<?> refPath = (List<?>) content;
                if (refPath.size() >= 2) {
                    String nodeId = refPath.get(0).toString();
                    String paramName = refPath.get(1).toString();
                    Object nodeResult = chain.getMemory().get(nodeId);
                    if (nodeResult instanceof Map) {
                        Object value = ((Map<?, ?>) nodeResult).get(paramName);
                        return value != null ? value.toString() : null;
                    }
                }
            }
        }
        return null;
    }

    /**
     * 简单模板解析方法
     * @param template 模板字符串
     * @param context 上下文数据
     * @return 解析后的字符串
     */
    private String parseTemplate(String template, Map<String, Object> context) {
        return SpringExpressionParser.getInstance().getVueStringValue(template, context);
    }

    @Override
    public List<Parameter> getOutputParameters() {
        List<Parameter> parameters = new ArrayList<>();

        Parameter bodyParam = new Parameter();
        bodyParam.setName("body");
        bodyParam.setType(cn.boommanpro.gaia.workflow.param.DataType.String);
        parameters.add(bodyParam);

        Parameter headersParam = new Parameter();
        headersParam.setName("headers");
        headersParam.setType(cn.boommanpro.gaia.workflow.param.DataType.Object);
        parameters.add(headersParam);

        Parameter statusCodeParam = new Parameter();
        statusCodeParam.setName("statusCode");
        statusCodeParam.setType(cn.boommanpro.gaia.workflow.param.DataType.Integer);
        parameters.add(statusCodeParam);

        return parameters;
    }

    /**
     * API配置
     */
    @Data
    public static class ApiConfig {
        private String method;
        private Object url;
    }

    /**
     * 请求体配置
     */
    @Data
    public static class BodyConfig {
        private String bodyType;
        private Object json;
    }

    /**
     * 超时配置
     */
    @Data
    public static class TimeoutConfig {
        private int timeout;
        private int retryTimes;
    }
}
