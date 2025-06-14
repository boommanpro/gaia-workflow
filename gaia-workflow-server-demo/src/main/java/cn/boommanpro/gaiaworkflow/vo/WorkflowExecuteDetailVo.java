package cn.boommanpro.gaiaworkflow.vo;

import cn.boommanpro.gaia.workflow.log.ChainNodeExecuteInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowExecuteDetailVo {
    private Map<String, Object> result;
    private Map<String, ChainNodeExecuteInfo> executeInfo;
}
