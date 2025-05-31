package cn.boommanpro.gaia.workflow.param;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class Parameter {
    private String name;
    private Boolean isPropertyRequired=false;
    private DataType type;
    private String defaultValueString;
    private String description;
    private RefType refType;
    private List<String> refValue;

}
