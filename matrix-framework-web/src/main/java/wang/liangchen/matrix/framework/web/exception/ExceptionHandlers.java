package wang.liangchen.matrix.framework.web.exception;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wang.liangchen.matrix.framework.web.response.FormattedResponse;

/**
 * @author Liangchen.Wang
 */
@RestControllerAdvice
public class ExceptionHandlers {
    @ExceptionHandler(Throwable.class)
    public FormattedResponse exceptionHandler(Exception ex) {
        return FormattedResponse.exception(ex);
    }
}
