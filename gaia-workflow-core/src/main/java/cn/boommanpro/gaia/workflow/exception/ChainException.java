package cn.boommanpro.gaia.workflow.exception;

/**
 * 代码功能
 *
 * @author <a href="mailto:wangqimeng03@meituan.com">wangqimeg03</a>
 * @date 2025/05/17 14:52
 */
public class ChainException extends RuntimeException {
    public ChainException(Exception exception) {

    }

    public ChainException(String message) {
        super(message);
    }
}
