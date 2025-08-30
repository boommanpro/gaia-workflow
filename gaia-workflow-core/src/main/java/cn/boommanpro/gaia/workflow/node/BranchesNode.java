package cn.boommanpro.gaia.workflow.node;

import cn.boommanpro.gaia.workflow.model.Chain;
import cn.boommanpro.gaia.workflow.param.Parameter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 分支节点，用于处理工作流中的条件分支
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/05/17
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class BranchesNode extends BaseNode {

    // 分支列表
    private List<Branch> branches = new ArrayList<>();

    @Override
    public Map<String, Object> execute(Chain chain) {
        // 执行所有分支的条件判断，并将结果存入memory
        Map<String, Object> result = new HashMap<>();

        for (Branch branch : branches) {
            boolean branchResult = evaluateBranch(branch, chain);
            result.put(branch.getId(), branchResult);
            // 将结果存入chain的memory中，以便后续节点使用
            chain.getMemory().put(getId() + "." + branch.getId(), branchResult);
        }

        return result;
    }

    @Override
    protected Map<String, Object> getParametersData(Chain chain) {
        return chain.getMemory();
    }

    /**
     * 评估一个分支的条件是否满足
     */
    private boolean evaluateBranch(Branch branch, Chain chain) {
        List<Condition> conditions = branch.getConditions();
        String logic = branch.getLogic();

        if (conditions.isEmpty()) {
            return true;
        }

        if ("and".equals(logic)) {
            // AND逻辑：所有条件都必须满足
            for (Condition condition : conditions) {
                if (!evaluateCondition(condition, chain)) {
                    return false;
                }
            }
            return true;
        } else if ("or".equals(logic)) {
            // OR逻辑：至少有一个条件满足
            for (Condition condition : conditions) {
                if (evaluateCondition(condition, chain)) {
                    return true;
                }
            }
            return false;
        }

        // 默认返回false
        return false;
    }

    /**
     * 评估单个条件是否满足
     */
    private boolean evaluateCondition(Condition condition, Chain chain) {
        Expression expression = condition.getValue();
        if (expression == null) {
            return false;
        }

        String operator = expression.getOperator();
        if (operator == null) {
            return false;
        }

        Object leftValue = getExpressionValue(expression.getLeft(), chain);

        switch (operator) {
            case "is_empty":
                return leftValue == null || leftValue.toString().isEmpty();
            case "is_true":
                return Boolean.TRUE.equals(leftValue);
            case "eq":
                Object rightValue = getExpressionValue(expression.getRight(), chain);
                if (leftValue == null && rightValue == null) {
                    return true;
                }
                if (leftValue == null || rightValue == null) {
                    return false;
                }
                return leftValue.equals(rightValue);
            // 可以根据需要添加更多的操作符
            default:
                return false;
        }
    }

    /**
     * 获取表达式的值
     */
    private Object getExpressionValue(ExpressionPart part, Chain chain) {
        if (part == null) {
            return null;
        }

        String type = part.getType();

        if ("ref".equals(type)) {
            // 引用类型，从chain的memory中获取值
            List<String> content = (List<String>) part.getContent();
            if (content != null && content.size() >= 2) {
                String key = String.join(".", content);
                return chain.getMemory().get(key);
            }
        } else if ("constant".equals(type)) {
            // 常量类型，直接返回值
            return part.getContent();
        }

        return null;
    }

    /**
     * 分支内部类
     */
    @Data
    public static class Branch {
        private String id;
        private String title;
        private String logic; // "and" or "or"
        private List<Condition> conditions = new ArrayList<>();
    }

    /**
     * 条件内部类
     */
    @Data
    public static class Condition {
        private String key;
        private Expression value;
    }

    /**
     * 表达式内部类
     */
    @Data
    public static class Expression {
        private String type;
        private String content;
        private ExpressionPart left;
        private String operator;
        private ExpressionPart right;
    }

    /**
     * 表达式部分内部类
     */
    @Data
    public static class ExpressionPart {
        private String type;
        private Object content; // 可以是字符串、布尔值、数字或字符串列表
    }

    @Override
    public List<Parameter> getOutputParameters() {
        return  new ArrayList<>();

    }
}
