package de.agilecoders.wicket.mustache;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.string.Strings;

import java.io.Reader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Base util class.
 *
 * @author miha
 */
public final class WicketMustache {

    /**
     * holds the mustache factory.
     */
    private static final class MustacheHolder {
        private static final MustacheFactory factory = new DefaultMustacheFactory();
    }

    /**
     * Convenience factory method to create a {@link MustachePanel} instance with a given
     * {@link IResourceStream} template resource.
     *
     * @param id               Component id
     * @param model            optional model for variable substitution.
     * @param templateResource The template resource
     * @return an instance of {@link MustachePanel}
     */
    public static MustachePanel newMustacheTemplatePanel(final String id, final IModel<Map<String, Object>> model, final IResourceStream templateResource) {
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
     * compiles given template without any template data. "escapeHtml" is set to false.
     *
     * @param templateReader The template reader
     * @param templateId     The template id
     * @return compiled template
     */
    public static String compile(final Reader templateReader, final String templateId) {
        return compile(templateReader, templateId, new HashMap<String, Object>(), false);
    }

    /**
     * compiles given template with given template data. "escapeHtml" is set to false.
     *
     * @param templateReader The template reader
     * @param templateId     The template id
     * @param data           The template data
     * @return compiled template
     */
    public static String compile(final Reader templateReader, final String templateId, final Map<String, Object> data) {
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
    public static String compile(final Reader templateReader, final String templateId, final Map<String, Object> data, final boolean escapeHtml) {
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
     * private constructor.
     */
    private WicketMustache() {
        throw new UnsupportedOperationException();
    }
}