package cn.boommanpro.gaia.workflow.node.condition;

/**
 * 为真操作符实现
 */
public class IsTrueOperator implements ConditionOperator<Object> {
    
    @Override
    public boolean apply(Object left, Object right) {
        if (left == null) {
            return false;
        }
        
        if (left instanceof Boolean) {
            return (Boolean) left;
        }
        
        if (left instanceof String) {
            return Boolean.parseBoolean((String) left);
        }
        
        // 其他类型，非null且非0数字认为为真
        if (left instanceof Number) {
            return ((Number) left).doubleValue() != 0;
        }
        
        // 其他非null对象认为为真
        return true;
    }
    
    @Override
    public String getOperator() {
        return "is_true";
    }
    
    @Override
    public boolean supportsType(Class<?> type) {
        return true; // 支持所有类型
    }
}