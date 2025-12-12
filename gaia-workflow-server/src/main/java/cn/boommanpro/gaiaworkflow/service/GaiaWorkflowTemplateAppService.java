package cn.boommanpro.gaiaworkflow.service;

import cn.boommanpro.gaiaworkflow.entity.GaiaWorkflowTemplate;
import cn.boommanpro.gaiaworkflow.mapper.GaiaWorkflowTemplateMapper;
import cn.boommanpro.gaiaworkflow.service.GaiaWorkflowTemplateService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class GaiaWorkflowTemplateAppService extends ServiceImpl<GaiaWorkflowTemplateMapper, GaiaWorkflowTemplate> implements GaiaWorkflowTemplateService {
}