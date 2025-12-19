package cn.boommanpro.gaiaworkflow.controller;

import cn.boommanpro.gaiaworkflow.converter.GaiaWorkflowTemplateConverter;
import cn.boommanpro.gaiaworkflow.dto.GaiaWorkflowTemplateDto;
import cn.boommanpro.gaiaworkflow.entity.GaiaWorkflowTemplate;
import cn.boommanpro.gaiaworkflow.service.GaiaWorkflowTemplateAppService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/template")
public class GaiaWorkflowTemplateController {

    private final GaiaWorkflowTemplateAppService templateAppService;

    public GaiaWorkflowTemplateController(GaiaWorkflowTemplateAppService templateAppService) {
        this.templateAppService = templateAppService;
    }

    /**
     * 获取所有模板列表
     */
    @GetMapping("/list")
    public List<GaiaWorkflowTemplateDto> listTemplates() {
        return templateAppService.list().stream()
                .map(GaiaWorkflowTemplateConverter::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取模板详情
     */
    @GetMapping("/{id}")
    public GaiaWorkflowTemplateDto getTemplateById(@PathVariable Long id) {
        GaiaWorkflowTemplate template = templateAppService.getById(id);
        return GaiaWorkflowTemplateConverter.convertToDto(template);
    }

    /**
     * 创建新模板
     */
    @PostMapping("/create")
    public boolean createTemplate(@RequestBody GaiaWorkflowTemplateDto templateDto) {
        GaiaWorkflowTemplate template = GaiaWorkflowTemplateConverter.convertToEntity(templateDto);
        return templateAppService.save(template);
    }

    /**
     * 更新模板
     */
    @PutMapping("/update")
    public boolean updateTemplate(@RequestBody GaiaWorkflowTemplateDto templateDto) {
        GaiaWorkflowTemplate template = GaiaWorkflowTemplateConverter.convertToEntity(templateDto);
        template.setCreatedAt(null);
        return templateAppService.updateById(template);
    }

    /**
     * 删除模板
     */
    @DeleteMapping("/delete/{id}")
    public boolean deleteTemplate(@PathVariable Long id) {
        return templateAppService.removeById(id);
    }
}
