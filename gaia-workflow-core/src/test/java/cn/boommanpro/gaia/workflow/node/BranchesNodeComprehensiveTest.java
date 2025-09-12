package cn.boommanpro.gaia.workflow.node;

import cn.boommanpro.gaia.workflow.model.Chain;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

/**
 * 分支节点综合测试类 - 验证新增的操作符和比较功能
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 */
@Slf4j
public class BranchesNodeComprehensiveTest {

    private BranchesNode branchesNode;
    private Chain chain;

    @Before
    public void setUp() {
        branchesNode = new BranchesNode();
        branchesNode.setId("test_branches_node");

        // 创建测试用的chain
        chain = new Chain();
        chain.setId("test_chain");
        // 初始化memory
        chain.getMemory().clear();
    }

    @Test
    public void testIsFalseOperator() {
        // 创建一个使用is_false操作符的分支
        BranchesNode.Branch branch = new BranchesNode.Branch();
        branch.setId("test_branch");
        branch.setTitle("Test Branch");
        branch.setLogic("and");

        BranchesNode.Condition condition = new BranchesNode.Condition();
        condition.setKey("condition1");

        BranchesNode.Expression expression = new BranchesNode.Expression();
        expression.setOperator("is_false");

        BranchesNode.ExpressionPart leftPart = new BranchesNode.ExpressionPart();
        leftPart.setType("ref");
        leftPart.setContent(Arrays.asList("test_ref"));

        expression.setLeft(leftPart);
        condition.setValue(expression);

        branch.getConditions().add(condition);
        branchesNode.getBranches().add(branch);

        // 测试is_false操作符 - 值为false时应返回true
        chain.getMemory().put("test_ref", false);
        boolean result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertTrue("当值为false时，is_false操作符应返回true", result);

        // 测试is_false操作符 - 值为true时应返回false
        chain.getMemory().put("test_ref", true);
        result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertFalse("当值为true时，is_false操作符应返回false", result);

        // 测试is_false操作符 - 值为null时应返回false
        chain.getMemory().remove("test_ref");
        result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertFalse("当值为null时，is_false操作符应返回false", result);
    }

    @Test
    public void testNotEqualOperator() {
        // 创建一个使用!=操作符的分支
        BranchesNode.Branch branch = new BranchesNode.Branch();
        branch.setId("test_branch_ne");
        branch.setTitle("Test Branch NE");
        branch.setLogic("and");

        BranchesNode.Condition condition = new BranchesNode.Condition();
        condition.setKey("condition1");

        BranchesNode.Expression expression = new BranchesNode.Expression();
        expression.setOperator("neq");

        BranchesNode.ExpressionPart leftPart = new BranchesNode.ExpressionPart();
        leftPart.setType("ref");
        leftPart.setContent(Arrays.asList("left_value"));

        BranchesNode.ExpressionPart rightPart = new BranchesNode.ExpressionPart();
        rightPart.setType("ref");
        rightPart.setContent(Arrays.asList("right_value"));

        expression.setLeft(leftPart);
        expression.setRight(rightPart);
        condition.setValue(expression);

        branch.getConditions().add(condition);
        branchesNode.getBranches().add(branch);

        // 测试!=操作符 - 不同值应返回true
        chain.getMemory().put("left_value", "value1");
        chain.getMemory().put("right_value", "value2");
        boolean result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertTrue("当两个值不相等时，!=操作符应返回true", result);

        // 测试!=操作符 - 相同值应返回false
        chain.getMemory().put("right_value", "value1");
        result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertFalse("当两个值相等时，!=操作符应返回false", result);

        // 测试!=操作符 - 一个值为null应返回true
        chain.getMemory().remove("right_value");
        result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertTrue("当一个值为null时，!=操作符应返回true", result);
    }

    @Test
    public void testGreaterThanOperator() {
        // 创建一个使用>操作符的分支
        BranchesNode.Branch branch = new BranchesNode.Branch();
        branch.setId("test_branch_gt");
        branch.setTitle("Test Branch GT");
        branch.setLogic("and");

        BranchesNode.Condition condition = new BranchesNode.Condition();
        condition.setKey("condition1");

        BranchesNode.Expression expression = new BranchesNode.Expression();
        expression.setOperator("gt");

        BranchesNode.ExpressionPart leftPart = new BranchesNode.ExpressionPart();
        leftPart.setType("ref");
        leftPart.setContent(Arrays.asList("left_value"));

        BranchesNode.ExpressionPart rightPart = new BranchesNode.ExpressionPart();
        rightPart.setType("ref");
        rightPart.setContent(Arrays.asList("right_value"));

        expression.setLeft(leftPart);
        expression.setRight(rightPart);
        condition.setValue(expression);

        branch.getConditions().add(condition);
        branchesNode.getBranches().add(branch);

        // 测试>操作符 - 左值大于右值应返回true
        chain.getMemory().put("left_value", 10);
        chain.getMemory().put("right_value", 5);
        boolean result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertTrue("当左值大于右值时，>操作符应返回true", result);

        // 测试>操作符 - 左值小于右值应返回false
        chain.getMemory().put("left_value", 3);
        chain.getMemory().put("right_value", 5);
        result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertFalse("当左值小于右值时，>操作符应返回false", result);

        // 测试>操作符 - 左值等于右值应返回false
        chain.getMemory().put("left_value", 5);
        result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertFalse("当左值等于右值时，>操作符应返回false", result);
    }

    @Test
    public void testGreaterThanOrEqualOperator() {
        // 创建一个使用>=操作符的分支
        BranchesNode.Branch branch = new BranchesNode.Branch();
        branch.setId("test_branch_ge");
        branch.setTitle("Test Branch GE");
        branch.setLogic("and");

        BranchesNode.Condition condition = new BranchesNode.Condition();
        condition.setKey("condition1");

        BranchesNode.Expression expression = new BranchesNode.Expression();
        expression.setOperator("gte");

        BranchesNode.ExpressionPart leftPart = new BranchesNode.ExpressionPart();
        leftPart.setType("ref");
        leftPart.setContent(Arrays.asList("left_value"));

        BranchesNode.ExpressionPart rightPart = new BranchesNode.ExpressionPart();
        rightPart.setType("ref");
        rightPart.setContent(Arrays.asList("right_value"));

        expression.setLeft(leftPart);
        expression.setRight(rightPart);
        condition.setValue(expression);

        branch.getConditions().add(condition);
        branchesNode.getBranches().add(branch);

        // 测试>=操作符 - 左值大于右值应返回true
        chain.getMemory().put("left_value", 10);
        chain.getMemory().put("right_value", 5);
        boolean result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertTrue("当左值大于右值时，>=操作符应返回true", result);

        // 测试>=操作符 - 左值等于右值应返回true
        chain.getMemory().put("left_value", 5);
        result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertTrue("当左值等于右值时，>=操作符应返回true", result);

        // 测试>=操作符 - 左值小于右值应返回false
        chain.getMemory().put("left_value", 3);
        chain.getMemory().put("right_value", 5);
        result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertFalse("当左值小于右值时，>=操作符应返回false", result);
    }

    @Test
    public void testLessThanOperator() {
        // 创建一个使用<操作符的分支
        BranchesNode.Branch branch = new BranchesNode.Branch();
        branch.setId("test_branch_lt");
        branch.setTitle("Test Branch LT");
        branch.setLogic("and");

        BranchesNode.Condition condition = new BranchesNode.Condition();
        condition.setKey("condition1");

        BranchesNode.Expression expression = new BranchesNode.Expression();
        expression.setOperator("lt");

        BranchesNode.ExpressionPart leftPart = new BranchesNode.ExpressionPart();
        leftPart.setType("ref");
        leftPart.setContent(Arrays.asList("left_value"));

        BranchesNode.ExpressionPart rightPart = new BranchesNode.ExpressionPart();
        rightPart.setType("ref");
        rightPart.setContent(Arrays.asList("right_value"));

        expression.setLeft(leftPart);
        expression.setRight(rightPart);
        condition.setValue(expression);

        branch.getConditions().add(condition);
        branchesNode.getBranches().add(branch);

        // 测试<操作符 - 左值小于右值应返回true
        chain.getMemory().put("left_value", 3);
        chain.getMemory().put("right_value", 5);
        boolean result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertTrue("当左值小于右值时，<操作符应返回true", result);

        // 测试<操作符 - 左值大于右值应返回false
        chain.getMemory().put("left_value", 10);
        chain.getMemory().put("right_value", 5);
        result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertFalse("当左值大于右值时，<操作符应返回false", result);

        // 测试<操作符 - 左值等于右值应返回false
        chain.getMemory().put("left_value", 5);
        result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertFalse("当左值等于右值时，<操作符应返回false", result);
    }

    @Test
    public void testLessThanOrEqualOperator() {
        // 创建一个使用<=操作符的分支
        BranchesNode.Branch branch = new BranchesNode.Branch();
        branch.setId("test_branch_le");
        branch.setTitle("Test Branch LE");
        branch.setLogic("and");

        BranchesNode.Condition condition = new BranchesNode.Condition();
        condition.setKey("condition1");

        BranchesNode.Expression expression = new BranchesNode.Expression();
        expression.setOperator("lte");

        BranchesNode.ExpressionPart leftPart = new BranchesNode.ExpressionPart();
        leftPart.setType("ref");
        leftPart.setContent(Arrays.asList("left_value"));

        BranchesNode.ExpressionPart rightPart = new BranchesNode.ExpressionPart();
        rightPart.setType("ref");
        rightPart.setContent(Arrays.asList("right_value"));

        expression.setLeft(leftPart);
        expression.setRight(rightPart);
        condition.setValue(expression);

        branch.getConditions().add(condition);
        branchesNode.getBranches().add(branch);

        // 测试<=操作符 - 左值小于右值应返回true
        chain.getMemory().put("left_value", 3);
        chain.getMemory().put("right_value", 5);
        boolean result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertTrue("当左值小于右值时，<=操作符应返回true", result);

        // 测试<=操作符 - 左值等于右值应返回true
        chain.getMemory().put("left_value", 5);
        result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertTrue("当左值等于右值时，<=操作符应返回true", result);

        // 测试<=操作符 - 左值大于右值应返回false
        chain.getMemory().put("left_value", 10);
        chain.getMemory().put("right_value", 5);
        result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertFalse("当左值大于右值时，<=操作符应返回false", result);
    }

    @Test
    public void testStringToNumberComparison() {
        // 创建一个使用>操作符的分支，测试字符串数字比较
        BranchesNode.Branch branch = new BranchesNode.Branch();
        branch.setId("test_branch_str_num");
        branch.setTitle("Test Branch String Number");
        branch.setLogic("and");

        BranchesNode.Condition condition = new BranchesNode.Condition();
        condition.setKey("condition1");

        BranchesNode.Expression expression = new BranchesNode.Expression();
        expression.setOperator("gt");

        BranchesNode.ExpressionPart leftPart = new BranchesNode.ExpressionPart();
        leftPart.setType("ref");
        leftPart.setContent(Arrays.asList("left_value"));

        BranchesNode.ExpressionPart rightPart = new BranchesNode.ExpressionPart();
        rightPart.setType("ref");
        rightPart.setContent(Arrays.asList("right_value"));

        expression.setLeft(leftPart);
        expression.setRight(rightPart);
        condition.setValue(expression);

        branch.getConditions().add(condition);
        branchesNode.getBranches().add(branch);

        // 测试字符串数字比较 - "10" > "5" 应返回true
        chain.getMemory().put("left_value", "10");
        chain.getMemory().put("right_value", "5");
        boolean result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertTrue("字符串数字比较应正确处理，\"10\" > \"5\" 应返回true", result);

        // 测试字符串数字比较 - "5" > "10" 应返回false
        chain.getMemory().put("left_value", "5");
        chain.getMemory().put("right_value", "10");
        result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertFalse("字符串数字比较应正确处理，\"5\" > \"10\" 应返回false", result);
    }

    @Test
    public void testOutputParameters() {
        // 创建测试分支
        BranchesNode.Branch branch1 = new BranchesNode.Branch();
        branch1.setId("branch_1");
        branch1.setTitle("Branch 1");
        
        BranchesNode.Branch branch2 = new BranchesNode.Branch();
        branch2.setId("branch_2");
        branch2.setTitle("Branch 2");
        
        branchesNode.getBranches().add(branch1);
        branchesNode.getBranches().add(branch2);

        // 验证输出参数
        java.util.List<cn.boommanpro.gaia.workflow.param.Parameter> outputParameters = branchesNode.getOutputParameters();
        Assert.assertEquals("应有两个输出参数", 2, outputParameters.size());
        Assert.assertEquals("第一个参数ID应为branch_1", "branch_1", outputParameters.get(0).getName());
        Assert.assertEquals("第二个参数ID应为branch_2", "branch_2", outputParameters.get(1).getName());
        Assert.assertEquals("参数类型应为Boolean", cn.boommanpro.gaia.workflow.param.DataType.Boolean, outputParameters.get(0).getType());
        Assert.assertEquals("参数类型应为Boolean", cn.boommanpro.gaia.workflow.param.DataType.Boolean, outputParameters.get(1).getType());
    }
    
    @Test
    public void testArrayContainsOperator() {
        // 创建一个使用contains操作符的分支
        BranchesNode.Branch branch = new BranchesNode.Branch();
        branch.setId("test_branch_contains");
        branch.setTitle("Test Branch Contains");
        branch.setLogic("and");

        BranchesNode.Condition condition = new BranchesNode.Condition();
        condition.setKey("condition1");

        BranchesNode.Expression expression = new BranchesNode.Expression();
        expression.setOperator("contains");

        BranchesNode.ExpressionPart leftPart = new BranchesNode.ExpressionPart();
        leftPart.setType("ref");
        leftPart.setContent(Arrays.asList("array_value"));

        BranchesNode.ExpressionPart rightPart = new BranchesNode.ExpressionPart();
        rightPart.setType("ref");
        rightPart.setContent(Arrays.asList("search_value"));

        expression.setLeft(leftPart);
        expression.setRight(rightPart);
        condition.setValue(expression);

        branch.getConditions().add(condition);
        branchesNode.getBranches().add(branch);

        // 测试数组包含 - List包含元素应返回true
        List<String> testList = Arrays.asList("apple", "banana", "orange");
        chain.getMemory().put("array_value", testList);
        chain.getMemory().put("search_value", "banana");
        boolean result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertTrue("List包含元素时，contains操作符应返回true", result);

        // 测试数组包含 - List不包含元素应返回false
        chain.getMemory().put("search_value", "grape");
        result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertFalse("List不包含元素时，contains操作符应返回false", result);
        
        // 测试数组包含 - 数组包含元素应返回true
        String[] testArray = {"apple", "banana", "orange"};
        chain.getMemory().put("array_value", testArray);
        chain.getMemory().put("search_value", "banana");
        result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertTrue("数组包含元素时，contains操作符应返回true", result);
    }
    
    @Test
    public void testStringContainsOperator() {
        // 创建一个使用contains操作符的分支
        BranchesNode.Branch branch = new BranchesNode.Branch();
        branch.setId("test_branch_str_contains");
        branch.setTitle("Test Branch String Contains");
        branch.setLogic("and");

        BranchesNode.Condition condition = new BranchesNode.Condition();
        condition.setKey("condition1");

        BranchesNode.Expression expression = new BranchesNode.Expression();
        expression.setOperator("contains");

        BranchesNode.ExpressionPart leftPart = new BranchesNode.ExpressionPart();
        leftPart.setType("ref");
        leftPart.setContent(Arrays.asList("string_value"));

        BranchesNode.ExpressionPart rightPart = new BranchesNode.ExpressionPart();
        rightPart.setType("ref");
        rightPart.setContent(Arrays.asList("substring_value"));

        expression.setLeft(leftPart);
        expression.setRight(rightPart);
        condition.setValue(expression);

        branch.getConditions().add(condition);
        branchesNode.getBranches().add(branch);

        // 测试字符串包含 - 包含子串应返回true
        chain.getMemory().put("string_value", "hello world");
        chain.getMemory().put("substring_value", "world");
        boolean result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertTrue("字符串包含子串时，contains操作符应返回true", result);

        // 测试字符串包含 - 不包含子串应返回false
        chain.getMemory().put("substring_value", "java");
        result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertFalse("字符串不包含子串时，contains操作符应返回false", result);
    }
    
    @Test
    public void testArrayNotContainsOperator() {
        // 创建一个使用not_contains操作符的分支
        BranchesNode.Branch branch = new BranchesNode.Branch();
        branch.setId("test_branch_not_contains");
        branch.setTitle("Test Branch Not Contains");
        branch.setLogic("and");

        BranchesNode.Condition condition = new BranchesNode.Condition();
        condition.setKey("condition1");

        BranchesNode.Expression expression = new BranchesNode.Expression();
        expression.setOperator("not_contains");

        BranchesNode.ExpressionPart leftPart = new BranchesNode.ExpressionPart();
        leftPart.setType("ref");
        leftPart.setContent(Arrays.asList("array_value"));

        BranchesNode.ExpressionPart rightPart = new BranchesNode.ExpressionPart();
        rightPart.setType("ref");
        rightPart.setContent(Arrays.asList("search_value"));

        expression.setLeft(leftPart);
        expression.setRight(rightPart);
        condition.setValue(expression);

        branch.getConditions().add(condition);
        branchesNode.getBranches().add(branch);

        // 测试数组不包含 - List不包含元素应返回true
        List<String> testList = Arrays.asList("apple", "banana", "orange");
        chain.getMemory().put("array_value", testList);
        chain.getMemory().put("search_value", "grape");
        boolean result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertTrue("List不包含元素时，not_contains操作符应返回true", result);

        // 测试数组不包含 - List包含元素应返回false
        chain.getMemory().put("search_value", "banana");
        result = branchesNode.evaluateBranch(branch, chain);
        Assert.assertFalse("List包含元素时，not_contains操作符应返回false", result);
    }
}