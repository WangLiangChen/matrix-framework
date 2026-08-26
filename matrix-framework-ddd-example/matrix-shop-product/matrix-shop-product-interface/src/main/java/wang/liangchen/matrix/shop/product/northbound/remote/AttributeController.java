package wang.liangchen.matrix.shop.product.northbound.remote;

import org.springframework.web.bind.annotation.*;
import wang.liangchen.matrix.framework.ddd.northbound.remote.IControllerRemote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.Remote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.RemoteType;
import wang.liangchen.matrix.shop.product.message.request.AddAttributeOptionCommandRequest;
import wang.liangchen.matrix.shop.product.message.request.AttributeQueryRequest;
import wang.liangchen.matrix.shop.product.message.request.CreateAttributeCommandRequest;
import wang.liangchen.matrix.shop.product.message.response.AddAttributeOptionResult;
import wang.liangchen.matrix.shop.product.message.response.AttributeView;
import wang.liangchen.matrix.shop.product.message.response.CreateAttributeResult;
import wang.liangchen.matrix.shop.product.northbound.local.AttributeCommandApplicationService;
import wang.liangchen.matrix.shop.product.northbound.local.AttributeQueryApplicationService;

import java.util.List;

/**
 * 属性控制器：面向UI的北向远程服务，只操作消息契约。
 */
@RestController
@RequestMapping("/attributes")
@Remote(RemoteType.Controller)
public class AttributeController implements IControllerRemote {

    private final AttributeCommandApplicationService commandService;
    private final AttributeQueryApplicationService queryService;

    public AttributeController(AttributeCommandApplicationService commandService, AttributeQueryApplicationService queryService) {
        this.commandService = commandService;
        this.queryService = queryService;
    }

    @PostMapping
    public CreateAttributeResult create(@RequestBody CreateAttributeCommandRequest request) {
        return commandService.createAttribute(request);
    }

    @PostMapping("/{attributeId}/options")
    public AddAttributeOptionResult addOption(@PathVariable String attributeId, @RequestBody AddAttributeOptionCommandRequest request) {
        return commandService.addAttributeOption(new AddAttributeOptionCommandRequest(attributeId, request.option()));
    }

    @GetMapping
    public List<AttributeView> list() {
        return queryService.queryAllAttributes(new AttributeQueryRequest());
    }
}
