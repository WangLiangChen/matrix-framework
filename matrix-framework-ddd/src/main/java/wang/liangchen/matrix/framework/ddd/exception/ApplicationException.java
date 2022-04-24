package wang.liangchen.matrix.framework.ddd.exception;

/**
 * @author Liangchen.Wang
 */
public class ApplicationException extends RuntimeException {
    public ApplicationException(String message, DomainException ex) {
        super(message, ex);
    }
}
