package cn.boommanpro.gaia.workflow.node;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Loop工作流测试类
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/05/17
 */
public class LoopWorkflowTest {

    private GaiaWorkflow workflow;

    @Before
    public void setUp() {
        // 读取完整的工作流JSON文件
        String jsonContent = IoUtil.read(new ClassPathResource("loop-workflow.json").getStream(), StandardCharsets.UTF_8);
        workflow = new GaiaWorkflow(jsonContent);
    }

    @Test
    public void testCompleteLoopWorkflow() {
        // 准备输入数据
        Map<String, Object> inputs = new HashMap<>();
        ArrayList<Object> value = new ArrayList<>();
        value.add(1);
        value.add(2);
        value.add(3);
        inputs.put("array_obj", value);

        // 执行工作流
        Map<String, Object> result = workflow.run(inputs);

        // 验证结果
        Assert.assertNotNull("工作流执行结果不应为空", result);

        // 验证输出结构
        if (result.containsKey("end_0")) {
            Map<String, Object> endResult = (Map<String, Object>) result.get("end_0");
            Assert.assertNotNull("结束节点结果不应为空", endResult);

            // 验证循环输出
            if (endResult.containsKey("ai_loop_user")) {
                List<String> loopUsers = (List<String>) endResult.get("ai_loop_user");
                Assert.assertNotNull("循环用户列表不应为空", loopUsers);

                // 验证循环次数（应该等于array_obj的长度）
                // array_obj包含2个元素，所以应该有2个用户
                Assert.assertTrue("循环用户列表应该包含2个元素", loopUsers.size() >= 2);

                // 验证用户名格式
                for (String user : loopUsers) {
                    Assert.assertTrue("用户名应该以User_开头", user.startsWith("User_"));
                    Assert.assertTrue("用户名应该包含索引", user.contains("_"));
                }

                System.out.println("循环执行结果:");
                for (int i = 0; i < loopUsers.size(); i++) {
                    System.out.println("  用户 " + i + ": " + loopUsers.get(i));
                }
            }
        }

        System.out.println("完整工作流执行成功！");
    }

    @Test
    public void testWorkflowStructure() {
        // 验证工作流结构
        Assert.assertNotNull("工作流不应为空", workflow);

        // 转换为链进行验证
        cn.boommanpro.gaia.workflow.model.Chain chain = workflow.toChain();
        Assert.assertNotNull("链不应为空", chain);
        Assert.assertNotNull("节点列表不应为空", chain.getNodes());
        Assert.assertNotNull("边列表不应为空", chain.getEdges());

        // 验证节点数量
        Assert.assertTrue("应该至少有3个节点", chain.getNodes().size() >= 3);

        // 验证循环节点存在
        boolean hasLoopNode = chain.getNodes().stream()
            .anyMatch(node -> "loop_TC60x".equals(node.getId()));
        Assert.assertTrue("应该包含循环节点", hasLoopNode);

        System.out.println("工作流结构验证通过，节点数量: " + chain.getNodes().size());
    }
}

