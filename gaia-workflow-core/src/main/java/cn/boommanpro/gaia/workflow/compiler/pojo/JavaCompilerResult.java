package cn.boommanpro.gaia.workflow.compiler.pojo;

import lombok.Data;

import java.util.List;

@Data
public class JavaCompilerResult {
    private Class<?> mainClass;
    private List<Class<?>> classList;
}
