package cn.boommanpro.gaiaworkflow;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.log.ChainNodeExecuteInfo;
import cn.boommanpro.gaia.workflow.model.Chain;
import cn.boommanpro.gaiaworkflow.param.GaiaExecParam;
import cn.boommanpro.gaiaworkflow.vo.WorkflowExecuteDetailVo;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("workflow")
@CrossOrigin(origins = "*", maxAge = 3600) // 添加跨域支持
public class GaiaWorkflowController {
    @PostMapping("exec")
    public WorkflowExecuteDetailVo exec(@RequestBody GaiaExecParam param) {
        GaiaWorkflow gaiaWorkflow = new GaiaWorkflow(param.getGraph());
        Chain chain = gaiaWorkflow.toChain();
        Map<String, Object> result = chain.executeForResult(param.getParams());
        Map<String, ChainNodeExecuteInfo> executeInfoMap = chain.getExecuteInfoMap();
        return new WorkflowExecuteDetailVo(result, executeInfoMap);
    }
}
