package cn.boommanpro.gaiaworkflow.controller;

import cn.boommanpro.gaiaworkflow.converter.GaiaWorkflowConverter;
import cn.boommanpro.gaiaworkflow.dto.GaiaWorkflowDto;
import cn.boommanpro.gaiaworkflow.entity.GaiaWorkflow;
import cn.boommanpro.gaiaworkflow.entity.GaiaWorkflowTemplate;
import cn.boommanpro.gaiaworkflow.entity.GaiaWorkflowVersion;
import cn.boommanpro.gaiaworkflow.service.GaiaWorkflowService;
import cn.boommanpro.gaiaworkflow.service.GaiaWorkflowTemplateService;
import cn.boommanpro.gaiaworkflow.service.GaiaWorkflowVersionService;
import cn.boommanpro.gaiaworkflow.service.GaiaWorkflowTemplateAppService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workflow")
public class GaiaWorkflowController {

    private final GaiaWorkflowService workflowService;

    private final GaiaWorkflowTemplateService templateService;

    private final GaiaWorkflowVersionService versionService;

    public GaiaWorkflowController(GaiaWorkflowService workflowService,
                                  @Qualifier("gaiaWorkflowTemplateAppService") GaiaWorkflowTemplateService templateService,
                                  GaiaWorkflowVersionService versionService) {
        this.workflowService = workflowService;
        this.templateService = templateService;
        this.versionService = versionService;
    }

    /**
     * 获取所有工作流列表
     */
    @GetMapping("/list")
    public List<GaiaWorkflowDto> listWorkflows() {
        return workflowService.list().stream()
                .map(GaiaWorkflowConverter::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取工作流详情
     */
    @GetMapping("/{id}")
    public GaiaWorkflowDto getWorkflowById(@PathVariable Long id) {
        GaiaWorkflow workflow = workflowService.getById(id);
        return GaiaWorkflowConverter.convertToDto(workflow);
    }

    /**
     * 根据工作流编码获取工作流详情
     */
    @GetMapping("/code/{workflowCode}")
    public GaiaWorkflowDto getWorkflowByCode(@PathVariable String workflowCode) {
        GaiaWorkflow workflow = workflowService.getOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<GaiaWorkflow>()
                .eq("workflow_code", workflowCode)
                .eq("is_deleted", 0)
        );
        return GaiaWorkflowConverter.convertToDto(workflow);
    }

    /**
     * 创建新工作流
     * 在创建工作流时，根据模板创建初始版本并设置为启用状态
     */
    @PostMapping("/create")
    public boolean createWorkflow(@RequestBody GaiaWorkflowDto workflowDto) {
        // 保存工作流
        GaiaWorkflow workflow = GaiaWorkflowConverter.convertToEntity(workflowDto);
        boolean workflowSaved = workflowService.save(workflow);

        if (workflowSaved && workflow.getTemplateCode() != null) {
            // 根据模板编码查找模板
            GaiaWorkflowTemplate template = templateService.getOne(
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<GaiaWorkflowTemplate>()
                    .eq("template_code", workflow.getTemplateCode())
                    .eq("is_deleted", 0)
            );

            if (template != null) {
                // 创建初始版本
                GaiaWorkflowVersion version = new GaiaWorkflowVersion();
                version.setWorkflowCode(workflow.getWorkflowCode());
                version.setVersionNumber("v1.0");
                version.setVersionDesc("基于模板 [" + template.getTemplateName() + "] 创建的初始版本");
                version.setWorkflowData(template.getTemplateData());
                version.setCreatedBy("system");
                version.setIsCurrent(1); // 设置为当前版本
                version.setCreatedAt(LocalDateTime.now());

                // 保存版本
                boolean versionSaved = versionService.save(version);

                if (versionSaved) {
                    // 更新工作流的当前版本ID
                    workflow.setCurrentVersionId(version.getId());
                    workflowService.updateById(workflow);
                }
            }
        }

        return workflowSaved;
    }

    /**
     * 更新工作流
     */
    @PutMapping("/update")
    public boolean updateWorkflow(@RequestBody GaiaWorkflowDto workflowDto) {
        GaiaWorkflow workflow = GaiaWorkflowConverter.convertToEntity(workflowDto);
        return workflowService.updateById(workflow);
    }

    /**
     * 删除工作流
     */
    @DeleteMapping("/delete/{id}")
    public boolean deleteWorkflow(@PathVariable Long id) {
        return workflowService.removeById(id);
    }
}
