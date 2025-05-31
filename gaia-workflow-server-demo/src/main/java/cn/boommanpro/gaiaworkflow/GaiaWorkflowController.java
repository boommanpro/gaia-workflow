package cn.boommanpro.gaiaworkflow;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.model.Chain;
import cn.boommanpro.gaiaworkflow.param.GaiaExecParam;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/gaia")
public class GaiaWorkflowController {
    @PostMapping("exec")
    public Map<String, Object> exec( @RequestBody GaiaExecParam param) {
        GaiaWorkflow gaiaWorkflow = new GaiaWorkflow(param.getContent());
        Chain chain = gaiaWorkflow.toChain();
        return chain.executeForResult(param.getParams());
    }
}
