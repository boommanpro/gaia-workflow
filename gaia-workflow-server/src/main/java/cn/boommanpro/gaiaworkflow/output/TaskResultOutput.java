package cn.boommanpro.gaiaworkflow.output;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class TaskResultOutput {
    private Map<String, Object> outputs;
}

