package org.cruzl.thymeleaf.layout;

import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import lombok.NonNull;

@Configuration
@EnableAutoConfiguration
public class ThymeleafLayoutConfig {

    private static final String CHARSET = "; charset={charset}";

    public ThymeleafLayoutConfig(final SpringTemplateEngine templateEngine, final ThymeleafLayoutProperties properties) {
        this.templateEngine = templateEngine;
        this.properties = properties;
    }

    private SpringTemplateEngine templateEngine;
    private ThymeleafLayoutProperties properties;

    @Bean
    public ThymeleafViewResolver thymeleafViewResolver(@NonNull final MessageSource messageSource) {
        final ThymeleafLayoutViewResolver thymeleafViewResolver = new ThymeleafLayoutViewResolver();

        thymeleafViewResolver.setViewClass(ThymeleafLayoutView.class);
        thymeleafViewResolver.setLayout(properties.getLayoutPath());
        thymeleafViewResolver.setTemplateEngine(this.templateEngine);
        thymeleafViewResolver.setContentType(MediaType.TEXT_HTML_VALUE.concat(CHARSET.replace("{charset}", properties.getEncoding())));
        thymeleafViewResolver.setCharacterEncoding(properties.getEncoding());

        return thymeleafViewResolver;
    }

    @Bean
    public SpringResourceTemplateResolver xhtmlTemplateResolver() {
        final SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix("classpath:/templates/");
        templateResolver.setSuffix(properties.getTemplateSuffix());
        templateResolver.setCharacterEncoding(properties.getEncoding());
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCheckExistence(true);
        templateResolver.setCacheable(true);
        templateResolver.setOrder(1);

        return templateResolver;
    }

}
