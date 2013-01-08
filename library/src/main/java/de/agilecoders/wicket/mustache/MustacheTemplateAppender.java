package de.agilecoders.wicket.mustache;

import org.apache.wicket.Component;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.io.IOUtils;
import org.apache.wicket.util.string.Strings;

/**
 * Appends a mustache template as data attribute to a assigned component.
 *
 * @author miha
 */
public class MustacheTemplateAppender extends Behavior {
    private static final long serialVersionUID = 14121982L;

    private final IModel<String> templateName;

    /**
     * Construct.
     *
     * @param templateName The template name
     */
    public MustacheTemplateAppender(final IModel<String> templateName) {
        super();

        this.templateName = templateName;
    }

    @Override
    public void onComponentTag(final Component component, final ComponentTag tag) {
        super.onComponentTag(component, tag);

        tag.put(WicketMustache.DATA_ID, loadTemplate(component));
    }

    /**
     * Evaluate the template.
     *
     * @return The evaluated template
     */
    private CharSequence loadTemplate(final Component component) {
        if (Strings.isEmpty(templateName.getObject())) {
            templateName.setObject(component.getClass().getSimpleName() + ".mustache");
        }

        try {
            return IOUtils.toString(WicketMustache.newTemplateReader(templateName.getObject(), component));
        } catch (Exception e) {
            throw new WicketRuntimeException("Error while executing mustache template script: " + templateName.getObject(), e);
        }
    }

    @Override
    public void detach(Component component) {
        super.detach(component);

        templateName.detach();
    }
}
