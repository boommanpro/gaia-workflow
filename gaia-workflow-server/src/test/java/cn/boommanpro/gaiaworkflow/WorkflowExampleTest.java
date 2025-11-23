package cn.boommanpro.gaiaworkflow;

import cn.boommanpro.gaia.workflow.GaiaWorkflow;
import cn.hutool.core.io.IoUtil;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 代码功能
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/11/14 15:22
 */
public class WorkflowExampleTest {
    @Test
    public void test() throws IOException, InterruptedException {
        // 可配置的线程池大小
        int threadPoolSize = 1;

        ExecutorService executor = Executors.newFixedThreadPool(threadPoolSize);

        String path = "workflow_var_code_str_format_example.json";
        String content = IoUtil.read(new ClassPathResource(path).getInputStream(), Charset.defaultCharset());

        // 总执行次数
        int totalExecutions = 1_0000;
        CountDownLatch latch = new CountDownLatch(totalExecutions);

        // 统计完成的任务数
        AtomicInteger completedCount = new AtomicInteger(0);

        long start = System.currentTimeMillis();

        // 模拟不断提交任务到线程池排队的情况
        for (int i = 0; i < totalExecutions; i++) {
            final int taskIndex = i;
            executor.submit(() -> {
                try {
                    // 模拟任务执行
                    GaiaWorkflow gaiaWorkflow = new GaiaWorkflow(content);
                    Map<String, Object> run = gaiaWorkflow.run(new HashMap<>());

                    int completed = completedCount.incrementAndGet();
                    // 如果需要看进度，可以取消注释下面这行
                    // if (completed % 10000 == 0) {
                    //     System.out.println("已完成任务数: " + completed);
                    // }

                } finally {
                    latch.countDown();
                }
            });
        }

        // 等待所有任务完成
        latch.await();

        long end = System.currentTimeMillis();
        System.out.println("排队执行完成，线程池大小: " + threadPoolSize + ", 总任务数: " + totalExecutions + ", 总耗时: " + (end - start) + "ms");

        // 关闭线程池
        executor.shutdown();
    }


    @Test
    public void test0() throws IOException {
        String path = "workflow_var_code_str_format_example.json";
        String content = IoUtil.read(new ClassPathResource(path).getInputStream(), Charset.defaultCharset());
        long start = System.currentTimeMillis();
        for (int i = 0; i < 1_0000; i++) {
            Map<Object, Object> result = new HashMap<>();
            result.put(i, i);
            if (result.get(i) == null) {
                break;
            }
        }
        long end = System.currentTimeMillis();
        System.out.println((end - start) + "ms");

    }
}
