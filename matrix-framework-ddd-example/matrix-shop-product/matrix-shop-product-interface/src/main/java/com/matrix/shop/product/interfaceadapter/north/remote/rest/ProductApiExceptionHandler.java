package wang.liangchen.matrix.product.interfaceadapter.north.remote.rest;
import wang.liangchen.matrix.shop.product.domain.shared.DomainException;
import org.springframework.http.*; import org.springframework.web.bind.annotation.*; import java.util.Map;
@RestControllerAdvice
public class ProductApiExceptionHandler {
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String,String>> handleDomain(DomainException ex) { return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "DOMAIN_RULE_VIOLATION", "message", ex.getMessage())); }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,String>> handleInput(IllegalArgumentException ex) { return ResponseEntity.badRequest().body(Map.of("error", "INVALID_ARGUMENT", "message", ex.getMessage())); }
}
