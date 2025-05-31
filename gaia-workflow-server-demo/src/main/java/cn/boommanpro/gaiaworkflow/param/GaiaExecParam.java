package cn.boommanpro.gaiaworkflow.param;

import lombok.Data;

import java.util.Map;

@Data
public class GaiaExecParam {
    private String content;
    private Map<String, Object> params;

}
