package de.agilecoders.wicket.mustache;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import de.agilecoders.wicket.mustache.markup.html.MustachePanel;
import de.agilecoders.wicket.webjars.WicketWebjars;
import org.apache.wicket.Component;
import org.apache.wicket.core.util.resource.PackageResourceStream;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.model.IModel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.resource.ResourceStreamNotFoundException;
import org.apache.wicket.util.string.Strings;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;

/**
 * Base util class.
 *
 * @author miha
 */
public final class WicketMustache {

    public static final String DATA_ID = "data-template";

    /**
     * holds the mustache factory.
     */
    private static final class MustacheHolder {

        private static final MustacheFactory factory = new DefaultMustacheFactory();
    }

    /**
     * Convenience factory method to create a {@link de.agilecoders.wicket.mustache.markup.html.MustachePanel} instance with a given
     * {@link IResourceStream} template resource.
     *
     * @param id               Component id
     * @param model            optional model for variable substitution.
     * @param templateResource The template resource
     * @return an instance of {@link de.agilecoders.wicket.mustache.markup.html.MustachePanel}
     */
    public static MustachePanel newMustacheTemplatePanel(final String id, final IModel<Object> model, final IResourceStream templateResource) {
        Args.notNull(templateResource, "templateResource");
        Args.notNull(model, "model");

        return new MustachePanel(id, model) {
            private static final long serialVersionUID = 1L;

            @Override
            protected IResourceStream newTemplateResourceStream() {
                return templateResource;
            }
        };
    }

    /**
     * creates a mustache javascript that renders a template with given content.
     *
     * @param component The mustache component
     * @param content   The content to render
     * @return new javascript that renders the mustache template with given content
     */
    public static CharSequence createRenderScript(final Component component, final CharSequence content) {
        final String markupId = component.getMarkupId(true);

        return "$(\"#" + markupId + "\").html(Mustache.render($(\"#" + markupId +
               "\").attr('" + WicketMustache.DATA_ID + "'), " + content + "))";
    }

    /**
     * appends a mustache javascript that renders a template with given content.
     *
     * @param component The mustache component
     * @param response  current header response
     * @param content   The content to render
     */
    public static void appendRenderScript(final Component component, final IHeaderResponse response, final CharSequence content) {
        response.render(OnDomReadyHeaderItem.forScript(createRenderScript(component, content)));
    }

    /**
     * Gets a new reader for the mustache template.
     *
     * @param templateName The name of the template
     * @param component    the reference component
     * @return reader for the mustache template
     */
    public static Reader newTemplateReader(final String templateName, final Component component) throws ResourceStreamNotFoundException {
        return new InputStreamReader(new PackageResourceStream(component.getClass(), templateName).getInputStream());
    }

    /**
     * compiles given template without any template data. "escapeHtml" is set to false.
     *
     * @param templateReader The template reader
     * @param templateId     The template id
     * @return compiled template
     */
    public static String compile(final Reader templateReader, final String templateId) {
        return compile(templateReader, templateId, null, false);
    }

    /**
     * compiles given template with given template data. "escapeHtml" is set to false.
     *
     * @param templateReader The template reader
     * @param templateId     The template id
     * @param data           The template data
     * @return compiled template
     */
    public static String compile(final Reader templateReader, final String templateId, final Object data) {
        return compile(templateReader, templateId, data, false);
    }

    /**
     * compiles given template with given template data.
     *
     * @param templateReader The template reader
     * @param templateId     The template id
     * @param data           The template data
     * @param escapeHtml     whether to escape HTML characters
     * @return compiled template
     */
    public static String compile(final Reader templateReader, final String templateId, final Object data, final boolean escapeHtml) {
        // create a writer for capturing the mustache output
        final StringWriter writer = new StringWriter();
        final String evaluatedTemplate;

        // execute the mustache script and capture the output in writer
        final Mustache mustache = MustacheHolder.factory.compile(templateReader, templateId);
        mustache.execute(writer, data);
        writer.flush();

        // convert writer to string.
        evaluatedTemplate = writer.toString();

        if (escapeHtml) {
            // encode the result in order to get valid html output that
            // does not break the rest of the page
            return Strings.escapeMarkup(evaluatedTemplate).toString();
        }

        return evaluatedTemplate;
    }

    /**
     * install all mustache configurations
     *
     * @param app current web application
     */
    public static void install(final WebApplication app) {
        WicketWebjars.install(app);
    }

    /**
     * private constructor.
     */
    private WicketMustache() {
        throw new UnsupportedOperationException();
    }
}
