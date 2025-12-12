package cn.boommanpro.gaiaworkflow.controller;

import cn.boommanpro.gaiaworkflow.converter.GaiaWorkflowConverter;
import cn.boommanpro.gaiaworkflow.dto.GaiaWorkflowDto;
import cn.boommanpro.gaiaworkflow.entity.GaiaWorkflow;
import cn.boommanpro.gaiaworkflow.service.GaiaWorkflowService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workflow")
public class GaiaWorkflowController {

    private final GaiaWorkflowService workflowService;

    public GaiaWorkflowController(GaiaWorkflowService workflowService) {
        this.workflowService = workflowService;
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
     */
    @PostMapping("/create")
    public boolean createWorkflow(@RequestBody GaiaWorkflowDto workflowDto) {
        GaiaWorkflow workflow = GaiaWorkflowConverter.convertToEntity(workflowDto);
        return workflowService.save(workflow);
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