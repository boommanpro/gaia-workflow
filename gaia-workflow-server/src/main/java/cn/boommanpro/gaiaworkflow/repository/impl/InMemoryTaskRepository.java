package cn.boommanpro.gaiaworkflow.repository.impl;

import cn.boommanpro.gaiaworkflow.model.TaskInfo;
import cn.boommanpro.gaiaworkflow.repository.TaskRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存任务存储库实现
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/08/22 15:05
 */
@Repository
public class InMemoryTaskRepository implements TaskRepository {

    /**
     * 存储任务信息的内存存储
     */
    private final Map<String, TaskInfo> taskStore = new ConcurrentHashMap<>();

    @Override
    public void saveTask(String taskId, TaskInfo taskInfo) {
        taskStore.put(taskId, taskInfo);
    }

    @Override
    public TaskInfo getTask(String taskId) {
        return taskStore.get(taskId);
    }

    @Override
    public boolean updateTask(String taskId, TaskInfo taskInfo) {
        if (taskStore.containsKey(taskId)) {
            taskStore.put(taskId, taskInfo);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteTask(String taskId) {
        return taskStore.remove(taskId) != null;
    }
}

