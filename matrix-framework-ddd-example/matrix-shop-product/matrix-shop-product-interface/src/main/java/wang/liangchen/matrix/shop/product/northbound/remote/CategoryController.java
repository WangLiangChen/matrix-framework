package wang.liangchen.matrix.shop.product.northbound.remote;

import org.springframework.web.bind.annotation.*;
import wang.liangchen.matrix.framework.ddd.northbound.remote.IControllerRemote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.Remote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.RemoteType;
import wang.liangchen.matrix.shop.product.message.request.CategoryQueryRequest;
import wang.liangchen.matrix.shop.product.message.request.CreateCategoryCommandRequest;
import wang.liangchen.matrix.shop.product.message.request.MoveCategoryCommandRequest;
import wang.liangchen.matrix.shop.product.message.request.RenameCategoryCommandRequest;
import wang.liangchen.matrix.shop.product.message.response.CategoryView;
import wang.liangchen.matrix.shop.product.message.response.CreateCategoryResult;
import wang.liangchen.matrix.shop.product.message.response.MoveCategoryResult;
import wang.liangchen.matrix.shop.product.message.response.RenameCategoryResult;
import wang.liangchen.matrix.shop.product.northbound.local.CategoryCommandApplicationService;
import wang.liangchen.matrix.shop.product.northbound.local.CategoryQueryApplicationService;

import java.util.List;

/**
 * 类目控制器：面向UI的北向远程服务，只操作消息契约。
 */
@RestController
@RequestMapping("/categories")
@Remote(RemoteType.Controller)
public class CategoryController implements IControllerRemote {

    private final CategoryCommandApplicationService commandService;
    private final CategoryQueryApplicationService queryService;

    public CategoryController(CategoryCommandApplicationService commandService, CategoryQueryApplicationService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    public CreateCategoryResult create(@RequestBody CreateCategoryCommandRequest request) {
        return commandService.createCategory(request);
    }

    @PutMapping("/{categoryId}/name")
    public RenameCategoryResult rename(@PathVariable String categoryId, @RequestBody RenameCategoryCommandRequest request) {
        return commandService.renameCategory(new RenameCategoryCommandRequest(categoryId, request.name()));
    }

    @PutMapping("/{categoryId}/parent")
    public MoveCategoryResult move(@PathVariable String categoryId, @RequestBody MoveCategoryCommandRequest request) {
        return commandService.moveCategory(new MoveCategoryCommandRequest(categoryId, request.newParentId()));
    }

    @GetMapping
    public List<CategoryView> tree() {
        return queryService.queryCategoryTree(new CategoryQueryRequest());
    }
}
