package wang.liangchen.matrix.shop.product.northbound.remote;

import org.springframework.web.bind.annotation.*;
import wang.liangchen.matrix.framework.ddd.northbound.remote.IControllerRemote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.Remote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.RemoteType;
import wang.liangchen.matrix.shop.product.message.request.BrandQueryRequest;
import wang.liangchen.matrix.shop.product.message.request.CreateBrandCommandRequest;
import wang.liangchen.matrix.shop.product.message.request.RenameBrandCommandRequest;
import wang.liangchen.matrix.shop.product.message.response.BrandView;
import wang.liangchen.matrix.shop.product.message.response.CreateBrandResult;
import wang.liangchen.matrix.shop.product.message.response.RenameBrandResult;
import wang.liangchen.matrix.shop.product.northbound.local.BrandCommandApplicationService;
import wang.liangchen.matrix.shop.product.northbound.local.BrandQueryApplicationService;

import java.util.List;

/**
 * 品牌控制器：面向UI的北向远程服务，只操作消息契约。
 */
@RestController
@RequestMapping("/brands")
@Remote(RemoteType.Controller)
public class BrandController implements IControllerRemote {

    private final BrandCommandApplicationService commandService;
    private final BrandQueryApplicationService queryService;

    public BrandController(BrandCommandApplicationService commandService, BrandQueryApplicationService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    public CreateBrandResult create(@RequestBody CreateBrandCommandRequest request) {
        return commandService.createBrand(request);
    }

    @PutMapping("/{brandId}/name")
    public RenameBrandResult rename(@PathVariable String brandId, @RequestBody RenameBrandCommandRequest request) {
        return commandService.renameBrand(new RenameBrandCommandRequest(brandId, request.name()));
    }

    @GetMapping
    public List<BrandView> list() {
        return queryService.queryAllBrands(new BrandQueryRequest());
    }
}
