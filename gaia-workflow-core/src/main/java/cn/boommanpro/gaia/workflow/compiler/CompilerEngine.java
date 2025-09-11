package cn.boommanpro.gaia.workflow.compiler;

import cn.boommanpro.gaia.workflow.compiler.pojo.CompileResult;

public interface CompilerEngine {
    CompileResult loadClass(String script);
}
