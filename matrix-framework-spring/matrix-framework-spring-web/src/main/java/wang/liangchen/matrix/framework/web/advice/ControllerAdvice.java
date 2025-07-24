package wang.liangchen.matrix.framework.web.advice;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wang.liangchen.matrix.framework.web.response.JsonResponse;

@RestControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(Throwable.class)
    public JsonResponse<?> exceptionHandler(Throwable throwable) {
        return JsonResponse.failure(throwable);
    }

}
