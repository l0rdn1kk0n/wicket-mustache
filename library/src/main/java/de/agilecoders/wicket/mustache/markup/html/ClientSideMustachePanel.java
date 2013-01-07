package de.agilecoders.wicket.mustache.markup.html;

import de.agilecoders.wicket.mustache.IScope;
import de.agilecoders.wicket.mustache.request.resource.MustacheJsReference;
import de.agilecoders.wicket.mustache.util.Json;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.IMarkupResourceStreamProvider;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.resource.ResourceUtil;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.StringResourceStream;

/**
 * Panel that displays the result of rendering a mustache template. The template itself can be any
 * {@link IResourceStream} implementation, of which there are a number of convenient
 * implementations in the {@link org.apache.wicket.util} package. The model can be any serializable
 * object, which will be used by mustache while rendering the template. The template will be rendered
 * on client side.
 *
 * @author miha
 */
public abstract class ClientSideMustachePanel extends GenericPanel<IScope> implements IMarkupResourceStreamProvider {

    /**
     * Construct.
     *
     * @param id the component id
     */
    public ClientSideMustachePanel(String id) {
        this(id, null);
    }

    /**
     * Construct.
     *
     * @param id    the component id
     * @param model the template data
     */
    public ClientSideMustachePanel(String id, IModel<IScope> model) {
        super(id, model);

        setOutputMarkupId(true);
        setOutputMarkupPlaceholderTag(true);

        add(new AttributeModifier("data-template", new LoadableDetachableModel<String>() {
            @Override
            public String load() {
                return newTemplate();
            }
        }));
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);

        if (size() > 0) {
            throw new WicketRuntimeException("you can't add components to a ClientSideMustachePanel");
        }

        response.render(JavaScriptHeaderItem.forReference(MustacheJsReference.instance()));
        response.render(OnDomReadyHeaderItem.forScript(createScript()));
    }

    /**
     * @return new javascript that renders mustache compiled content into panels body.
     */
    private CharSequence createScript() {
        return "$(\"#" + getMarkupId(true) + "\").html(Mustache.render($(\"#" + getMarkupId(true) + "\").attr('data-template'), " + Json.stringify(getModelObject()) + "))";
    }

    /**
     * Returns the template resource passed to the constructor.
     *
     * @return The template resource
     */
    protected abstract IResourceStream newTemplateResourceStream();

    /**
     * Gets a new reader for the mustache template.
     *
     * @return reader for the mustache template
     */
    protected final String newTemplate() {
        final IResourceStream resource = newTemplateResourceStream();
        if (resource == null) {
            throw new IllegalArgumentException("newTemplateResourceStream must return a resource");
        }

        final String template = ResourceUtil.readString(resource);
        if (template != null) {
            return template;
        }

        throw new IllegalArgumentException("can't find template content on given resource.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final IResourceStream getMarkupResourceStream(final MarkupContainer container, final Class<?> containerClass) {
        // evaluate the template and return a new StringResourceStream
        return new StringResourceStream("<wicket:panel></wicket:panel>");
    }
}
