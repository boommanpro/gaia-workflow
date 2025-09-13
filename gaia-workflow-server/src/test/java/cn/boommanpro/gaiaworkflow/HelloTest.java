package cn.boommanpro.gaiaworkflow;

import cn.boommanpro.gaia.workflow.code.CodeExecute;

import java.util.HashMap;
import java.util.Map;

public class HelloTest implements CodeExecute {
    @Override
    public Map<String, Object> execute(Map<String, Object> inputs) {

        HashMap<String, Object> result = new HashMap<>();
        result.putAll(inputs);
        result.put("c", "" + inputs.get("a") + inputs.get("b"));
        return result;
    }
}
