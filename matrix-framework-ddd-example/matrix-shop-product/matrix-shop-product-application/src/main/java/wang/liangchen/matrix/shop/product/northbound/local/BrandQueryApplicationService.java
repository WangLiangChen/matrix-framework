package wang.liangchen.matrix.shop.product.northbound.local;

import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.ddd.domain.exception.AbstractDomainException;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationServiceType;
import wang.liangchen.matrix.framework.ddd.northbound.local.IQueryApplicationService;
import wang.liangchen.matrix.shop.product.domain.port.BrandQueryPort;
import wang.liangchen.matrix.shop.product.domain.readmodel.BrandSummary;
import wang.liangchen.matrix.shop.product.message.request.BrandQueryRequest;
import wang.liangchen.matrix.shop.product.message.response.BrandView;
import wang.liangchen.matrix.shop.product.northbound.exception.ApplicationException;

import java.util.List;
import java.util.function.Supplier;

/**
 * 品牌查询应用服务：CQRS查询侧，返回品牌列表视图。
 */
@Service
@ApplicationService(ApplicationServiceType.QUERY)
public class BrandQueryApplicationService implements IQueryApplicationService {

    private final BrandQueryPort brandQuery;

    public BrandQueryApplicationService(BrandQueryPort brandQuery) {
        this.brandQuery = brandQuery;
    }

    /**
     * 用例：查询品牌列表。
     */
    public List<BrandView> queryAllBrands(BrandQueryRequest request) {
        return useCase("查询品牌列表", () -> brandQuery.queryAllBrands().stream().map(this::brandView).toList());
    }

    private BrandView brandView(BrandSummary summary) {
        return new BrandView(summary.id().value(), summary.name(), summary.description(), summary.logo());
    }

    private <T> T useCase(String useCaseName, Supplier<T> body) {
        try {
            return body.get();
        } catch (AbstractDomainException ex) {
            throw new ApplicationException("用例[" + useCaseName + "]执行失败：" + ex.getMessage(), ex);
        }
    }
}
