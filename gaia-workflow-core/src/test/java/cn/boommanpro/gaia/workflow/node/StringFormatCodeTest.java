package cn.boommanpro.gaia.workflow.node;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.model.Chain;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * StringFormatCode节点测试类
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 */
@Slf4j
public class StringFormatCodeTest {

    @Test
    public void testStringFormatCodeWithSpel() {
        // 创建一个简单的JSON工作流定义，包含StringFormatCode节点
        String workflowJson = IoUtil.read(new ClassPathResource("stringFormat.json").getStream(), StandardCharsets.UTF_8);

        GaiaWorkflow gaiaWorkflow = new GaiaWorkflow(workflowJson);
        Chain chain = gaiaWorkflow.toChain();

        // 执行工作流
        HashMap<String, Object> input = new HashMap<>();
        input.put("query", "Hello World");
        Map<String, Object> result = chain.executeForResult(input);

        // 验证结果
        Assert.assertEquals("Hello World!", result.get("output"));
    }



}
