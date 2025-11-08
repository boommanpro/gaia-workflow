package cn.boommanpro.gaia.workflow.node.loop;

import cn.boommanpro.gaia.workflow.model.Chain;
import cn.boommanpro.gaia.workflow.model.ChainNode;
import cn.boommanpro.gaia.workflow.param.Parameter;
import cn.boommanpro.gaia.workflow.param.RefType;
import cn.boommanpro.gaia.workflow.status.ChainNodeStatus;
import cn.boommanpro.gaia.workflow.status.ChainStatus;
import cn.boommanpro.gaia.workflow.tools.SpringExpressionParser;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 循环节点，用于处理工作流中的循环逻辑
 * 参考agents-flex-core的设计，LoopNode作为特殊的Chain来处理循环逻辑
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/05/17
 */
@Slf4j
@Data
@EqualsAndHashCode(callSuper = true)
public class LoopNode extends Chain {

    // 循环遍历的对象
    private LoopFor loopFor;

    // 循环输出映射
    private Map<String, LoopOutput> loopOutputs = new HashMap<>();

    // 输出定义
    private OutputDefinition outputs;

    // 子块列表
    private List<Block> blocks = new ArrayList<>();

    // 循环控制变量
    private int currentLoopIndex = 0;
    private Object currentLoopItem;
    private boolean loopBreak = false;
    private boolean loopContinue = false;

    @Override
    public Map<String, Object> execute(Chain parent) {
        // 重置循环状态
        resetLoopState();

        // 获取循环数据
        Object loopData = getLoopData(parent);
        if (loopData == null) {
            log.warn("Loop data is null, skipping loop execution");
            return new HashMap<>();
        }

        // 将循环数据转换为列表
        List<Object> loopList = convertToList(loopData);
        if (loopList.isEmpty()) {
            log.info("Loop list is empty, skipping loop execution");
            return new HashMap<>();
        }

        // 初始化输出结果
        Map<String, List<Object>> outputResults = new HashMap<>();
        for (String outputKey : loopOutputs.keySet()) {
            outputResults.put(outputKey, new ArrayList<>());
        }

        // 执行循环
        for (int i = 0; i < loopList.size(); i++) {
            currentLoopIndex = i;
            currentLoopItem = loopList.get(i);
            loopBreak = false;
            loopContinue = false;

            // 重置每次迭代的状态
            resetIterationState();

            // 设置循环变量到内存
            parent.getMemory().put("loop.item", currentLoopItem);
            parent.getMemory().put("loop.index", currentLoopIndex);
            parent.getMemory().put("loop.control", null);

            try {
                // 将 parent 的 memory 复制到当前 LoopNode 的 memory 中
                // 这样 executeInternal() 才能正确访问所有变量
                this.getMemory().putAll(parent.getMemory());

                // 执行子链逻辑
                // 通过 executeForResult 来正确管理执行生命周期
                Map<String, Object> iterationResult = this.executeForResult(this.getMemory(), true);

                // 将执行结果合并回 parent 的 memory
                if (iterationResult != null) {
                    parent.getMemory().putAll(iterationResult);
                    this.getMemory().putAll(iterationResult);
                }

                // 检查是否遇到break
                if (loopBreak) {
                    log.info("Loop execution interrupted by break at index {}", i);
                    break;
                }

                // 检查是否遇到continue
                if (loopContinue) {
                    log.info("Loop execution continued at index {}", i);
                    continue;
                }

                // 收集输出
                collectOutputs(parent, outputResults);

            } catch (Exception e) {
                log.error("Error in loop iteration {}: {}", i, e.getMessage(), e);
                // 循环中的错误应该中断整个循环
                break;
            }
        }

        // 清理循环变量
        parent.getMemory().remove("loop.item");
        parent.getMemory().remove("loop.index");
        parent.getMemory().remove("loop.control");

        // 将结果转换为最终输出格式
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, List<Object>> entry : outputResults.entrySet()) {
            result.put(entry.getKey(), entry.getValue());
        }

        return result;
    }

    @Override
    protected void executeInternal() {
        // 直接调用父类的executeInternal方法，使用Chain的完整执行逻辑
        // 这样可以正确处理nodes和edges
        super.executeInternal();
    }


    @Override
    protected void onNodeExecuteAfter(cn.boommanpro.gaia.workflow.model.NodeContext nodeContext) {
        // 检查是否有循环控制标记
        String controlFlag = (String) getMemory().get("loop.control");
        if ("break".equals(controlFlag)) {
            loopBreak = true;
        } else if ("continue".equals(controlFlag)) {
            loopContinue = true;
        }
    }

    /**
     * 重置循环状态
     */
    private void resetLoopState() {
        currentLoopIndex = 0;
        currentLoopItem = null;
        loopBreak = false;
        loopContinue = false;
    }

    /**
     * 重置 LoopNode 的执行状态（用于每次循环迭代）
     */
    private void resetIterationState() {
        // 重置 Chain 的执行状态
        this.setStatus(ChainStatus.READY);
        this.setException(null);
        this.setMessage(null);
        this.setExecuteResult(null);

        // 重置所有节点的状态
        if (this.getNodes() != null) {
            for (ChainNode node : this.getNodes()) {
                node.setStatus(ChainNodeStatus.READY);
            }
        }

        // 清空节点上下文
        this.getNodeContexts().clear();
    }

    /**
     * 获取循环数据
     */
    private Object getLoopData(Chain parent) {
        if (loopFor == null) {
            return null;
        }

        if ("ref".equals(loopFor.getType())) {
            Object content = loopFor.getContent();
            if (content instanceof List) {
                List<String> contentList = (List<String>) content;
                String key = String.join(".", contentList);
                return SpringExpressionParser.getInstance().getValue(key, parent.getMemory());
            }
        } else if ("constant".equals(loopFor.getType())) {
            return loopFor.getContent();
        }

        return null;
    }

    /**
     * 将数据转换为列表
     */
    @SuppressWarnings("unchecked")
    private List<Object> convertToList(Object data) {
        if (data == null) {
            return new ArrayList<>();
        }

        if (data instanceof List) {
            return (List<Object>) data;
        }

        if (data instanceof JSONArray) {
            return ((JSONArray) data).toList(Object.class);
        }

        // 如果是数组，转换为列表
        if (data.getClass().isArray()) {
            Object[] array = (Object[]) data;
            return Arrays.asList(array);
        }

        // 如果是字符串且看起来像JSON数组
        if (data instanceof String) {
            String dataStr = (String) data;
            if (dataStr.startsWith("[") && dataStr.endsWith("]")) {
                try {
                    JSONArray array = JSONUtil.parseArray(dataStr);
                    return array.toList(Object.class);
                } catch (Exception e) {
                    log.warn("Failed to parse array string: {}", dataStr, e);
                }
            }
        }

        // 单个元素转换为单元素列表
        return Collections.singletonList(data);
    }

    /**
     * 收集输出结果
     */
    private void collectOutputs(Chain parent, Map<String, List<Object>> outputResults) {
        for (Map.Entry<String, LoopOutput> entry : loopOutputs.entrySet()) {
            String outputKey = entry.getKey();
            LoopOutput loopOutput = entry.getValue();

            Object outputValue = getOutputValue(loopOutput, parent);
            if (outputValue != null) {
                outputResults.get(outputKey).add(outputValue);
            }
        }
    }

    /**
     * 获取输出值
     */
    private Object getOutputValue(LoopOutput loopOutput, Chain parent) {
        if ("ref".equals(loopOutput.getType())) {
            Object content = loopOutput.getContent();
            if (content instanceof List) {
                List<String> contentList = (List<String>) content;
                String key = String.join(".", contentList);

                // 特殊处理loop.item和loop.index
                if ("loop.item".equals(key)) {
                    return currentLoopItem;
                } else if ("loop.index".equals(key)) {
                    return currentLoopIndex;
                } else {
                    return SpringExpressionParser.getInstance().getValue(key, parent.getMemory());
                }
            }
        } else if ("constant".equals(loopOutput.getType())) {
            return loopOutput.getContent();
        }

        return null;
    }

    @Override
    public List<Parameter> getOutputParameters() {
        List<Parameter> parameters = new ArrayList<>();

        if (outputs != null && outputs.getProperties() != null) {
            for (Map.Entry<String, PropertyDefinition> entry : outputs.getProperties().entrySet()) {
                String key = entry.getKey();
                PropertyDefinition property = entry.getValue();

                Parameter parameter = new Parameter();
                parameter.setName(key);
                parameter.setType(convertDataType(property.getType()));
                parameter.setDescription(property.getDescription());
                parameter.setRefType(RefType.REF);
                parameter.setRefValue(Collections.singletonList(key));
                parameters.add(parameter);
            }
        }

        return parameters;
    }

    /**
     * 转换数据类型
     */
    private cn.boommanpro.gaia.workflow.param.DataType convertDataType(String type) {
        if (StrUtil.isBlank(type)) {
            return cn.boommanpro.gaia.workflow.param.DataType.String;
        }

        switch (type.toLowerCase()) {
            case "string":
                return cn.boommanpro.gaia.workflow.param.DataType.String;
            case "number":
            case "integer":
                return cn.boommanpro.gaia.workflow.param.DataType.Number;
            case "boolean":
                return cn.boommanpro.gaia.workflow.param.DataType.Boolean;
            case "array":
                return cn.boommanpro.gaia.workflow.param.DataType.Array_Object;
            case "object":
                return cn.boommanpro.gaia.workflow.param.DataType.Object;
            default:
                return cn.boommanpro.gaia.workflow.param.DataType.String;
        }
    }

    /**
     * 循环遍历定义
     */
    @Data
    public static class LoopFor {
        private String type; // "ref" or "constant"
        private Object content;
    }

    /**
     * 循环输出定义
     */
    @Data
    public static class LoopOutput {
        private String type; // "ref" or "constant"
        private Object content;
    }

    /**
     * 输出定义
     */
    @Data
    public static class OutputDefinition {
        private String type;
        private List<String> required;
        private Map<String, PropertyDefinition> properties;
    }

    /**
     * 属性定义
     */
    @Data
    public static class PropertyDefinition {
        private String type;
        private String description;
        private PropertyDefinition items; // 用于数组类型
    }

    /**
     * 块定义
     */
    @Data
    public static class Block {
        private String id;
        private String type;
        private Map<String, Object> meta;
        private Map<String, Object> data;
    }

    @Override
    protected Map<String, Object> getParametersData(Chain chain) {
        return chain.getMemory();
    }

}

