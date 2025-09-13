package cn.boommanpro.gaia.workflow.compiler.java;


import cn.hutool.json.JSONUtil;
import cn.boommanpro.gaia.workflow.compiler.CompilerEngine;
import cn.boommanpro.gaia.workflow.compiler.exception.CompileException;
import cn.boommanpro.gaia.workflow.compiler.java.dependency.JavaCompiler;
import cn.boommanpro.gaia.workflow.compiler.java.dependency.MemoryClassLoader;
import cn.boommanpro.gaia.workflow.compiler.pojo.CompileResult;
import cn.boommanpro.gaia.workflow.compiler.pojo.CompilerCache;
import cn.boommanpro.gaia.workflow.compiler.pojo.JavaCompilerResult;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.Base64;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class JavaCompilerEngine implements CompilerEngine {

    private static final JavaCompiler javaCompiler = new JavaCompiler();

    public static final MemoryClassLoader defaultClassLoader = new MemoryClassLoader();
    
    // 添加缓存，使用LoadingCache自动处理缓存加载和过期
    private static final LoadingCache<String, CompileResult> scriptCache = CacheBuilder.newBuilder()
            .maximumSize(1000) // 最多缓存1000个脚本
            .expireAfterWrite(1, TimeUnit.HOURS) // 缓存1小时后过期
            .build(new CacheLoader<String, CompileResult>() {
                @Override
                public CompileResult load(String script) throws Exception {
                    try {
                        JavaCompilerResult compile = javaCompiler.compile(script, defaultClassLoader);
                        return CompileResult.success(compile.getMainClass(), compile.getClassList());
                    } catch (Exception e) {
                        return CompileResult.otherException(e);
                    }
                }
            });

    @Override
    public CompileResult loadClass(String script) {
        try {
            return scriptCache.get(script);
        } catch (ExecutionException e) {
            // 如果缓存加载过程中出现异常，返回异常结果
            return CompileResult.otherException(e);
        }
    }

    public static Class<?> from(String cacheJson) {
        // 1. 反序列化 JSON
        CompilerCache cache = JSONUtil.parseObj(cacheJson).toBean(CompilerCache.class);
        String mainClassName = cache.getMainClassName();
        Map<String, String> encodeClassMap = cache.getEncodeClassMap();

        if (encodeClassMap == null || encodeClassMap.isEmpty()) {
            throw new CompileException("No classes found in cache");
        }

        for (Map.Entry<String, String> entry : encodeClassMap.entrySet()) {
            String className = entry.getKey();
            String base64Bytes = entry.getValue();
            byte[] classBytes = Base64.getDecoder().decode(base64Bytes);
            defaultClassLoader.addClass(className, classBytes);
        }

        try {
            return defaultClassLoader.loadClass(mainClassName);
        } catch (ClassNotFoundException e) {
            throw new CompileException("load class fail", e);
        }
    }

}