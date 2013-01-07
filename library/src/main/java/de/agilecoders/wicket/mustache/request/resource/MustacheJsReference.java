package de.agilecoders.wicket.mustache.request.resource;

import de.agilecoders.wicket.webjars.request.resource.WebjarsJavaScriptResourceReference;

/**
 * Static resource reference for mustache javascript resources.
 *
 * @author miha
 */
public class MustacheJsReference extends WebjarsJavaScriptResourceReference {
    private static final String FILENAME = "mustachejs/%s/mustache.js";

    /**
     * instance holder of {@link MustacheJsReference}
     */
    private static final class Holder {
        private static final MustacheJsReference instance = new MustacheJsReference("0.7.0");
    }

    /**
     * @return unique {@link MustacheJsReference} instance
     */
    public static MustacheJsReference instance() {
        return Holder.instance;
    }

    /**
     * Construct. Uses the recent mustache version.
     */
    public MustacheJsReference() {
        this("0.7.0");
    }

    /**
     * Construct.
     *
     * @param version mustache js version to use. Please update your {@link /pom.xml} too
     */
    public MustacheJsReference(final String version) {
        super(String.format(FILENAME, version));
    }
}
