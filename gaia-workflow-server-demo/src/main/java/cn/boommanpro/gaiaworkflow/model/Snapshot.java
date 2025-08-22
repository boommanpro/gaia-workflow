package cn.boommanpro.gaiaworkflow.model;

import cn.boommanpro.gaiaworkflow.output.TaskReportOutput;
import lombok.Data;

/**
 * 代码功能
 *
 * @author <a href="mailto:wangqimeng03@meituan.com">wangqimeg03</a>
 * @date 2025/08/22 16:17
 */
@Data
public  class Snapshot extends SnapshotData {
    private String id;

    public Snapshot() {
        super();
    }
}
