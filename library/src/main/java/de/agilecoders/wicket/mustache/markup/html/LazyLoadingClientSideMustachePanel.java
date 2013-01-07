package de.agilecoders.wicket.mustache.markup.html;

import org.apache.wicket.ajax.AbstractDefaultAjaxBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.time.Duration;

/**
 * lazy loading version of {@link ClientSideMustachePanel}.
 *
 * @author miha
 */
public abstract class LazyLoadingClientSideMustachePanel extends ClientSideMustachePanel {
    private final AbstractDefaultAjaxBehavior ajaxBehavior;

    /**
     * Construct.
     *
     * @param id component id
     */
    public LazyLoadingClientSideMustachePanel(String id) {
        this(id, null);
    }

    /**
     * Construct.
     *
     * @param id    component id
     * @param model template data
     */
    public LazyLoadingClientSideMustachePanel(String id, IModel<Object> model) {
        super(id, model);

        add(ajaxBehavior = new AbstractDefaultAjaxBehavior() {
            @Override
            protected void respond(AjaxRequestTarget target) {
                target.appendJavaScript(LazyLoadingClientSideMustachePanel.super.createScript());
            }
        });
    }

    /**
     * @return delay in milliseconds
     */
    protected Duration delay() {
        return Duration.seconds(0);
    }

    /**
     * @return loading message
     */
    protected CharSequence loading() {
        return "loading...";
    }

    @Override
    protected CharSequence createScript() {
        return "setTimeout(function(){" + ajaxBehavior.getCallbackScript() + " }, " + delay().getMilliseconds() + ");";
    }

    @Override
    protected CharSequence newMarkup() {
        return "<wicket:panel>" + loading() + "</wicket:panel>";
    }
}
