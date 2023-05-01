package wang.liangchen.matrix.framework.commons.validation;

import org.hibernate.validator.internal.util.stereotypes.Immutable;
import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;

import java.util.*;

/**
 * @author Liangchen.Wang 2023-05-01 12:50
 */
public class MatrixResourceBundle extends ResourceBundle {
    @Immutable
    private final Map<Locale, Map<String, Object>> contents = new HashMap<Locale, Map<String, Object>>() {{
        put(Locale.ENGLISH, new HashMap<String, Object>() {{
            put("Parameter.NotNull", "Parameter must not be null");
            put("Collection.NotEmpty", "Collection parameter must not be empty");
        }});
        put(Locale.CHINA, new HashMap<String, Object>() {{
            put("Parameter.NotNull", "参数不能为null");
            put("Collection.NotEmpty", "集合参数不能为空");
        }});
    }};
    private final List<String> defaultBasenames = Arrays.asList("wang.liangchen.matrix.framework.validator.messages", "messages", "i18n.messages");
    private final Locale locale;
    private final Map<String, Object> messages;

    public MatrixResourceBundle(Locale locale) {
        this.locale = locale;
        messages = contents.getOrDefault(locale, new HashMap<>());

        // 补充载入预制的
        for (String basename : defaultBasenames) {
            PlatformResourceBundleLocator platformResourceBundleLocator = new PlatformResourceBundleLocator(basename);
            ResourceBundle resourceBundle = platformResourceBundleLocator.getResourceBundle(locale);
            if (null == resourceBundle) {
                continue;
            }
            Enumeration<String> keys = resourceBundle.getKeys();
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                messages.put(key, resourceBundle.getObject(key));
            }
        }
    }

    @Override
    protected Object handleGetObject(String key) {
        return messages.get(key);
    }

    @Override
    public boolean containsKey(String key) {
        return messages.containsKey(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(messages.keySet());
    }

    @Override
    public Locale getLocale() {
        return this.locale;
    }
}
