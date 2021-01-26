package org.cruzl.thymeleaf.layout;

import org.cruzl.thymeleaf.layout.utils.PdfUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class ThymeleafLayoutConfig {

    private static final String CHARSET = "; charset={charset}";

    @Autowired
    private SpringTemplateEngine templateEngine;

    private final ThymeleafLayoutProperties properties;

    public ThymeleafLayoutConfig(final ThymeleafLayoutProperties properties) {
        this.properties = properties;
    }

    @Bean
    public ThymeleafViewResolver thymeleafViewResolver(@NonNull final MessageSource messageSource) {
        final ThymeleafLayoutViewResolver thymeleafViewResolver = new ThymeleafLayoutViewResolver();
        thymeleafViewResolver.setViewClass(ThymeleafLayoutView.class);
        thymeleafViewResolver.setLayout(this.properties.getLayoutPath());
        thymeleafViewResolver.setTemplateEngine(this.templateEngine);
        thymeleafViewResolver.setContentType(
                MediaType.TEXT_HTML_VALUE.concat(CHARSET.replace("{charset}", this.properties.getEncoding())));
        thymeleafViewResolver.setCharacterEncoding(this.properties.getEncoding());

        return thymeleafViewResolver;
    }

    @Bean
    public SpringResourceTemplateResolver xhtmlTemplateResolver() {
        final SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
        templateResolver.setPrefix(this.properties.getTemplatePrefix());
        templateResolver.setSuffix(this.properties.getTemplateSuffix());
        templateResolver.setCharacterEncoding(this.properties.getEncoding());
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCheckExistence(false);
        templateResolver.setOrder(1);

        return templateResolver;
    }
    
	@Bean
	public PdfUtils pdfUtils() {
		return new PdfUtils(this.templateEngine);
	}

}
