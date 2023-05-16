package wang.liangchen.matrix.framework.springboot.messages;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import wang.liangchen.matrix.framework.commons.validation.MatrixResourceBundle;

import java.util.Enumeration;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author Liangchen.Wang 2023-05-01 12:58
 */
public class MatrixMessageSourceResourceBundle extends ResourceBundle {
    private final MessageSource messageSource;
    private final Locale locale;

    public MatrixMessageSourceResourceBundle(MessageSource source, Locale locale) {
        Assert.notNull(source, "MessageSource must not be null");
        this.messageSource = source;
        this.locale = locale;
        setParent(new MatrixResourceBundle(locale));
    }

    @Override
    @Nullable
    protected Object handleGetObject(String key) {
        try {
            return this.messageSource.getMessage(key, null, this.locale);
        } catch (NoSuchMessageException ex) {
            return null;
        }
    }

    /**
     * This implementation checks whether the target MessageSource can resolve
     * a message for the given key, translating {@code NoSuchMessageException}
     * accordingly. In contrast to ResourceBundle's default implementation in
     * JDK 1.6, this does not rely on the capability to enumerate message keys.
     */
    @Override
    public boolean containsKey(String key) {
        try {
            this.messageSource.getMessage(key, null, this.locale);
            return true;
        } catch (NoSuchMessageException ex) {
            return false;
        }
    }

    @Override
    public Enumeration<String> getKeys() {
        throw new UnsupportedOperationException("MessageSourceResourceBundle does not support enumerating its keys");
    }

}
