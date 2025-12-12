package cn.boommanpro.gaiaworkflow.controller;

import cn.boommanpro.gaiaworkflow.converter.GaiaWorkflowLogConverter;
import cn.boommanpro.gaiaworkflow.dto.GaiaWorkflowLogDto;
import cn.boommanpro.gaiaworkflow.entity.GaiaWorkflowLog;
import cn.boommanpro.gaiaworkflow.service.GaiaWorkflowLogService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/workflow-log")
public class GaiaWorkflowLogController {

    private final GaiaWorkflowLogService workflowLogService;

    public GaiaWorkflowLogController(GaiaWorkflowLogService workflowLogService) {
        this.workflowLogService = workflowLogService;
    }

    /**
     * 获取指定工作流的所有日志列表
     */
    @GetMapping("/list/{workflowCode}")
    public List<GaiaWorkflowLogDto> listLogsByWorkflowCode(@PathVariable String workflowCode) {
        return workflowLogService.list(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<GaiaWorkflowLog>()
                .eq("workflow_code", workflowCode)
                .orderByDesc("created_at")
        ).stream()
                .map(GaiaWorkflowLogConverter::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 获取指定工作流和版本的日志列表
     */
    @GetMapping("/list/{workflowCode}/{versionNumber}")
    public List<GaiaWorkflowLogDto> listLogsByVersion(@PathVariable String workflowCode, 
                                                      @PathVariable String versionNumber) {
        return workflowLogService.list(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<GaiaWorkflowLog>()
                .eq("workflow_code", workflowCode)
                .eq("version_number", versionNumber)
                .orderByDesc("created_at")
        ).stream()
                .map(GaiaWorkflowLogConverter::convertToDto)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取日志详情
     */
    @GetMapping("/{id}")
    public GaiaWorkflowLogDto getLogById(@PathVariable Long id) {
        GaiaWorkflowLog log = workflowLogService.getById(id);
        return GaiaWorkflowLogConverter.convertToDto(log);
    }

    /**
     * 根据执行ID获取日志详情
     */
    @GetMapping("/execution/{executionId}")
    public GaiaWorkflowLogDto getLogByExecutionId(@PathVariable String executionId) {
        GaiaWorkflowLog log = workflowLogService.getOne(
            new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<GaiaWorkflowLog>()
                .eq("execution_id", executionId)
        );
        return GaiaWorkflowLogConverter.convertToDto(log);
    }

    /**
     * 创建新日志
     */
    @PostMapping("/create")
    public boolean createLog(@RequestBody GaiaWorkflowLogDto logDto) {
        GaiaWorkflowLog log = GaiaWorkflowLogConverter.convertToEntity(logDto);
        return workflowLogService.save(log);
    }

    /**
     * 更新日志
     */
    @PutMapping("/update")
    public boolean updateLog(@RequestBody GaiaWorkflowLogDto logDto) {
        GaiaWorkflowLog log = GaiaWorkflowLogConverter.convertToEntity(logDto);
        return workflowLogService.updateById(log);
    }

    /**
     * 删除日志
     */
    @DeleteMapping("/delete/{id}")
    public boolean deleteLog(@PathVariable Long id) {
        return workflowLogService.removeById(id);
    }
}