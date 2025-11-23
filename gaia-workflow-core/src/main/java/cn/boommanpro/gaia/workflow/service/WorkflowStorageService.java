package cn.boommanpro.gaia.workflow.service;

import cn.boommanpro.gaia.workflow.model.WorkflowDefinition;

import java.util.List;

/**
 * 工作流存储服务接口
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/11/10
 */
public interface WorkflowStorageService {

    /**
     * 获取所有工作流定义
     *
     * @return 工作流定义列表
     */
    List<WorkflowDefinition> getAllWorkflows();

    /**
     * 根据ID获取工作流定义
     *
     * @param id 工作流ID
     * @return 工作流定义
     */
    WorkflowDefinition getWorkflowById(String id);

    /**
     * 保存工作流定义
     *
     * @param workflow 工作流定义
     * @return 保存后的工作流定义
     */
    WorkflowDefinition saveWorkflow(WorkflowDefinition workflow);

    /**
     * 更新工作流定义
     *
     * @param id      工作流ID
     * @param workflow 工作流定义
     * @return 更新后的工作流定义
     */
    WorkflowDefinition updateWorkflow(String id, WorkflowDefinition workflow);

    /**
     * 删除工作流定义
     *
     * @param id 工作流ID
     * @return 是否删除成功
     */
    boolean deleteWorkflow(String id);

    /**
     * 根据标签搜索工作流
     *
     * @param tag 标签
     * @return 工作流定义列表
     */
    List<WorkflowDefinition> searchWorkflowsByTag(String tag);

    /**
     * 根据名称搜索工作流
     *
     * @param name 工作流名称（支持模糊搜索）
     * @return 工作流定义列表
     */
    List<WorkflowDefinition> searchWorkflowsByName(String name);
}

