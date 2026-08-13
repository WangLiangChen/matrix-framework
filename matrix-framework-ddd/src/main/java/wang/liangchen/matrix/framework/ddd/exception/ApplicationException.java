package wang.liangchen.matrix.framework.ddd.exception;

import wang.liangchen.matrix.framework.ddd.domain.DomainException;

/**
 * @author Liangchen.Wang
 */
public class ApplicationException extends RuntimeException {
    public ApplicationException(String message, DomainException ex) {
        super(message, ex);
    }
}
