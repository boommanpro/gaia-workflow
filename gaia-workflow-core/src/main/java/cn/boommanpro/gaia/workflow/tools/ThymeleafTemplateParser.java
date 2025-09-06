package cn.boommanpro.gaia.workflow.tools;

import cn.hutool.core.lang.Dict;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

public class ThymeleafTemplateParser {
    public static String parse(String template, Map<String, Object> variables){
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode("TEXT");
        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine.process(template, new Context(Locale.CHINA, variables));
    }
}
