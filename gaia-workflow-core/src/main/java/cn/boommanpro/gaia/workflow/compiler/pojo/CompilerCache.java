package cn.boommanpro.gaia.workflow.compiler.pojo;

import lombok.Data;

import java.util.Map;

@Data
public class CompilerCache {
    private String mainClassName;
    private Map<String, String> encodeClassMap;
}
