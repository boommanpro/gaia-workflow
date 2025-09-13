package cn.boommanpro.gaia.workflow.node;

import cn.boommanpro.gaia.workflow.model.Chain;
import cn.boommanpro.gaia.workflow.param.Parameter;
import cn.boommanpro.gaia.workflow.param.RefType;
import cn.boommanpro.gaia.workflow.tools.SpringExpressionParser;
import cn.hutool.json.JSONObject;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 变量节点，用于处理变量声明和赋值操作
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class VariableNode extends BaseNode {

    // 变量赋值操作列表
    private List<VariableAssign> assigns = new ArrayList<>();

    @Override
    public Map<String, Object> execute(Chain chain) {
        Map<String, Object> result = new HashMap<>();

        // 执行所有赋值操作
        for (VariableAssign assign : assigns) {
            // 检查左值是否为引用类型
            if (assign.getLeftType() != null && assign.getLeftType() == RefType.REF) {
                // 处理引用类型的左值
                handleReferenceLeftValue(assign, chain);
            } else {
                // 处理普通变量名的左值
                Object value = evaluateValue(assign.getRight(), chain);
                result.put(assign.getLeft(), value);
            }
        }

        return result;
    }



    /**
     * 处理引用类型的左值赋值
     * @param assign 赋值操作
     * @param chain 工作流链
     */
    private void handleReferenceLeftValue(VariableAssign assign, Chain chain) {
        Object leftContent = assign.getLeftContent();
        if (!(leftContent instanceof List)) {
            return;
        }

        List<String> refPath = (List<String>) leftContent;
        if (refPath.size() < 2) {
            return;
        }
        // 获取右值
        Object value = evaluateValue(assign.getRight(), chain);
        Map<String, Object> target = chain.getMemory();
        for (int i = 0; i < refPath.size()-1; i++) {
            target = (Map<String, Object>) target.get(refPath.get(i));
        }
        if (target != null) {
            target.put(refPath.get(refPath.size() - 1), value);
        }
    }

    /**
     * 计算右侧表达式的值
     * @param value 变量值定义
     * @param chain 工作流链
     * @return 计算结果
     */
    private Object evaluateValue(VariableValue value, Chain chain) {
        if (value.getType() == RefType.CONSTANT) {
            return value.getContent();
        } else if (value.getType() == RefType.REF) {
            Object content = value.getContent();
            if (content instanceof List) {
                List<?> refPath = (List<?>) content;
                if (refPath != null && refPath.size() >= 2) {
                    String nodeId = refPath.get(0).toString();
                    String paramName = refPath.get(1).toString();
                    Object nodeResult = SpringExpressionParser.getInstance().getValue(nodeId,chain.getMemory());;
                    if (nodeResult instanceof Map) {
                        return ((Map<?, ?>) nodeResult).get(paramName);
                    }
                }
            }
        }
        return null;
    }

    @Override
    public List<Parameter> getOutputParameters() {
        List<Parameter> parameters = new ArrayList<>();
        for (VariableAssign assign : assigns) {
            // 只有当左值不是引用类型时才添加到输出参数中
            if (assign.getLeftType() == null || assign.getLeftType() != RefType.REF) {
                Parameter param = new Parameter();
                param.setName(assign.getLeft());
                // 简化处理，实际应该根据右值类型推断
                param.setType(cn.boommanpro.gaia.workflow.param.DataType.String);
                parameters.add(param);
            }
        }
        return parameters;
    }

    /**
     * 变量赋值操作
     */
    @Data
    public static class VariableAssign {
        // 操作符 (declare, assign)
        private String operator;
        // 左值 (变量名)
        private String left;
        // 左值类型
        private RefType leftType;
        // 左值内容（用于引用类型）
        private Object leftContent;
        // 右值
        private VariableValue right;
    }

    /**
     * 变量值定义
     */
    @Data
    public static class VariableValue {
        // 值类型 (constant, ref, expression)
        private RefType type;
        // 值内容
        private Object content;
        // 值的schema定义
        private JSONObject schema;
    }
}
