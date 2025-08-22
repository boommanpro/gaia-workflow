package cn.boommanpro.gaiaworkflow.enums;

import lombok.Getter;

@Getter
public enum WorkflowStatusEnum {
    IDLE("idle", "空闲"),
    PROCESSING("processing", "处理中"),
    SUCCESS("success", "成功"),
    FAIL("fail", "失败"),
    CANCELED("canceled", "已取消");

    private final String code;
    private final String desc;

    WorkflowStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
