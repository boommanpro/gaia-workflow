package cn.boommanpro.gaiaworkflow;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.boommanpro.gaia.workflow.log.ChainNodeExecuteInfo;
import cn.boommanpro.gaia.workflow.model.Chain;
import cn.boommanpro.gaiaworkflow.param.GaiaExecParam;
import cn.boommanpro.gaiaworkflow.vo.CommonResult;
import cn.boommanpro.gaiaworkflow.vo.WorkflowExecuteDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("workflow")
@CrossOrigin(origins = "*", maxAge = 3600) // 添加跨域支持
public class GaiaWorkflowController {
    @PostMapping("exec")
    public CommonResult<WorkflowExecuteDetailVo> exec(@RequestBody GaiaExecParam param) {
        GaiaWorkflow gaiaWorkflow = new GaiaWorkflow(param.getGraph());
        try {
            Chain chain = gaiaWorkflow.toChain();
            Map<String, Object> result = null;
            try {
                result = chain.executeForResult(param.getParams());
            } catch (Exception e) {
                log.info("error", e);
            }
            Map<String, ChainNodeExecuteInfo> executeInfoMap = chain.getExecuteInfoMap();
            return CommonResult.success(new WorkflowExecuteDetailVo(result, executeInfoMap));
        } catch (Exception e) {
            return CommonResult.error(e);
        }


    }
}
