package cn.boommanpro.gaia.workflow.code;

import java.util.Map;

public interface CodeExecute {
    Map<String, Object> execute(Map<String, Object> inputs);
}
