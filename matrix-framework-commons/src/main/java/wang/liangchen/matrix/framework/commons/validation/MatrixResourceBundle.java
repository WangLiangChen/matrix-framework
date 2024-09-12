package wang.liangchen.matrix.framework.commons.validation;

import org.hibernate.validator.resourceloading.PlatformResourceBundleLocator;

import java.util.*;

/**
 * @author Liangchen.Wang 2023-05-01 12:50
 */
public class MatrixResourceBundle extends ResourceBundle {

    private final Locale locale;
    private final Map<String, Object> messages = new HashMap<>();


    public MatrixResourceBundle(Locale locale) {
        this.locale = locale;

        final List<String> defaultBasenames = Arrays.asList(
                "org.hibernate.validator.ValidationMessages",
                "wang.liangchen.matrix.framework.validator.ValidationMessages");

        // 补充载入预制的
        for (String basename : defaultBasenames) {
            PlatformResourceBundleLocator platformResourceBundleLocator = new PlatformResourceBundleLocator(basename);
            ResourceBundle resourceBundle = platformResourceBundleLocator.getResourceBundle(locale);
            if (null == resourceBundle) {
                continue;
            }
            resourceBundle.keySet().forEach(key -> messages.put(key, resourceBundle.getObject(key)));
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
