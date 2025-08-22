package cn.boommanpro.gaiaworkflow.input;

import lombok.Data;

import java.util.Map;

/**
 * 代码功能
 *
 * @author <a href="mailto:wangqimeng03@meituan.com">wangqimeg03</a>
 * @date 2025/08/22 13:35
 */
@Data
public class TaskRunInput {
    private String schema;
    private Map<String, Object> inputs;
}
