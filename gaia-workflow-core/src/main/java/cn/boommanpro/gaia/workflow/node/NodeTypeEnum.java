package cn.boommanpro.gaia.workflow.node;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum NodeTypeEnum {

    START("start", "开始节点"),
    END("end", "结束节点"),
    CONDITION("condition", "条件节点"),
    CODE("code", "代码节点"),
    NOTE("note", "注释节点"),
    COMMENT("comment", "评论节点"),
    BRANCHES("branches", "分支节点"),
    STRING_FORMAT("string-format", "字符串格式化节点");
    ;

    private final String code;
    private final String description;

    public static NodeTypeEnum of(String type) {
        for (NodeTypeEnum nodeTypeEnum : NodeTypeEnum.values()) {
            if (nodeTypeEnum.getCode().equals(type)) {
                return nodeTypeEnum;
            }
        }
        return null;
    }
}