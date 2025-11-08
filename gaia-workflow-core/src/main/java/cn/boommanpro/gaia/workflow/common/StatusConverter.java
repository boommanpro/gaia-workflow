package cn.boommanpro.gaia.workflow.common;

import cn.boommanpro.gaia.workflow.status.ChainNodeStatus;
import cn.boommanpro.gaia.workflow.status.ChainStatus;

/**
 * 状态转换工具类
 * 提供各种状态枚举之间的转换功能，统一状态管理
 *
 * @author <a href="mailto:boommanpro@gmail.com">boommanpro</a>
 * @date 2025/08/22
 */
public class StatusConverter {

    /**
     * 将ChainNodeStatus转换为ExecutionStatus
     *
     * @param chainNodeStatus 链节点状态
     * @return 执行状态
     */
    public static ExecutionStatus fromChainNodeStatus(ChainNodeStatus chainNodeStatus) {
        if (chainNodeStatus == null) {
            return ExecutionStatus.PENDING;
        }

        switch (chainNodeStatus) {
            case READY:
                return ExecutionStatus.READY;
            case WAIT:
                return ExecutionStatus.WAIT;
            case RUNNING:
                return ExecutionStatus.RUNNING;
            case FINISHED:
                return ExecutionStatus.FINISHED;
            case FAILED:
                return ExecutionStatus.FAILED;
            case SKIPPED:
                return ExecutionStatus.SKIPPED;
            default:
                return ExecutionStatus.PENDING;
        }
    }

    /**
     * 将ExecutionStatus转换为ChainNodeStatus
     *
     * @param executionStatus 执行状态
     * @return 链节点状态
     */
    public static ChainNodeStatus toChainNodeStatus(ExecutionStatus executionStatus) {
        if (executionStatus == null) {
            return ChainNodeStatus.WAIT;
        }

        switch (executionStatus) {
            case READY:
                return ChainNodeStatus.READY;
            case WAIT:
            case PENDING:
            case IDLE:
                return ChainNodeStatus.WAIT;
            case RUNNING:
            case PROCESSING:
                return ChainNodeStatus.RUNNING;
            case FINISHED:
            case SUCCEEDED:
            case SUCCESS:
            case FINISHED_NORMAL:
                return ChainNodeStatus.FINISHED;
            case FAILED:
            case FAIL:
            case ERROR:
            case FINISHED_ABNORMAL:
                return ChainNodeStatus.FAILED;
            case SKIPPED:
            case CANCELED:
                return ChainNodeStatus.SKIPPED;
            default:
                return ChainNodeStatus.WAIT;
        }
    }

    /**
     * 将ChainStatus转换为ExecutionStatus
     *
     * @param chainStatus 链状态
     * @return 执行状态
     */
    public static ExecutionStatus fromChainStatus(ChainStatus chainStatus) {
        if (chainStatus == null) {
            return ExecutionStatus.PENDING;
        }

        switch (chainStatus) {
            case READY:
                return ExecutionStatus.READY;
            case RUNNING:
                return ExecutionStatus.RUNNING;
            case SKIPPED:
                return ExecutionStatus.SKIPPED;
            case ERROR:
                return ExecutionStatus.ERROR;
            case FINISHED_NORMAL:
                return ExecutionStatus.FINISHED_NORMAL;
            case FINISHED_ABNORMAL:
                return ExecutionStatus.FINISHED_ABNORMAL;
            default:
                return ExecutionStatus.PENDING;
        }
    }

    /**
     * 将ExecutionStatus转换为ChainStatus
     *
     * @param executionStatus 执行状态
     * @return 链状态
     */
    public static ChainStatus toChainStatus(ExecutionStatus executionStatus) {
        if (executionStatus == null) {
            return ChainStatus.READY;
        }

        switch (executionStatus) {
            case READY:
            case PENDING:
            case IDLE:
                return ChainStatus.READY;
            case RUNNING:
            case PROCESSING:
                return ChainStatus.RUNNING;
            case SKIPPED:
            case CANCELED:
                return ChainStatus.SKIPPED;
            case ERROR:
            case FAILED:
            case FAIL:
                return ChainStatus.ERROR;
            case FINISHED:
            case SUCCEEDED:
            case SUCCESS:
            case FINISHED_NORMAL:
                return ChainStatus.FINISHED_NORMAL;
            case FINISHED_ABNORMAL:
                return ChainStatus.FINISHED_ABNORMAL;
            default:
                return ChainStatus.READY;
        }
    }

    /**
     * 将ExecutionStatus转换为server模块兼容的NodeStatus字符串值
     *
     * @param executionStatus 执行状态
     * @return NodeStatus兼容的字符串值
     */
    public static String toNodeStatusValue(ExecutionStatus executionStatus) {
        if (executionStatus == null) {
            return "pending";
        }
        return executionStatus.toNodeStatusValue();
    }

    /**
     * 从server模块的NodeStatus字符串值转换为ExecutionStatus
     *
     * @param nodeStatusValue NodeStatus字符串值
     * @return 执行状态
     */
    public static ExecutionStatus fromNodeStatusValue(String nodeStatusValue) {
        return ExecutionStatus.fromCode(nodeStatusValue);
    }

    /**
     * 判断状态是否表示成功
     *
     * @param status 状态
     * @return 是否成功
     */
    public static boolean isSuccess(ExecutionStatus status) {
        return status != null && status.isSuccess();
    }

    /**
     * 判断状态是否表示失败
     *
     * @param status 状态
     * @return 是否失败
     */
    public static boolean isFailure(ExecutionStatus status) {
        return status != null && status.isFailure();
    }

    /**
     * 判断状态是否表示运行中
     *
     * @param status 状态
     * @return 是否运行中
     */
    public static boolean isRunning(ExecutionStatus status) {
        return status != null && status.isRunning();
    }

    /**
     * 判断状态是否表示等待
     *
     * @param status 状态
     * @return 是否等待
     */
    public static boolean isWaiting(ExecutionStatus status) {
        return status != null && status.isWaiting();
    }

    /**
     * 判断状态是否为终态
     *
     * @param status 状态
     * @return 是否为终态
     */
    public static boolean isTerminal(ExecutionStatus status) {
        return status != null && status.isTerminal();
    }
}

