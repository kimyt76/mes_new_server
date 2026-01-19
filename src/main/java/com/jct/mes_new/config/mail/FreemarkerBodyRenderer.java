package com.jct.mes_new.config.mail;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class FreemarkerBodyRenderer {
    private final freemarker.template.Configuration freemarker;

    public FreemarkerBodyRenderer(freemarker.template.Configuration freemarker) {
        this.freemarker = freemarker;
    }

    public String render(String templatePath, Map<String, Object> model) {
        try {
            var tpl = freemarker.getTemplate(templatePath);
            var out = new java.io.StringWriter();
            tpl.process(model == null ? Map.of() : model, out);
            return out.toString();
        } catch (Exception e) {
            throw new RuntimeException("FTL 렌더 실패: " + templatePath, e);
        }
    }
}
