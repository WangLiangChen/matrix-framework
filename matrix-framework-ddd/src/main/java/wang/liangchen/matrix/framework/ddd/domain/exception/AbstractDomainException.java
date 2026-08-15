package wang.liangchen.matrix.framework.ddd.domain.exception;

/**
 * @author Liangchen.Wang
 *
 */
public abstract class AbstractDomainException extends RuntimeException implements IDomainException {

    public AbstractDomainException() {
    }

    public AbstractDomainException(String message) {
        super(message);
    }

    public AbstractDomainException(String message, Throwable cause) {
        super(message, cause);
    }

    public AbstractDomainException(Throwable cause) {
        super(cause);
    }
}
