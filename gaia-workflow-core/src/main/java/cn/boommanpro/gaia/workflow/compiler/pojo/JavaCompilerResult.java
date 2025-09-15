package cn.boommanpro.gaia.workflow.compiler.pojo;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class JavaCompilerResult {

    private Class<?> mainClass;
    private List<Class<?>> classList;
    private Map<String, byte[]> classBytesMap;
    
}