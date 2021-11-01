package wang.liangchen.matrix.framework.web.exception;

import wang.liangchen.matrix.framework.web.response.FormattedResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author Liangchen.Wang
 */
@RestControllerAdvice
public class ExceptionHandlers {
    @ExceptionHandler(Exception.class)
    public FormattedResponse exceptionHandler(Exception ex) {
        return FormattedResponse.exception(ex);
    }
}
