package wang.liangchen.matrix.framework.ddd.domain;

/**
 * @author Liangchen.Wang
 * 领域驱动设计和敏捷开发一样都是一种思想，切忌照本宣科，生搬硬套
 * 为平衡开发成本与质量，核心域选择领域模型模式(DomainModel);而支撑域可以选择事务脚本模式(TransactionScript)
 */
public enum DomainType {
    //
    Core("核心域"), Generic("通用域"), Supporting("支撑域");
    private final String summary;

    DomainType(String summary) {
        this.summary = summary;
    }
}