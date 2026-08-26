package wang.liangchen.matrix.shop.order.northbound.remote;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import wang.liangchen.matrix.shop.order.northbound.exception.ApplicationException;

/**
 * 应用异常处理器：将应用层用例异常转换为客户端可读的响应。
 */
@RestControllerAdvice("wang.liangchen.matrix.shop.order.northbound.remote")
public class OrderApplicationExceptionHandler {

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<String> handle(ApplicationException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
