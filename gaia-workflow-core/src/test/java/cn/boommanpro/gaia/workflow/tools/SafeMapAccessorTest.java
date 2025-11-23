package cn.boommanpro.gaia.workflow.tools;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * 测试 SafeMapAccessor 的行为
 */
public class SafeMapAccessorTest {

    @Test
    public void testSafeMapAccessorWithExistingProperty() {
        Map<String, Object> testData = new HashMap<>();
        testData.put("existingKey", "existingValue");

        SpringExpressionParser parser = SpringExpressionParser.getInstance();
        String result = parser.getValue("existingKey", testData);
        assertEquals("existingValue", result);
    }

    @Test
    public void testSafeMapAccessorWithNonExistingProperty() {
        Map<String, Object> testData = new HashMap<>();
        testData.put("existingKey", "existingValue");

        SpringExpressionParser parser = SpringExpressionParser.getInstance();
        Object result = parser.getValue("nonExistingKey", testData);
        assertNull("不存在的属性应该返回 null", result);
    }

    @Test
    public void testSafeMapAccessorWithNestedExpression() {
        Map<String, Object> testData = new HashMap<>();
        testData.put("existingKey", "existingValue");

        SpringExpressionParser parser = SpringExpressionParser.getInstance();
        // 测试嵌套表达式，其中部分路径不存在
        String result = parser.getTemplateStringValue("${nonExistingKey ?: 'defaultValue'}", testData);
        assertEquals("defaultValue", result);
    }

    @Test
    public void testSafeMapAccessorWithComplexExpression() {
        Map<String, Object> testData = new HashMap<>();
        testData.put("name", "test");

        SpringExpressionParser parser = SpringExpressionParser.getInstance();
        // 测试包含不存在属性的表达式
        String result = parser.getTemplateStringValue("${name} - ${nonExistent ?: 'default'}", testData);
        assertEquals("test - default", result);
    }
}

