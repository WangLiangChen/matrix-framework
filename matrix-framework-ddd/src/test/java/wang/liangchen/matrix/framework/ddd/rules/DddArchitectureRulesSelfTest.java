package wang.liangchen.matrix.framework.ddd.rules;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

/**
 * 框架规则集自测（dogfooding）：框架自身的包结构必须遵守自己发布的架构规则。
 * packageAnnotationRules 不参与自检：框架根包不是业务限界上下文，不标注@BoundedContextPackage。
 * 分析范围排除test-classes（DO_NOT_INCLUDE_TESTS），避免测试夹具（位于框架包名下）混入自检。
 */
class DddArchitectureRulesSelfTest {

    private static final String FRAMEWORK_ROOT = "wang.liangchen.matrix.framework.ddd";

    private static final JavaClasses FRAMEWORK_CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(FRAMEWORK_ROOT);

    @Test
    void layeredDependencyRules() {
        DddArchitectureRules.layeredDependencyRules(FRAMEWORK_ROOT).check(FRAMEWORK_CLASSES);
    }

    @Test
    void domainModelRules() {
        DddArchitectureRules.domainModelRules(FRAMEWORK_ROOT).check(FRAMEWORK_CLASSES);
    }

    @Test
    void messageContractRules() {
        DddArchitectureRules.messageContractRules(FRAMEWORK_ROOT).check(FRAMEWORK_CLASSES);
    }

    @Test
    void architectureAnnotationRules() {
        DddArchitectureRules.architectureAnnotationRules(FRAMEWORK_ROOT).check(FRAMEWORK_CLASSES);
    }
}
