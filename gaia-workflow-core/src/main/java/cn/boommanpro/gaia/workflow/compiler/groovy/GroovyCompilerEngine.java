package cn.boommanpro.gaia.workflow.compiler.groovy;

import cn.boommanpro.gaia.workflow.compiler.CompilerEngine;
import cn.boommanpro.gaia.workflow.compiler.pojo.CompileResult;
import groovy.lang.GroovyClassLoader;

public class GroovyCompilerEngine implements CompilerEngine {

    private static final GroovyClassLoader loader = new GroovyClassLoader();

    @Override
    public CompileResult loadClass(String script) {
        try {
            Class<?> result = loader.parseClass(script);
            return CompileResult.success(result);
        } catch (Exception e) {
            return CompileResult.otherException(e);
        }
    }
}
