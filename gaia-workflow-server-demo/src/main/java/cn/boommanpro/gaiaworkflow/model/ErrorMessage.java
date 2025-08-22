package cn.boommanpro.gaiaworkflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 错误消息
 *
 * @author <a href="mailto:wangqimeng03@meituan.com">wangqimeg03</a>
 * @date 2025/08/22 13:41
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessage {
    private String nodeID;
    private String message;
}

