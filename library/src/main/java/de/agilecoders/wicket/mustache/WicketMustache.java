package de.agilecoders.wicket.mustache;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import de.agilecoders.wicket.mustache.markup.html.MustachePanel;
import de.agilecoders.wicket.webjars.util.Webjars;
import org.apache.wicket.Application;
import org.apache.wicket.model.IModel;
import org.apache.wicket.util.lang.Args;
import org.apache.wicket.util.resource.IResourceStream;
import org.apache.wicket.util.string.Strings;

import java.io.Reader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

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
     * Convenience factory method to create a {@link de.agilecoders.wicket.mustache.markup.html.MustachePanel} instance with a given
     * {@link IResourceStream} template resource.
     *
     * @param id               Component id
     * @param model            optional model for variable substitution.
     * @param templateResource The template resource
     * @return an instance of {@link de.agilecoders.wicket.mustache.markup.html.MustachePanel}
     */
    public static MustachePanel newMustacheTemplatePanel(final String id, final IModel<IScope> model, final IResourceStream templateResource) {
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
    public static String compile(final Reader templateReader, final String templateId, final IScope data) {
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
    public static String compile(final Reader templateReader, final String templateId, final IScope data, final boolean escapeHtml) {
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
    public static void install(Application app) {
        Webjars.install(app);
    }


    public static <V> ScopedList<V> newScopedList(final List<V> list) {
        return new ScopedList<V>(list);
    }

    public static <K, V> ScopedMap<K, V> newScopedMap(final Map<K, V> map) {
        return new ScopedMap<K, V>(map);
    }

    public static final class ScopedList<V> implements List<V>, IScope {

        private final List<V> list;

        public ScopedList(List<V> list) {
            this.list = list;
        }

        @Override
        public int size() {
            return list.size();
        }

        @Override
        public boolean isEmpty() {
            return list.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return list.contains(o);
        }

        @Override
        public Iterator<V> iterator() {
            return list.iterator();
        }

        @Override
        public Object[] toArray() {
            return list.toArray();
        }

        @Override
        public <T> T[] toArray(T[] ts) {
            return list.toArray(ts);
        }

        @Override
        public boolean add(V v) {
            return list.add(v);
        }

        @Override
        public boolean remove(Object o) {
            return list.remove(o);
        }

        @Override
        public boolean containsAll(Collection<?> objects) {
            return list.containsAll(objects);
        }

        @Override
        public boolean addAll(Collection<? extends V> vs) {
            return list.addAll(vs);
        }

        @Override
        public boolean addAll(int i, Collection<? extends V> vs) {
            return list.addAll(i, vs);
        }

        @Override
        public boolean removeAll(Collection<?> objects) {
            return list.removeAll(objects);
        }

        @Override
        public boolean retainAll(Collection<?> objects) {
            return list.retainAll(objects);
        }

        @Override
        public void clear() {
            list.clear();
        }

        @Override
        public V get(int i) {
            return list.get(i);
        }

        @Override
        public V set(int i, V v) {
            return list.set(i, v);
        }

        @Override
        public void add(int i, V v) {
            list.add(i, v);
        }

        @Override
        public V remove(int i) {
            return list.remove(i);
        }

        @Override
        public int indexOf(Object o) {
            return list.indexOf(o);
        }

        @Override
        public int lastIndexOf(Object o) {
            return list.lastIndexOf(o);
        }

        @Override
        public ListIterator<V> listIterator() {
            return list.listIterator();
        }

        @Override
        public ListIterator<V> listIterator(int i) {
            return list.listIterator(i);
        }

        @Override
        public List<V> subList(int i, int i2) {
            return list.subList(i, i2);
        }
    }

    public static final class ScopedMap<K, V> implements Map<K, V>, IScope {

        private final Map<K, V> map;

        public ScopedMap(Map<K, V> map) {
            this.map = map;
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public boolean isEmpty() {
            return map.isEmpty();
        }

        @Override
        public boolean containsKey(Object o) {
            return map.containsKey(o);
        }

        @Override
        public boolean containsValue(Object o) {
            return map.containsValue(o);
        }

        @Override
        public V get(Object o) {
            return map.get(o);
        }

        @Override
        public V put(K k, V v) {
            return map.put(k, v);
        }

        @Override
        public V remove(Object o) {
            return map.remove(o);
        }

        @Override
        public void putAll(Map<? extends K, ? extends V> map) {
            this.map.putAll(map);
        }

        @Override
        public void clear() {
            map.clear();
        }

        @Override
        public Set<K> keySet() {
            return map.keySet();
        }

        @Override
        public Collection<V> values() {
            return map.values();
        }

        @Override
        public Set<Entry<K, V>> entrySet() {
            return map.entrySet();
        }
    }

    /**
     * private constructor.
     */
    private WicketMustache() {
        throw new UnsupportedOperationException();
    }
}
