package cn.boommanpro.gaia.workflow.node;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.model.Chain;
import cn.boommanpro.gaia.workflow.param.DataType;
import cn.boommanpro.gaia.workflow.param.Parameter;
import cn.boommanpro.gaia.workflow.parser.HttpNodeParser;
import cn.hutool.json.JSONObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * HttpNode测试类
 */
public class HttpNodeTest {

    @Test
    public void testHttpNodeExecute() {
        // 创建HttpNode
        HttpNode httpNode = new HttpNode();
        
        // 设置API配置
        HttpNode.ApiConfig apiConfig = new HttpNode.ApiConfig();
        apiConfig.setMethod("GET");
        apiConfig.setUrl("https://httpbin.org/get");
        httpNode.setApi(apiConfig);
        
        // 设置超时配置
        HttpNode.TimeoutConfig timeoutConfig = new HttpNode.TimeoutConfig();
        timeoutConfig.setTimeout(10000);
        timeoutConfig.setRetryTimes(1);
        httpNode.setTimeout(timeoutConfig);
        
        // 创建Chain并执行
        Chain chain = new Chain();
        Map<String, Object> result = httpNode.execute(chain);
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.containsKey("body"));
        assertTrue(result.containsKey("headers"));
        assertTrue(result.containsKey("statusCode"));
        assertEquals(200, result.get("statusCode"));
    }
    
    @Test
    public void testHttpNodeParser() {
        // 创建JSON对象模拟前端数据
        JSONObject nodeJson = new JSONObject();
        nodeJson.set("id", "http_1");
        nodeJson.set("type", "http");
        
        JSONObject data = new JSONObject();
        data.set("title", "HTTP_1");
        
        // 创建api配置
        JSONObject api = new JSONObject();
        api.set("method", "GET");
        JSONObject url = new JSONObject();
        url.set("type", "template");
        url.set("content", "https://httpbin.org/get");
        api.set("url", url);
        data.set("api", api);
        
        // 创建headersValues
        JSONObject headersValues = new JSONObject();
        JSONObject headerValue = new JSONObject();
        headerValue.set("type", "constant");
        headerValue.set("content", "test-value");
        headersValues.set("test-header", headerValue);
        data.set("headersValues", headersValues);
        
        // 创建paramsValues
        JSONObject paramsValues = new JSONObject();
        JSONObject paramValue = new JSONObject();
        paramValue.set("type", "constant");
        paramValue.set("content", "test-param");
        paramsValues.set("test-param", paramValue);
        data.set("paramsValues", paramsValues);
        
        // 创建body配置
        JSONObject body = new JSONObject();
        body.set("bodyType", "JSON");
        JSONObject json = new JSONObject();
        json.set("type", "template");
        json.set("content", "{\"key\": \"value\"}");
        body.set("json", json);
        data.set("body", body);
        
        // 创建timeout配置
        JSONObject timeout = new JSONObject();
        timeout.set("timeout", 10000);
        timeout.set("retryTimes", 1);
        data.set("timeout", timeout);
        
        nodeJson.set("data", data);
        
        // 创建解析器并解析
        HttpNodeParser parser = new HttpNodeParser();
        GaiaWorkflow workflow = new GaiaWorkflow("{}");
        HttpNode httpNode = parser.buildInstance(nodeJson, workflow);
        
        // 验证解析结果
        assertNotNull(httpNode);
        assertNotNull(httpNode.getApi());
        assertEquals("GET", httpNode.getApi().getMethod());
        assertNotNull(httpNode.getHeadersValues());
        assertFalse(httpNode.getHeadersValues().isEmpty());
        assertNotNull(httpNode.getParamsValues());
        assertFalse(httpNode.getParamsValues().isEmpty());
        assertNotNull(httpNode.getBody());
        assertEquals("JSON", httpNode.getBody().getBodyType());
        assertNotNull(httpNode.getTimeout());
        assertEquals(10000, httpNode.getTimeout().getTimeout());
        assertEquals(1, httpNode.getTimeout().getRetryTimes());
    }
    
    @Test
    public void testHttpNodeOutputParameters() {
        // 创建HttpNode
        HttpNode httpNode = new HttpNode();
        
        // 获取输出参数
        List<Parameter> outputParams = httpNode.getOutputParameters();
        
        // 验证输出参数
        assertEquals(3, outputParams.size());
        
        Parameter bodyParam = outputParams.get(0);
        assertEquals("body", bodyParam.getName());
        assertEquals(DataType.String, bodyParam.getType());
        
        Parameter headersParam = outputParams.get(1);
        assertEquals("headers", headersParam.getName());
        assertEquals(DataType.Object, headersParam.getType());
        
        Parameter statusCodeParam = outputParams.get(2);
        assertEquals("statusCode", statusCodeParam.getName());
        assertEquals(DataType.Integer, statusCodeParam.getType());
    }
    
    @Test
    public void testHttpNodeWithReferenceValues() {
        // 创建HttpNode
        HttpNode httpNode = new HttpNode();
        
        // 设置API配置
        HttpNode.ApiConfig apiConfig = new HttpNode.ApiConfig();
        apiConfig.setMethod("GET");
        apiConfig.setUrl("https://httpbin.org/get");
        httpNode.setApi(apiConfig);
        
        // 设置引用类型的headersValues
        Map<String, Object> headersValues = new HashMap<>();
        JSONObject headerValue = new JSONObject();
        headerValue.set("type", "ref");
        headerValue.set("content", new String[]{"start_0", "headerValue"});
        headersValues.put("dynamic-header", headerValue);
        httpNode.setHeadersValues(headersValues);
        
        // 设置引用类型的paramsValues
        Map<String, Object> paramsValues = new HashMap<>();
        JSONObject paramValue = new JSONObject();
        paramValue.set("type", "ref");
        paramValue.set("content", new String[]{"start_0", "paramValue"});
        paramsValues.put("dynamic-param", paramValue);
        httpNode.setParamsValues(paramsValues);
        
        // 创建Chain并设置内存数据
        Chain chain = new Chain();
        Map<String, Object> startResult = new HashMap<>();
        startResult.put("headerValue", "dynamic-header-value");
        startResult.put("paramValue", "dynamic-param-value");
        chain.getMemory().put("start_0", startResult);
        
        // 执行节点
        Map<String, Object> result = httpNode.execute(chain);
        
        // 验证基本结构（不验证具体请求结果，因为可能因网络问题失败）
        assertNotNull(result);
    }
}