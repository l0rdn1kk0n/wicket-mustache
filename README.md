wicket-mustache
===============

Provides a specialized panel and some related utilities that enables users to work with Mustache and Apache Wicket.

Current build status: [![Build Status](https://buildhive.cloudbees.com/job/l0rdn1kk0n/job/wicket-mustache/badge/icon)](https://buildhive.cloudbees.com/job/l0rdn1kk0n/job/wicket-mustache/)

**wicket-mustache** dependes on [mustache.java](https://github.com/spullara/mustache.java).

Documentation:

- [Mustache.js manual](http://mustache.github.com/mustache.5.html)
- [Mustache.java manual](https://github.com/spullara/mustache.java)
- Passes all of the `mustache` [specification tests](https://github.com/mustache/spec) modulo whitespace differences


Maven dependency:

```xml
<dependency>
  <groupId>de.agilecoders.wicket.mustache</groupId>
  <artifactId>wicket-mustache</artifactId>
  <version>0.1.0-SNAPSHOT</version>
</dependency>
```

Example template file:

	{{#items}}
	Name: {{name}}
	Price: {{price}}
	  {{#features}}
	  Feature: {{description}}
	  {{/features}}
	{{/items}}

Might be powered by some backing code:

```java
public class Context implements IScope {
  List<Item> items() {
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
```

And would result in:

	Name: Item 1
	Price: $19.99
	  Feature: New!
	  Feature: Awesome!
	Name: Item 2
	Price: $29.99
	  Feature: Old.
	  Feature: Ugly.

Evaluation of the template proceeds serially. For instance, if you have blocking code within one of your callbacks
you the system will pause while executing them:

```java
static class Feature {
  Feature(String description) {
    this.description = description;
  }

  String description() throws InterruptedException {
    Thread.sleep(1000);
    return description;
  }
}
```

If you change description to return a `Callable` instead it will automatically be executed in a separate
thread if you have provided an `ExecutorService` when you created your `MustacheFactory`.

```java
Callable<String> description() throws InterruptedException {
  return new Callable<String>() {

    @Override
    public String call() throws Exception {
      Thread.sleep(1000);
      return description;
    }
  };
}
```

This enables scheduled tasks, streaming behavior and asynchronous i/o. Check out the `samples` module in order
to see a complete end-to-end example:

```java
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
```
