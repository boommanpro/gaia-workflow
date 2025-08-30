package cn.boommanpro.gaia.workflow.node;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.model.Chain;
import cn.boommanpro.gaia.workflow.model.ChainNode;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * 分支节点测试类
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/05/17
 */
@Slf4j
public class BranchesNodeTest {

    private GaiaWorkflow gaiaWorkflow;
    private Chain chain;
    private String nodeId = "condition_lT8aG";

    @Before
    public void setUp() {
        // 加载branches.json文件
        String content = IoUtil.read(new ClassPathResource("branches.json").getStream(), StandardCharsets.UTF_8);
        gaiaWorkflow = new GaiaWorkflow(content);
        chain = gaiaWorkflow.toChain();
    }

    @Test
    public void testParseBranchesNode() {
        // 验证是否成功解析了branches节点
        ChainNode node = findNodeById(nodeId);
        Assert.assertNotNull("分支节点应该被成功解析", node);
        Assert.assertTrue("节点应该是BranchesNode类型", node instanceof BranchesNode);

        BranchesNode branchesNode = (BranchesNode) node;
        Assert.assertEquals("应该有4个分支", 4, branchesNode.getBranches().size());

        // 验证第一个分支的属性
        Assert.assertEquals("分支1的ID不正确", "branch_IF1cn", branchesNode.getBranches().get(0).getId());
        Assert.assertEquals("分支1的标题不正确", "Branch 1", branchesNode.getBranches().get(0).getTitle());
        Assert.assertEquals("分支1的逻辑不正确", "and", branchesNode.getBranches().get(0).getLogic());

        // 验证第一个分支的条件数量
        Assert.assertEquals("分支1应该有2个条件", 2, branchesNode.getBranches().get(0).getConditions().size());

        // 可以根据需要添加更多的验证
    }

    @Test
    public void testExecuteEmptyQuery() {
        // 测试当query为空时的分支条件
        Map<String, Object> params = new HashMap<>();
        params.put("start_0.query", ""); // query为空
        params.put("start_0.enable", false); // enable为false
        params.put("start_0.array_obj", "test"); // array_obj为test

        Map<String, Object> result = chain.executeForResult(params);

        // 在当前实现中，executeForResult可能不会直接返回分支结果
        // 我们需要从chain的memory中获取结果
        Map<String, Object> memory = chain.getMemory();

        // 验证分支1的结果：第一个条件满足（query为空），第二个条件表达式为空，应返回false
        Assert.assertFalse("分支1应该返回false", (Boolean) memory.get("condition_lT8aG.branch_IF1cn"));

        // 验证分支2的结果：enable为false，应返回false
        Assert.assertFalse("分支2应该返回false", (Boolean) memory.get("condition_lT8aG.branch_nsF9D"));

        // 验证分支3的结果：比较array_obj等于自身，应返回true
        Assert.assertTrue("分支3应该返回true", (Boolean) memory.get("condition_lT8aG.branch_nc7lp"));

        // 验证分支4的结果：query不等于"123"且enable为false，应返回false
        Assert.assertFalse("分支4应该返回false", (Boolean) memory.get("condition_lT8aG.branch_KQwWL"));
    }

    @Test
    public void testExecuteQueryEquals123() {
        // 测试当query等于"123"时的分支条件
        Map<String, Object> params = new HashMap<>();
        params.put("start_0.query", "123"); // query等于"123"
        params.put("start_0.enable", false); // enable为false
        params.put("start_0.array_obj", "test"); // array_obj为test

        Map<String, Object> result = chain.executeForResult(params);

        // 获取chain的memory
        Map<String, Object> memory = chain.getMemory();

        // 验证分支1的结果：query不为空，应返回false
        Assert.assertFalse("分支1应该返回false", (Boolean) memory.get("condition_lT8aG.branch_IF1cn"));

        // 验证分支2的结果：enable为false，应返回false
        Assert.assertFalse("分支2应该返回false", (Boolean) memory.get("condition_lT8aG.branch_nsF9D"));

        // 验证分支3的结果：比较array_obj等于自身，应返回true
        Assert.assertTrue("分支3应该返回true", (Boolean) memory.get("condition_lT8aG.branch_nc7lp"));

        // 验证分支4的结果：query等于"123"，OR逻辑，应返回true
        Assert.assertTrue("分支4应该返回true", (Boolean) memory.get("condition_lT8aG.branch_KQwWL"));
    }

    @Test
    public void testExecuteEnableTrue() {
        // 测试当enable为true时的分支条件
        Map<String, Object> params = new HashMap<>();
        params.put("start_0.query", "test"); // query不为空
        params.put("start_0.enable", true); // enable为true
        params.put("start_0.array_obj", "test"); // array_obj为test

        Map<String, Object> result = chain.executeForResult(params);

        // 获取chain的memory
        Map<String, Object> memory = chain.getMemory();

        // 验证分支1的结果：query不为空，应返回false
        Assert.assertFalse("分支1应该返回false", (Boolean) memory.get("condition_lT8aG.branch_IF1cn"));

        // 验证分支2的结果：enable为true，应返回true
        Assert.assertTrue("分支2应该返回true", (Boolean) memory.get("condition_lT8aG.branch_nsF9D"));

        // 验证分支3的结果：比较array_obj等于自身，应返回true
        Assert.assertTrue("分支3应该返回true", (Boolean) memory.get("condition_lT8aG.branch_nc7lp"));

        // 验证分支4的结果：enable为true，OR逻辑，应返回true
        Assert.assertTrue("分支4应该返回true", (Boolean) memory.get("condition_lT8aG.branch_KQwWL"));
    }

    private ChainNode findNodeById(String nodeId) {
        for (ChainNode node : chain.getNodes()) {
            if (nodeId.equals(node.getId())) {
                return node;
            }
        }
        return null;
    }
}
