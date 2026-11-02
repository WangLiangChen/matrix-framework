package wang.liangchen.matrix.product.client;

import wang.liangchen.matrix.product.contract.message.request.CreateSpuRequest;
import wang.liangchen.matrix.product.contract.message.request.CreateSkuRequest;
import wang.liangchen.matrix.product.contract.message.response.ResourceCreatedResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@FeignClient(name = "product-catalog", url = "${clients.product-catalog.url}")
public interface ProductCatalogFeignClient {
    @PostMapping("/api/v1/tenants/{tenantId}/spus") ResourceCreatedResponse createSpu(@PathVariable("tenantId") UUID tenantId, @RequestBody CreateSpuRequest request);
    @PostMapping("/api/v1/tenants/{tenantId}/spus/{spuId}/skus") ResourceCreatedResponse createSku(@PathVariable("tenantId") UUID tenantId, @PathVariable("spuId") UUID spuId, @RequestBody CreateSkuRequest request);
}
