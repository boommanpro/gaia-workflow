package cn.boommanpro.gaia.workflow.compiler.pojo;

import cn.boommanpro.gaia.workflow.compiler.java.dependency.JavaCompiler;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
public class CompileResult {

    protected final Class<?> clazz;
    protected final List<Class<?>> classList;
    protected final CompileResultCode code;
    protected final String msg;

    public static CompileResult success(Class<?> result) {
        return new CompileResult(result, Arrays.asList(result), CompileResultCode.SUCCESS, "ok");
    }

    public static CompileResult success(Class<?> result, List<Class<?>> classList) {
        return new CompileResult(result, classList, CompileResultCode.SUCCESS, "ok");
    }


    public static CompileResult compileException(Exception e) {
        return new CompileResult(null, null, CompileResultCode.COMPILE_EXCEPTION, e.getMessage());
    }


    public static CompileResult otherException(Exception e) {
        return new CompileResult(null, null, CompileResultCode.OTHER_EXCEPTION, e != null ? e.getMessage() : null);
    }

    /**
     * only support java type
     *
     * @return
     * @throws Exception
     */
    public CompilerCache getCompilerCache() throws Exception {
        CompilerCache compilerCache = new CompilerCache();
        compilerCache.setMainClassName(clazz.getName());

        Map<String, String> encodeCacheMap = classList.stream().collect(Collectors.toMap(
                Class::getName,
                cls -> {
                    try {
                        byte[] classBytes = JavaCompiler.getBytes(cls.getName());
                        return Base64.getEncoder().encodeToString(classBytes);
                    } catch (Exception e) {
                        throw new RuntimeException("Failed to read class bytes for " + cls.getName(), e);
                    }
                }
        ));

        compilerCache.setEncodeClassMap(encodeCacheMap);
        return compilerCache;
    }
}
