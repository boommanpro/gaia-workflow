package cn.boommanpro.gaia.workflow;

import cn.boommanpro.gaia.workflow.model.Chain;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class GaiaWorkflowTest {

    @Test
    public void execute() {
        String content = IoUtil.read(new ClassPathResource("full_run.json").getStream(), StandardCharsets.UTF_8);
        GaiaWorkflow gaiaWorkflow = new GaiaWorkflow(content);
        Chain chain = gaiaWorkflow.toChain();
        HashMap<String, Object> params = new HashMap<>();
        params.put("a", "1");
        params.put("b", "2");
        params.put("c", "3");
        params.put("d", "4");
        Map<String, Object> result = chain.executeForResult(params);
        System.out.println(result);
        System.out.println(chain.getExecuteInfoMap());
    }

    @Test
    public void test02() {

    }

}
