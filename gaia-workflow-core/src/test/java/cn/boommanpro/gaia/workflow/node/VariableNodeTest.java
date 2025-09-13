package cn.boommanpro.gaia.workflow.node;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.model.Chain;
import cn.boommanpro.gaia.workflow.param.DataType;
import cn.boommanpro.gaia.workflow.param.Parameter;
import cn.boommanpro.gaia.workflow.param.RefType;
import cn.boommanpro.gaia.workflow.parser.VariableNodeParser;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

/**
 * VariableNode测试类
 */
public class VariableNodeTest {

    @Test
    public void testVariableNodeExecuteWithConstant() {
        // 创建VariableNode
        VariableNode variableNode = new VariableNode();
        
        // 创建赋值操作
        VariableNode.VariableAssign assign = new VariableNode.VariableAssign();
        assign.setOperator("declare");
        assign.setLeft("testVar");
        
        // 创建常量值
        VariableNode.VariableValue value = new VariableNode.VariableValue();
        value.setType(RefType.CONSTANT);
        value.setContent("testValue");
        assign.setRight(value);
        
        // 添加赋值操作到节点
        List<VariableNode.VariableAssign> assigns = new ArrayList<>();
        assigns.add(assign);
        variableNode.setAssigns(assigns);
        
        // 创建Chain并执行
        Chain chain = new Chain();
        Map<String, Object> result = variableNode.execute(chain);
        
        // 验证结果
        assertEquals(1, result.size());
        assertEquals("testValue", result.get("testVar"));
    }
    
    @Test
    public void testVariableNodeExecuteWithReference() {
        // 创建VariableNode
        VariableNode variableNode = new VariableNode();
        
        // 创建赋值操作
        VariableNode.VariableAssign assign = new VariableNode.VariableAssign();
        assign.setOperator("declare");
        assign.setLeft("resultVar");
        
        // 创建引用值
        VariableNode.VariableValue value = new VariableNode.VariableValue();
        value.setType(RefType.REF);
        List<String> refContent = Arrays.asList("node1", "param1");
        value.setContent(refContent);
        assign.setRight(value);
        
        // 添加赋值操作到节点
        List<VariableNode.VariableAssign> assigns = new ArrayList<>();
        assigns.add(assign);
        variableNode.setAssigns(assigns);
        
        // 创建Chain并设置内存数据
        Chain chain = new Chain();
        Map<String, Object> node1Result = new HashMap<>();
        node1Result.put("param1", "referencedValue");
        chain.getMemory().put("node1", node1Result);
        
        // 执行节点
        Map<String, Object> result = variableNode.execute(chain);
        
        // 验证结果
        assertEquals(1, result.size());
        assertEquals("referencedValue", result.get("resultVar"));
    }
    
    @Test
    public void testVariableNodeExecuteWithReferenceLeftValue() {
        // 创建VariableNode
        VariableNode variableNode = new VariableNode();
        
        // 创建赋值操作，左值为引用类型
        VariableNode.VariableAssign assign = new VariableNode.VariableAssign();
        assign.setOperator("assign");
        assign.setLeftType(RefType.REF);
        List<String> leftRefContent = Arrays.asList("targetNode", "targetParam");
        assign.setLeftContent(leftRefContent);
        
        // 创建右值
        VariableNode.VariableValue value = new VariableNode.VariableValue();
        value.setType(RefType.CONSTANT);
        value.setContent("assignedValue");
        assign.setRight(value);
        
        // 添加赋值操作到节点
        List<VariableNode.VariableAssign> assigns = new ArrayList<>();
        assigns.add(assign);
        variableNode.setAssigns(assigns);
        
        // 创建Chain并执行
        Chain chain = new Chain();
        
        // 执行节点
        variableNode.execute(chain);
        
        // 验证chain memory是否被正确更新
        Map<String, Object> targetNodeResult = (Map<String, Object>) chain.getMemory().get("targetNode");
        assertNotNull("targetNode should exist in memory", targetNodeResult);
        assertEquals("assignedValue", targetNodeResult.get("targetParam"));
    }
    
    @Test
    public void testVariableNodeParser() {
        // 创建JSON对象模拟前端数据
        JSONObject nodeJson = new JSONObject();
        nodeJson.set("id", "variable_1");
        nodeJson.set("type", "variable");
        
        JSONObject data = new JSONObject();
        data.set("title", "TestVariable");
        
        // 创建assign数组
        List<JSONObject> assignList = new ArrayList<>();
        JSONObject assignObj = new JSONObject();
        assignObj.set("operator", "declare");
        assignObj.set("left", "sum");
        
        JSONObject rightObj = new JSONObject();
        rightObj.set("type", "constant");
        rightObj.set("content", 100);
        assignObj.set("right", rightObj);
        
        assignList.add(assignObj);
        data.set("assign", assignList);
        nodeJson.set("data", data);
        
        // 创建解析器并解析
        VariableNodeParser parser = new VariableNodeParser();
        GaiaWorkflow workflow = new GaiaWorkflow("{}");
        VariableNode variableNode = parser.buildInstance(nodeJson, workflow);
        
        // 验证解析结果
        assertNotNull(variableNode);
        assertEquals(1, variableNode.getAssigns().size());
        
        VariableNode.VariableAssign assign = variableNode.getAssigns().get(0);
        assertEquals("declare", assign.getOperator());
        assertEquals("sum", assign.getLeft());
        assertNotNull(assign.getRight());
        assertEquals(RefType.CONSTANT, assign.getRight().getType());
        assertEquals(100, assign.getRight().getContent());
    }
    
    @Test
    public void testVariableNodeParserWithReferenceLeftValue() {
        // 创建JSON对象模拟前端数据，包含引用类型的左值
        JSONObject nodeJson = new JSONObject();
        nodeJson.set("id", "variable_1");
        nodeJson.set("type", "variable");
        
        JSONObject data = new JSONObject();
        data.set("title", "TestVariable");
        
        // 创建assign数组
        List<JSONObject> assignList = new ArrayList<>();
        JSONObject assignObj = new JSONObject();
        assignObj.set("operator", "assign");
        
        // 创建引用类型的左值
        JSONObject leftObj = new JSONObject();
        leftObj.set("type", "ref");
        JSONArray leftContent = new JSONArray();
        leftContent.add("targetNode");
        leftContent.add("targetParam");
        leftObj.set("content", leftContent);
        assignObj.set("left", leftObj);
        
        JSONObject rightObj = new JSONObject();
        rightObj.set("type", "constant");
        rightObj.set("content", "testValue");
        assignObj.set("right", rightObj);
        
        assignList.add(assignObj);
        data.set("assign", assignList);
        nodeJson.set("data", data);
        
        // 创建解析器并解析
        VariableNodeParser parser = new VariableNodeParser();
        GaiaWorkflow workflow = new GaiaWorkflow("{}");
        VariableNode variableNode = parser.buildInstance(nodeJson, workflow);
        
        // 验证解析结果
        assertNotNull(variableNode);
        assertEquals(1, variableNode.getAssigns().size());
        
        VariableNode.VariableAssign assign = variableNode.getAssigns().get(0);
        assertEquals("assign", assign.getOperator());
        assertEquals(RefType.REF, assign.getLeftType());
        assertNotNull(assign.getLeftContent());
        assertTrue(assign.getLeftContent() instanceof List);
        assertNotNull(assign.getRight());
        assertEquals(RefType.CONSTANT, assign.getRight().getType());
        assertEquals("testValue", assign.getRight().getContent());
    }
    
    @Test
    public void testVariableNodeOutputParameters() {
        // 创建VariableNode
        VariableNode variableNode = new VariableNode();
        
        // 创建赋值操作 - 普通变量名左值
        VariableNode.VariableAssign assign1 = new VariableNode.VariableAssign();
        assign1.setOperator("declare");
        assign1.setLeft("outputVar");
        
        VariableNode.VariableValue value1 = new VariableNode.VariableValue();
        value1.setType(RefType.CONSTANT);
        value1.setContent("someValue");
        assign1.setRight(value1);
        
        // 创建赋值操作 - 引用类型左值
        VariableNode.VariableAssign assign2 = new VariableNode.VariableAssign();
        assign2.setOperator("assign");
        assign2.setLeftType(RefType.REF);
        List<String> leftRefContent = Arrays.asList("targetNode", "targetParam");
        assign2.setLeftContent(leftRefContent);
        
        VariableNode.VariableValue value2 = new VariableNode.VariableValue();
        value2.setType(RefType.CONSTANT);
        value2.setContent("anotherValue");
        assign2.setRight(value2);
        
        List<VariableNode.VariableAssign> assigns = new ArrayList<>();
        assigns.add(assign1);
        assigns.add(assign2);
        variableNode.setAssigns(assigns);
        
        // 获取输出参数
        List<Parameter> outputParams = variableNode.getOutputParameters();
        
        // 验证输出参数 - 只有普通变量名左值才会出现在输出参数中
        assertEquals(1, outputParams.size());
        Parameter param = outputParams.get(0);
        assertEquals("outputVar", param.getName());
        assertEquals(DataType.String, param.getType());
    }
}