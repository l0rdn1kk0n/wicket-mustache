package de.agilecoders.wicket;

import de.agilecoders.wicket.mustache.IScope;
import de.agilecoders.wicket.mustache.MustachePanel;
import org.apache.wicket.core.util.resource.PackageResourceStream;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.util.resource.IResourceStream;

import java.util.Arrays;
import java.util.List;

public class HomePage extends WebPage {
    private static final long serialVersionUID = 1L;

    public HomePage(final PageParameters parameters) {
        super(parameters);

        IModel<IScope> scopeModel = new LoadableDetachableModel<IScope>() {
            @Override
            public IScope load() {
                return new Example();
            }
        };

        add(new MustachePanel("template", scopeModel) {
            @Override
            protected IResourceStream newTemplateResourceStream() {
                return new PackageResourceStream(HomePage.class, "template.mustache");
            }
        });
    }

    public static class Example implements IScope {

        public List<Item> items() {
            return Arrays.asList(
                    new Item("Item 1", "$19.99", Arrays.asList(new Feature("New!"), new Feature("Awesome!"))),
                    new Item("Item 2", "$29.99", Arrays.asList(new Feature("Old."), new Feature("Ugly.")))
            );
        }

        static class Item {
            Item(String name, String price, List<Feature> features) {
                this.name = name;
                this.price = price;
                this.features = features;
            }

            String name, price;
            List<Feature> features;
        }

        static class Feature {
            Feature(String description) {
                this.description = description;
            }

            String description;
        }
    }
}
