package org.cruzl.thymeleaf.layout;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;

/**
 * ThymeleafLayoutProperties
 */
@Getter
@ConfigurationProperties("thymeleaf")
public class ThymeleafLayoutProperties {

    private static final String UTF_8 = "UTF-8";

    private String layoutPath;
    private String encoding = UTF_8;
    private String templateSuffix = ".xhtml";
}