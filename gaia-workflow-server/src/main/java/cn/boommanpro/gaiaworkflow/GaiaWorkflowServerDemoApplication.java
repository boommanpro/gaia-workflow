package cn.boommanpro.gaiaworkflow;

import cn.boommanpro.gaia.workflow.node.WorkflowNode;
import cn.boommanpro.gaia.workflow.service.WorkflowStorageService;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.event.EventListener;

/**
 * Gaia工作流服务启动类
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 */
@SpringBootApplication
@ComponentScan(basePackages = {"cn.boommanpro.gaiaworkflow", "cn.boommanpro.gaia.workflow"})
public class GaiaWorkflowServerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(GaiaWorkflowServerDemoApplication.class, args);
    }

    @Autowired
    private WorkflowStorageService workflowStorageService;

    /**
     * 应用启动完成后初始化工作流节点存储服务
     */
    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        // 初始化WorkflowNode的存储服务
        WorkflowNode.setWorkflowStorageService(workflowStorageService);
    }

}
