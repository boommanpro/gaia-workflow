package cn.boommanpro.gaiaworkflow.param;

import lombok.Data;

import java.util.Map;

@Data
public class GaiaExecParam {
    private String graph;
    private Map<String, Object> params;

}
