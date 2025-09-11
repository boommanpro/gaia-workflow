package cn.boommanpro.gaia.workflow.compiler.java.dependency;

import cn.boommanpro.gaia.workflow.compiler.pojo.JavaCompilerResult;

public interface Compiler {

    JavaCompilerResult compile(String script, ClassLoader classLoader);

}
