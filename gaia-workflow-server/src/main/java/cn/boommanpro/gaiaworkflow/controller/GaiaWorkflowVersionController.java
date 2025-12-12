package cn.boommanpro.gaiaworkflow.controller;

import cn.boommanpro.gaiaworkflow.converter.GaiaWorkflowVersionConverter;
import cn.boommanpro.gaiaworkflow.dto.GaiaWorkflowVersionDto;
import cn.boommanpro.gaiaworkflow.entity.GaiaWorkflowVersion;
import cn.boommanpro.gaiaworkflow.service.GaiaWorkflowVersionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workflow-version")
public class GaiaWorkflowVersionController {

    private final GaiaWorkflowVersionService workflowVersionService;

    public GaiaWorkflowVersionController(GaiaWorkflowVersionService workflowVersionService) {
        this.workflowVersionService = workflowVersionService;
        }

    /**
     * 获取指定工作流的所有版本列表
     */
    @GetMapping("/list/{workflowCode}")
    public List<GaiaWorkflowVersionDto> listVersionsByWorkflowCode(@PathVariable String workflowCode) {
        return workflowVersionService.list(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<GaiaWorkflowVersion>()
                .eq("workflow_code", workflowCode)
        ).stream()
                .map(GaiaWorkflowVersionConverter::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取版本详情
     */
    @GetMapping("/{id}")
    public GaiaWorkflowVersionDto getVersionById(@PathVariable Long id) {
        GaiaWorkflowVersion version = workflowVersionService.getById(id);
        return GaiaWorkflowVersionConverter.convertToDto(version);
    }

    /**
     * 创建新版本
     */
    @PostMapping("/create")
    public boolean createVersion(@RequestBody GaiaWorkflowVersionDto versionDto) {
        GaiaWorkflowVersion version = GaiaWorkflowVersionConverter.convertToEntity(versionDto);
        return workflowVersionService.save(version);
    }

    /**
     * 更新版本
     */
    @PutMapping("/update")
    public boolean updateVersion(@RequestBody GaiaWorkflowVersionDto versionDto) {
        GaiaWorkflowVersion version = GaiaWorkflowVersionConverter.convertToEntity(versionDto);
        return workflowVersionService.updateById(version);
    }

    /**
     * 删除版本
     */
    @DeleteMapping("/delete/{id}")
    public boolean deleteVersion(@PathVariable Long id) {
        return workflowVersionService.removeById(id);
    }

    /**
     * 设置为当前版本
     */
    @PutMapping("/set-current/{id}")
    public boolean setCurrentVersion(@PathVariable Long id) {
        // 先将该工作流下的所有版本设为非当前版本
        GaiaWorkflowVersion version = workflowVersionService.getById(id);
        if (version != null) {
            workflowVersionService.update(
                new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<GaiaWorkflowVersion>()
                    .eq("workflow_code", version.getWorkflowCode())
                    .set("is_current", 0)
            );
            
            // 再将指定版本设为当前版本
            return workflowVersionService.update(
                new com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper<GaiaWorkflowVersion>()
                    .eq("id", id)
                    .set("is_current", 1)
            );
        }
        return false;
    }
}