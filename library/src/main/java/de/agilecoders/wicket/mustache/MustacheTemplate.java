package de.agilecoders.wicket.mustache;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.StringHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.string.Strings;

/**
 * An {@link org.apache.wicket.markup.html.IHeaderContributor} implementation that renders a
 * mustache template and writes it to the response.
 *
 * @author miha
 */
public class MustacheTemplate extends Behavior {
    private static final long serialVersionUID = 14121982L;

    private final IModel<String> templateName;
    private final IModel<Object> templateData;

    /**
     * Construct. Default template name is used "${ComponentSimpleClassName}.mustache"
     *
     * @param templateData The template data
     */
    public MustacheTemplate(final IModel<Object> templateData) {
        this(Model.of(""), templateData);
    }

    /**
     * Construct.
     *
     * @param templateName The template name
     * @param templateData The template data
     */
    public MustacheTemplate(final IModel<String> templateName, final IModel<Object> templateData) {
        super();

        Args.notNull(templateData, "templateData");

        this.templateName = templateName;
        this.templateData = templateData;
    }

    @Override
    public void renderHead(Component component, IHeaderResponse response) {
        super.renderHead(component, response);

        final CharSequence content = compile(component);
        if (null != content) {
            response.render(StringHeaderItem.forString(content));
        }
    }

    /**
     * @return whether to escape HTML characters. The default value is false
     */
    protected boolean escapeHtml() {
        return false;
    }

    /**
     * Evaluate the template.
     *
     * @return The evaluated template
     */
    private CharSequence compile(final Component component) {
        if (Strings.isEmpty(templateName.getObject())) {
            templateName.setObject(component.getClass().getSimpleName() + ".mustache");
        }

        try {
            return WicketMustache.compile(WicketMustache.newTemplateReader(templateName.getObject(), component), templateName.getObject(),
                                          templateData.getObject(), escapeHtml());
        } catch (Exception e) {
            throw new WicketRuntimeException("Error while executing mustache template script: " + templateName.getObject(), e);
        }
    }

    @Override
    public void detach(Component component) {
        super.detach(component);

        templateName.detach();
        templateData.detach();
    }
}
