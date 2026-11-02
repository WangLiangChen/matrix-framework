package wang.liangchen.matrix.product.contract.message.request;
import java.util.UUID;
public record CategoryAssignmentRequest(UUID categoryId, boolean primary) {}
