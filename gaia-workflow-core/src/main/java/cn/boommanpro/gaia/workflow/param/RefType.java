package cn.boommanpro.gaia.workflow.param;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RefType {
    REF("ref"),
    CONSTANT("constant"),
    ;

    private final String value;
}
