package wang.liangchen.matrix.framework.commons.validation;

import org.hibernate.validator.internal.util.stereotypes.Immutable;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

import java.util.*;

/**
 * @author Liangchen.Wang 2022-09-16 12:33
 */
class MatrixResourceBundleLocator implements ResourceBundleLocator {
    @Immutable
    private final Map<Locale, Map<String, Object>> contents = new HashMap<Locale, Map<String, Object>>() {{
        put(Locale.ENGLISH, new HashMap<String, Object>() {{
            put("Parameter.NotNull", "Parameter must not be null");
            put("Collection.NotEmpty","Collection parameter must not be empty");
        }});
        put(Locale.CHINA, new HashMap<String, Object>() {{
            put("Parameter.NotNull", "参数不能为null");
            put("Collection.NotEmpty","集合参数不能为空");
        }});
    }};

    @Override
    public ResourceBundle getResourceBundle(Locale locale) {
        locale = contents.containsKey(locale) ? locale : Locale.ENGLISH;
        Map<String, Object> messages = contents.get(locale);
        return new ResourceBundle() {
            @Override
            protected Object handleGetObject(String key) {
                return messages.get(key);
            }

            @Override
            public Enumeration<String> getKeys() {
                return Collections.enumeration(messages.keySet());
            }
        };
    }

}
