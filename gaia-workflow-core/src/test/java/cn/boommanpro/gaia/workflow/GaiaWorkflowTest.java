package cn.boommanpro.gaia.workflow;

import cn.boommanpro.gaia.workflow.model.Chain;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
@Slf4j
public class GaiaWorkflowTest {

    @Test
    public void  onlyStartEndTest() {
        String content = IoUtil.read(new ClassPathResource("only_start_end.json").getStream(), StandardCharsets.UTF_8);
        GaiaWorkflow gaiaWorkflow = new GaiaWorkflow(content);
        Chain chain = gaiaWorkflow.toChain();
        HashMap<String, Object> params = new HashMap<>();
        params.put("aa", "1");
        params.put("ba", "2");
        params.put("ca", "3");
        params.put("da", "4");
        Map<String, Object> result = chain.executeForResult(params);
        Assert.assertEquals("1",result.get("result"));
    }

    @Test
    public void codeTest() {
        String content = IoUtil.read(new ClassPathResource("code.json").getStream(), StandardCharsets.UTF_8);
        GaiaWorkflow gaiaWorkflow = new GaiaWorkflow(content);
        Chain chain = gaiaWorkflow.toChain();
        HashMap<String, Object> params = new HashMap<>();
        params.put("aa", "1");
        params.put("ba", "2");
        params.put("ca", "3");
        params.put("da", "4");
        try {

            Map<String, Object> result = chain.executeForResult(params);
            System.out.println(result);
        } catch (Exception e) {
            log.error("run error", e);
        }
        System.out.println(chain.getExecuteInfoMap());

    }

}
