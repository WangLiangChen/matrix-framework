package wang.liangchen.matrix.framework.commons.validation;

import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Liangchen.Wang 2022-09-16 12:33
 */
class MatrixResourceBundleLocator implements ResourceBundleLocator {
    @Override
    public ResourceBundle getResourceBundle(Locale locale) {
        return new MatrixResourceBundle(locale);
    }
}
