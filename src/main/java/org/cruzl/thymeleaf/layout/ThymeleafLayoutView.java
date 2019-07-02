package org.cruzl.thymeleaf.layout;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.ApplicationContext;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.view.AbstractTemplateView;
import org.thymeleaf.context.IWebContext;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.expression.ThymeleafEvaluationContext;
import org.thymeleaf.spring5.naming.SpringContextVariableNames;
import org.thymeleaf.spring5.view.ThymeleafView;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Setter
@Getter
@Slf4j
public class ThymeleafLayoutView extends ThymeleafView {

    public static final String VIEW = "view";
    private static final String PATH_VARIABLES = "PATH_VARIABLES";
    private static final String PATH_VARIABLE_SELECTOR;

    static {
        String pathVariablesSelectorValue = null;
        try {
            final Field pathVariablesField = View.class.getDeclaredField(PATH_VARIABLES);
            pathVariablesSelectorValue = (String) pathVariablesField.get(null);
        } catch (final NoSuchFieldException | IllegalAccessException ignored) {
            log.trace("Ignored error", ignored);
        }
        PATH_VARIABLE_SELECTOR = pathVariablesSelectorValue;
    }

    private static String CONTENT_TYPE_DEFAULT = MediaType.TEXT_HTML_VALUE + ";charset=UTF-8";

    protected static void addRequestContextAsVariable(final Map<String, Object> model, final String variableName,
            final RequestContext requestContext) throws ServletException {
        if (model.containsKey(variableName)) {
            throw new ServletException("Cannot expose request context in model attribute '" + variableName
                    + "' because of an existing model object of the same name");
        }
        model.put(variableName, requestContext);
    }

    private String layout = null;

    private String vista;

    public ThymeleafLayoutView() {
        super();
    }

    public ThymeleafLayoutView(final String templateName) {
        super(templateName);
    }

    protected void carregaCapcaleres(final HttpServletResponse response) {
        final String contentType = this.getContentType() == null ? CONTENT_TYPE_DEFAULT : this.getContentType();
        final String templateCharacterEncoding = this.getCharacterEncoding();

        response.setLocale(this.getLocale());
        response.setContentType(contentType);

        if (templateCharacterEncoding != null) {
            response.setCharacterEncoding(templateCharacterEncoding);
        }
    }

    protected void escriuResponse(final HttpServletRequest request, final HttpServletResponse response,
            final Map<String, ?> model) throws IOException, ServletException {
        final String resultat = this.getPlantillaProcessada(model, request, response);
        response.getWriter().append(resultat);
    }

    @SuppressWarnings("unchecked")
    public String getPlantillaProcessada(final Map<String, ?> model, final HttpServletRequest request,
            final HttpServletResponse response) throws ServletException {
        final Map<String, Object> modelMerged = (Map<String, Object>) model;
        modelMerged.put(VIEW, this.getVista());
        this.setTemplateName(this.getLayout());

        final IWebContext context = this.preparaContext(modelMerged, request, response);

        if (this.getTemplateEngine() == null) {
            throw new NoSuchElementException("No hi ha un TemplateEngine definit");
        }

        return this.getTemplateEngine().process(this.getLayout(), context);
    }

    protected IWebContext preparaContext(final Map<String, ?> model, final HttpServletRequest request,
            final HttpServletResponse response) throws ServletException {

        final Map<String, Object> mergedModel = new HashMap<>(30);
        mergedModel.put(VIEW, this.getVista());

        if (this.getLocale() == null) {
            throw new IllegalArgumentException("Property 'locale' is required");
        }

        final Map<String, Object> templateStaticVariables = this.getStaticVariables();
        if (templateStaticVariables != null) {
            mergedModel.putAll(templateStaticVariables);
        }

        if (PATH_VARIABLE_SELECTOR != null) {
            @SuppressWarnings("unchecked")
            final Map<String, Object> pathVars = (Map<String, Object>) request.getAttribute(PATH_VARIABLE_SELECTOR);
            if (pathVars != null) {
                mergedModel.putAll(pathVars);
            }
        }

        if (model != null) {
            mergedModel.putAll(model);
        }

        final ApplicationContext applicationContext = this.getApplicationContext();

        final RequestContext requestContext = new RequestContext(request, response, this.getServletContext(),
                mergedModel);

        addRequestContextAsVariable(mergedModel, SpringContextVariableNames.SPRING_REQUEST_CONTEXT, requestContext);
        addRequestContextAsVariable(mergedModel, AbstractTemplateView.SPRING_MACRO_REQUEST_CONTEXT_ATTRIBUTE,
                requestContext);

        final ConversionService conversionService = (ConversionService) request
                .getAttribute(ConversionService.class.getName());
        final ThymeleafEvaluationContext evaluationContext = new ThymeleafEvaluationContext(applicationContext,
                conversionService);

        mergedModel.put(ThymeleafEvaluationContext.THYMELEAF_EVALUATION_CONTEXT_CONTEXT_VARIABLE_NAME,
                evaluationContext);
        return new WebContext(request, response, this.getServletContext(), this.getLocale(), mergedModel);

    }

    @Override
    public void render(final Map<String, ?> model, final HttpServletRequest request, final HttpServletResponse response)
            throws Exception {

        if (this.getLayout() == null) {
            super.render(model, request, response);
        } else {
            this.carregaCapcaleres(response);
            this.escriuResponse(request, response, model);
        }
    }

}