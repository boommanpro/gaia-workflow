package cn.boommanpro.gaiaworkflow.controller;

import cn.boommanpro.gaia.workflow.model.WorkflowDefinition;
import cn.boommanpro.gaia.workflow.service.WorkflowStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 工作流管理API控制器
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/11/10
 */
@RestController
@RequestMapping("api/workflows")
@CrossOrigin(origins = "*", allowCredentials = "false", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
public class WorkflowController {

    private final WorkflowStorageService workflowStorageService;

    @Autowired
    public WorkflowController(WorkflowStorageService workflowStorageService) {
        this.workflowStorageService = workflowStorageService;
    }

    /**
     * 获取所有工作流
     */
    @GetMapping
    public List<WorkflowDefinition> getAllWorkflows() {
        return workflowStorageService.getAllWorkflows();
    }

    /**
     * 根据ID获取工作流
     */
    @GetMapping("/{id}")
    public WorkflowDefinition getWorkflowById(@PathVariable String id) {
        WorkflowDefinition workflow = workflowStorageService.getWorkflowById(id);
        if (workflow == null) {
            throw new IllegalArgumentException("工作流不存在: " + id);
        }
        return workflow;
    }

    /**
     * 创建工作流
     */
    @PostMapping
    public WorkflowDefinition createWorkflow(@RequestBody WorkflowDefinition workflow) {
        return workflowStorageService.saveWorkflow(workflow);
    }

    /**
     * 更新工作流
     */
    @PutMapping("/{id}")
    public WorkflowDefinition updateWorkflow(@PathVariable String id, @RequestBody WorkflowDefinition workflow) {
        return workflowStorageService.updateWorkflow(id, workflow);
    }

    /**
     * 删除工作流
     */
    @DeleteMapping("/{id}")
    public boolean deleteWorkflow(@PathVariable String id) {
        return workflowStorageService.deleteWorkflow(id);
    }

    /**
     * 根据标签搜索工作流
     */
    @GetMapping("/search/tag")
    public List<WorkflowDefinition> searchWorkflowsByTag(@RequestParam String tag) {
        return workflowStorageService.searchWorkflowsByTag(tag);
    }

    /**
     * 根据名称搜索工作流
     */
    @GetMapping("/search/name")
    public List<WorkflowDefinition> searchWorkflowsByName(@RequestParam String name) {
        return workflowStorageService.searchWorkflowsByName(name);
    }
}

