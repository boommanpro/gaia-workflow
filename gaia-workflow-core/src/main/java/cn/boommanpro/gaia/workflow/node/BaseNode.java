package cn.boommanpro.gaia.workflow.node;

import cn.boommanpro.gaia.workflow.model.ChainNode;
import cn.boommanpro.gaia.workflow.param.Parameter;
import lombok.Data;

import java.util.List;

/**
 * 代码功能
 *
 * @author <a href="mailto:wangqimeng03@meituan.com">wangqimeg03</a>
 * @date 2025/05/17 15:01
 */
@Data
public abstract class BaseNode extends ChainNode {
    protected List<Parameter> parameters;
    protected List<Parameter> outputDefs;

    public List<Parameter> addParameter() {
        return parameters;
    }

    public List<Parameter> addOutDefs() {
        return outputDefs;
    }
}
