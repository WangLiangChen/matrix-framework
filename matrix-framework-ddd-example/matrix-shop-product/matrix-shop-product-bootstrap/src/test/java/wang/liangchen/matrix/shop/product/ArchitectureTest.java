package wang.liangchen.matrix.shop.product;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import wang.liangchen.matrix.framework.ddd.rules.DddArchitectureRules;

/**
 * 商品限界上下文架构守护测试：引用框架的DDD架构规则集，
 * 守护分层依赖规则、领域模型规则、消息契约规则、包与架构标注规则、架构放置规则。
 */
class ArchitectureTest {

    private static final String ROOT = "wang.liangchen.matrix.shop.product";
    private static final JavaClasses CLASSES = new ClassFileImporter().importPackages(ROOT);

    @Test
    void layeredDependencyRules() {
        DddArchitectureRules.layeredDependencyRules(ROOT).check(CLASSES);
    }

    @Test
    void domainModelRules() {
        DddArchitectureRules.domainModelRules(ROOT).check(CLASSES);
    }

    @Test
    void messageContractRules() {
        DddArchitectureRules.messageContractRules(ROOT).check(CLASSES);
    }

    @Test
    void packageAnnotationRules() {
        DddArchitectureRules.packageAnnotationRules(ROOT).check(CLASSES);
    }

    @Test
    void architectureAnnotationRules() {
        DddArchitectureRules.architectureAnnotationRules(ROOT).check(CLASSES);
    }

    @Test
    void architecturePlacementRules() {
        DddArchitectureRules.architecturePlacementRules(ROOT).check(CLASSES);
    }
}
