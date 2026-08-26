package wang.liangchen.matrix.shop.product.domain.readmodel;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject;
import wang.liangchen.matrix.shop.product.domain.category.CategoryId;

import java.util.List;

/**
 * 类目摘要：类目树读模型，children表达子类目。
 */
@DomainModel(DomainMetaModel.ValueObject)
public record CategorySummary(CategoryId id, String name, CategoryId parentId, List<CategorySummary> children) implements IValueObject {
}
