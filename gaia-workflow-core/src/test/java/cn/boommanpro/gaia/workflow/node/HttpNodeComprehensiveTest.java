package cn.boommanpro.gaia.workflow.node;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.model.Chain;
import cn.boommanpro.gaia.workflow.parser.ChainParser;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * HttpNode综合测试类
 */
public class HttpNodeComprehensiveTest {

    @Test
    public void testWorkflowWithHttpNode() {
        // 构建包含HttpNode的工作流JSON
        JSONArray nodes = new JSONArray();
        
        // 添加开始节点
        JSONObject startNode = new JSONObject();
        startNode.set("id", "start_0");
        startNode.set("type", "start");
        
        JSONObject startData = new JSONObject();
        startData.set("title", "Start");
        
        JSONObject startOutputs = new JSONObject();
        startOutputs.set("type", "object");
        
        JSONObject startProperties = new JSONObject();
        JSONObject urlProperty = new JSONObject();
        urlProperty.set("type", "string");
        urlProperty.set("default", "https://httpbin.org/get");
        startProperties.set("url", urlProperty);
        startOutputs.set("properties", startProperties);
        startData.set("outputs", startOutputs);
        startNode.set("data", startData);
        
        JSONObject startMeta = new JSONObject();
        JSONObject startPosition = new JSONObject();
        startPosition.set("x", 180);
        startPosition.set("y", 0);
        startMeta.set("position", startPosition);
        startNode.set("meta", startMeta);
        
        nodes.add(startNode);
        
        // 添加HTTP节点
        JSONObject httpNode = new JSONObject();
        httpNode.set("id", "http_0");
        httpNode.set("type", "http");
        
        JSONObject httpData = new JSONObject();
        httpData.set("title", "HTTP_1");
        
        // 创建api配置
        JSONObject api = new JSONObject();
        api.set("method", "GET");
        JSONObject url = new JSONObject();
        url.set("type", "ref");
        JSONArray urlContent = new JSONArray();
        urlContent.add("start_0");
        urlContent.add("url");
        url.set("content", urlContent);
        api.set("url", url);
        httpData.set("api", api);
        
        // 创建headersValues
        JSONObject headersValues = new JSONObject();
        httpData.set("headersValues", headersValues);
        
        // 创建paramsValues
        JSONObject paramsValues = new JSONObject();
        httpData.set("paramsValues", paramsValues);
        
        // 创建body配置
        JSONObject body = new JSONObject();
        body.set("bodyType", "none");
        httpData.set("body", body);
        
        // 创建timeout配置
        JSONObject timeout = new JSONObject();
        timeout.set("timeout", 10000);
        timeout.set("retryTimes", 1);
        httpData.set("timeout", timeout);
        
        // 创建输出配置
        JSONObject outputs = new JSONObject();
        outputs.set("type", "object");
        JSONObject outputProperties = new JSONObject();
        JSONObject bodyProperty = new JSONObject();
        bodyProperty.set("type", "string");
        outputProperties.set("body", bodyProperty);
        JSONObject headersProperty = new JSONObject();
        headersProperty.set("type", "object");
        outputProperties.set("headers", headersProperty);
        JSONObject statusCodeProperty = new JSONObject();
        statusCodeProperty.set("type", "integer");
        outputProperties.set("statusCode", statusCodeProperty);
        outputs.set("properties", outputProperties);
        httpData.set("outputs", outputs);
        
        httpNode.set("data", httpData);
        
        JSONObject httpMeta = new JSONObject();
        JSONObject httpPosition = new JSONObject();
        httpPosition.set("x", 180);
        httpPosition.set("y", 100);
        httpMeta.set("position", httpPosition);
        httpNode.set("meta", httpMeta);
        
        nodes.add(httpNode);
        
        // 添加结束节点
        JSONObject endNode = new JSONObject();
        endNode.set("id", "end_0");
        endNode.set("type", "end");
        
        JSONObject endData = new JSONObject();
        endData.set("title", "End");
        
        JSONObject endInputs = new JSONObject();
        endInputs.set("type", "object");
        JSONObject endProperties = new JSONObject();
        JSONObject resultProperty = new JSONObject();
        resultProperty.set("type", "string");
        endProperties.set("result", resultProperty);
        endInputs.set("properties", endProperties);
        endData.set("inputs", endInputs);
        
        JSONObject endInputsValues = new JSONObject();
        JSONObject resultValue = new JSONObject();
        resultValue.set("type", "ref");
        JSONArray resultContent = new JSONArray();
        resultContent.add("http_0");
        resultContent.add("body");
        resultValue.set("content", resultContent);
        endInputsValues.set("result", resultValue);
        endData.set("inputsValues", endInputsValues);
        endNode.set("data", endData);
        
        JSONObject endMeta = new JSONObject();
        JSONObject endPosition = new JSONObject();
        endPosition.set("x", 180);
        endPosition.set("y", 200);
        endMeta.set("position", endPosition);
        endNode.set("meta", endMeta);
        
        nodes.add(endNode);
        
        // 添加边
        JSONArray edges = new JSONArray();
        
        // Start -> HTTP
        JSONObject edge1 = new JSONObject();
        edge1.set("sourceNodeID", "start_0");
        edge1.set("targetNodeID", "http_0");
        edges.add(edge1);
        
        // HTTP -> End
        JSONObject edge2 = new JSONObject();
        edge2.set("sourceNodeID", "http_0");
        edge2.set("targetNodeID", "end_0");
        edges.add(edge2);
        
        // 解析并执行工作流
        ChainParser parser = new ChainParser();
        GaiaWorkflow workflow = new GaiaWorkflow("{}");
        Chain chain = parser.parse(nodes, edges, workflow);
        
        // 执行工作流
        Map<String, Object> result = chain.executeForResult(new HashMap<>());
        
        // 验证结果
        assertNotNull(result);
        assertTrue(result.containsKey("result"));
    }
}