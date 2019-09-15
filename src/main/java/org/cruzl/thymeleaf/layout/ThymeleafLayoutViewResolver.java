package org.cruzl.thymeleaf.layout;

import java.util.Locale;

import org.springframework.web.servlet.View;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ThymeleafLayoutViewResolver extends ThymeleafViewResolver {

    protected String layout;

    @Override
    protected View loadView(final String viewName, final Locale locale) throws Exception {
        final ThymeleafLayoutView view = (ThymeleafLayoutView) super.loadView(viewName, locale);
        view.setLayout(this.getLayout());
        view.setView(viewName);
        return view;
    }
}