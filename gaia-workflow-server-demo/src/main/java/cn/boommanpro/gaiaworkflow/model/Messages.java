package cn.boommanpro.gaiaworkflow.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 消息
 *
 * @author <a href="mailto:wangqimeng03@meituan.com">wangqimeg03</a>
 * @date 2025/08/22 13:41
 */
@Data
@NoArgsConstructor
public class Messages {
    private List<ErrorMessage> error = new ArrayList<>();
}

