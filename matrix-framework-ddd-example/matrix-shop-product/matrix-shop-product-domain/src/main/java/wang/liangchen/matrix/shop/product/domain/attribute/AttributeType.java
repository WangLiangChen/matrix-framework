package wang.liangchen.matrix.shop.product.domain.attribute;

/**
 * 属性类型：
 * 一般属性——商品的非销售展示信息；
 * 关键属性——确定SPU归属的特征；
 * 销售属性——决定SKU规格的销售特征。
 */
public enum AttributeType {
    General("一般属性"),
    Key("关键属性"),
    Sales("销售属性");

    private final String summary;

    AttributeType(String summary) {
        this.summary = summary;
    }

    public String getSummary() {
        return summary;
    }
}
