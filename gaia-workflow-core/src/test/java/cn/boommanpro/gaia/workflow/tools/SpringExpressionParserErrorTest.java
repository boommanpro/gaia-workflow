package cn.boommanpro.gaia.workflow.tools;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * 测试原始错误场景：EL1008E: Property or field 'llm_kMoRm' cannot be found on object of type 'java.util.HashMap'
 */
public class SpringExpressionParserErrorTest {

    @Test
    public void testOriginalErrorScenario() {
        Map<String, Object> testData = new HashMap<>();
        testData.put("existingField", "someValue");

        SpringExpressionParser parser = SpringExpressionParser.getInstance();

        // 这个表达式在原始版本中会抛出 EL1008E 错误
        Object result = parser.getValue("llm_kMoRm", testData);

        // 现在应该返回 null 而不是抛出异常
        assertNull("不存在的属性 'llm_kMoRm' 应该返回 null", result);
    }

    @Test
    public void testComplexExpressionWithMissingProperties() {
        Map<String, Object> testData = new HashMap<>();
        testData.put("name", "test");
        testData.put("age", 25);

        SpringExpressionParser parser = SpringExpressionParser.getInstance();

        // 测试包含多个不存在属性的表达式
        String template = "${name} - ${llm_kMoRm ?: 'defaultLLM'} - ${age} - ${missingField ?: 'defaultMissing'}";
        String result = parser.getTemplateStringValue(template, testData);

        assertEquals("test - defaultLLM - 25 - defaultMissing", result);
    }

@Test
    public void testChainedPropertyAccessWithMissingIntermediate() {
        Map<String, Object> testData = new HashMap<>();
        Map<String, Object> existingNested = new HashMap<>();
        existingNested.put("innerProp", "innerValue");
        testData.put("existingNested", existingNested);

        SpringExpressionParser parser = SpringExpressionParser.getInstance();

        // 测试链式属性访问，中间路径不存在
        Object result = parser.getValue("missingNested?.innerProp", testData);
        assertNull("中间路径不存在时应该返回 null", result);

        // 测试链式属性访问，最终属性不存在
        result = parser.getValue("existingNested?.missingInnerProp", testData);
        assertNull("最终属性不存在时应该返回 null", result);
    }
}

