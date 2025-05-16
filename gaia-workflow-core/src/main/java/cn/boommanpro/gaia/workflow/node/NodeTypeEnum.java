package cn.boommanpro.gaia.workflow.node;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NodeTypeEnum {

    START("start", "开始节点"),
    END("end", "结束节点"),
    CONDITION("condition", "条件节点"),
    CODE("code", "代码节点")
    ;
    private final String code;
    private final String description;
}
