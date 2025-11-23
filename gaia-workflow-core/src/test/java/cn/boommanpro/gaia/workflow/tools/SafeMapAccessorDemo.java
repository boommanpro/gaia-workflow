package cn.boommanpro.gaia.workflow.tools;

import java.util.HashMap;
import java.util.Map;

/**
 * 演示 SafeMapAccessor 解决 EL1008E 错误的效果
 */
public class SafeMapAccessorDemo {

    public static void main(String[] args) {
        SpringExpressionParser parser = SpringExpressionParser.getInstance();

        // 创建测试数据
        Map<String, Object> testData = new HashMap<>();
        testData.put("name", "张三");
        testData.put("age", 30);

        System.out.println("=== Spring EL 安全访问演示 ===");

        // 1. 测试存在属性
        Object nameValue = parser.getValue("name", testData);
        System.out.println("访问存在属性 'name': " + nameValue);

        // 2. 测试不存在的属性（之前会抛出 EL1008E 错误）
        Object missingValue = parser.getValue("llm_kMoRm", testData);
        System.out.println("访问不存在属性 'llm_kMoRm': " + missingValue);

        // 3. 测试模板字符串中的不存在的属性
        String templateResult = parser.getTemplateStringValue(
            "用户名：${name}，年龄：${age}，LLM结果：${llm_kMoRm ?: '默认LLM结果'}",
            testData
        );
        System.out.println("模板字符串结果：" + templateResult);

        // 4. 测试 Vue 风格的模板
        String vueResult = parser.getVueStringValue(
            "用户：{{name}}，LLM：{{llm_kMoRm ?: '默认值'}}",
            testData
        );
        System.out.println("Vue风格模板结果：" + vueResult);

        // 5. 测试安全导航操作符
        Object safeNavResult = parser.getValue("missingNested?.innerProp", testData);
        System.out.println("安全导航访问结果：" + safeNavResult);

        System.out.println("=== 演示完成，所有操作都没有抛出异常 ===");
    }
}

