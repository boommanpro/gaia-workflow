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
 * VariableNode综合测试类
 */
public class VariableNodeComprehensiveTest {

    @Test
    public void testWorkflowWithVariableNode() {
        // 构建包含VariableNode的工作流JSON
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
        JSONObject aProperty = new JSONObject();
        aProperty.set("type", "string");
        aProperty.set("default", "Hello");
        startProperties.set("a", aProperty);
        
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
        
        // 添加Variable节点
        JSONObject variableNode = new JSONObject();
        variableNode.set("id", "variable_0");
        variableNode.set("type", "variable");
        
        JSONObject variableData = new JSONObject();
        variableData.set("title", "VariableNode");
        
        JSONArray assignArray = new JSONArray();
        JSONObject assignObj = new JSONObject();
        assignObj.set("operator", "declare");
        assignObj.set("left", "result");
        
        JSONObject rightObj = new JSONObject();
        rightObj.set("type", "ref");
        
        JSONArray contentArray = new JSONArray();
        contentArray.add("start_0");
        contentArray.add("a");
        rightObj.set("content", contentArray);
        assignObj.set("right", rightObj);
        
        assignArray.add(assignObj);
        variableData.set("assign", assignArray);
        variableNode.set("data", variableData);
        
        JSONObject variableMeta = new JSONObject();
        JSONObject variablePosition = new JSONObject();
        variablePosition.set("x", 180);
        variablePosition.set("y", 100);
        variableMeta.set("position", variablePosition);
        variableNode.set("meta", variableMeta);
        
        nodes.add(variableNode);
        
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
        resultContent.add("variable_0");
        resultContent.add("result");
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
        
        // Start -> Variable
        JSONObject edge1 = new JSONObject();
        edge1.set("sourceNodeID", "start_0");
        edge1.set("targetNodeID", "variable_0");
        edges.add(edge1);
        
        // Variable -> End
        JSONObject edge2 = new JSONObject();
        edge2.set("sourceNodeID", "variable_0");
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
        assertEquals("Hello", result.get("result"));
    }
    
    @Test
    public void testWorkflowWithVariableNodeReferenceLeftValue() {
        // 构建包含VariableNode的工作流JSON，其中VariableNode使用引用类型左值
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
        JSONObject aProperty = new JSONObject();
        aProperty.set("type", "string");
        aProperty.set("default", "InitialValue");
        startProperties.set("a", aProperty);
        
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
        
        // 添加Variable节点，使用引用类型左值修改start_0节点的结果
        JSONObject variableNode = new JSONObject();
        variableNode.set("id", "variable_0");
        variableNode.set("type", "variable");
        
        JSONObject variableData = new JSONObject();
        variableData.set("title", "VariableNode");
        
        JSONArray assignArray = new JSONArray();
        JSONObject assignObj = new JSONObject();
        assignObj.set("operator", "assign");
        
        // 设置引用类型的左值
        JSONObject leftObj = new JSONObject();
        leftObj.set("type", "ref");
        JSONArray leftContentArray = new JSONArray();
        leftContentArray.add("start_0");
        leftContentArray.add("a");
        leftObj.set("content", leftContentArray);
        assignObj.set("left", leftObj);
        
        // 设置右值
        JSONObject rightObj = new JSONObject();
        rightObj.set("type", "constant");
        rightObj.set("content", "ModifiedValue");
        assignObj.set("right", rightObj);
        
        assignArray.add(assignObj);
        variableData.set("assign", assignArray);
        variableNode.set("data", variableData);
        
        JSONObject variableMeta = new JSONObject();
        JSONObject variablePosition = new JSONObject();
        variablePosition.set("x", 180);
        variablePosition.set("y", 100);
        variableMeta.set("position", variablePosition);
        variableNode.set("meta", variableMeta);
        
        nodes.add(variableNode);
        
        // 添加结束节点，从start_0获取修改后的值
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
        resultContent.add("start_0");
        resultContent.add("a"); // 获取被修改的值
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
        
        // Start -> Variable
        JSONObject edge1 = new JSONObject();
        edge1.set("sourceNodeID", "start_0");
        edge1.set("targetNodeID", "variable_0");
        edges.add(edge1);
        
        // Variable -> End
        JSONObject edge2 = new JSONObject();
        edge2.set("sourceNodeID", "variable_0");
        edge2.set("targetNodeID", "end_0");
        edges.add(edge2);
        
        // 解析并执行工作流
        ChainParser parser = new ChainParser();
        GaiaWorkflow workflow = new GaiaWorkflow("{}");
        Chain chain = parser.parse(nodes, edges, workflow);
        
        // 执行工作流
        Map<String, Object> result = chain.executeForResult(new HashMap<>());
        
        // 验证结果 - 应该获取到被修改后的值
        assertNotNull(result);
        assertEquals("ModifiedValue", result.get("result"));
    }
}