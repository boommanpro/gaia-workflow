package cn.boommanpro.gaia.workflow.compiler.java.dependency;


public class ScriptIllegalException extends RuntimeException {

    public ScriptIllegalException(Throwable cause) {
        super(cause);
    }

    public ScriptIllegalException(String message) {
        super(message);
    }

    public ScriptIllegalException(String message, Throwable cause) {
        super(message, cause);
    }
}
