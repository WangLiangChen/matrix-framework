package wang.liangchen.matrix.framework.springboot.messages;

import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;
import org.springframework.context.MessageSource;
import org.springframework.util.Assert;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Liangchen.Wang 2023-05-01 13:23
 */
public class MatrixMessageSourceResourceBundleLocator implements ResourceBundleLocator {

    private final MessageSource messageSource;

    public MatrixMessageSourceResourceBundleLocator(MessageSource messageSource) {
        Assert.notNull(messageSource, "MessageSource must not be null");
        this.messageSource = messageSource;
    }

    @Override
    public ResourceBundle getResourceBundle(Locale locale) {
        return new MatrixMessageSourceResourceBundle(this.messageSource, locale);
    }

}