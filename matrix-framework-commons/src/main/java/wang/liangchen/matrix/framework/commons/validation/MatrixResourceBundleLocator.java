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
    }};

    @Override
    public ResourceBundle getResourceBundle(Locale locale) {
        Map<String, Object> messages = contents.get(locale);
        if (null == messages) {
            return null;
        }
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
