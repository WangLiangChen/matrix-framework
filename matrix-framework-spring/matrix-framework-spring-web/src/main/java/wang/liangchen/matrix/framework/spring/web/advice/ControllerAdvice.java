package wang.liangchen.matrix.framework.spring.web.advice;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wang.liangchen.matrix.framework.spring.web.response.JsonResponse;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(Throwable.class)
    public JsonResponse<?> exceptionHandler(Throwable throwable, HttpServletResponse response) {
        HttpStatus httpStatus = resolveException(throwable);
        response.setStatus(httpStatus.value());
        return JsonResponse.failure(throwable);
    }

    private HttpStatus resolveException(Throwable throwable) {
        String className = throwable.getClass().getSimpleName();
        return switch (className) {
            case "NoHandlerFoundException", "NoResourceFoundException" -> HttpStatus.NOT_FOUND;
            default -> HttpStatus.OK;
        };
    }

}
