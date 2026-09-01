package wang.liangchen.matrix.framework.ddd.rules;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 规则行为测试：以合规夹具（fixture.good）验证规则放行，以违规夹具（fixture.bad）验证规则拦截。
 * 每条规则至少一个正例与一个反例；反例断言按违规类所在子包独立导入（importPackages），
 * 避免不同反例类相互干扰、断言精确到目标类名。
 */
class DddArchitectureRulesBehaviorTest {

    private static final String GOOD = "wang.liangchen.matrix.framework.ddd.rules.fixture.good";
    private static final String BAD = "wang.liangchen.matrix.framework.ddd.rules.fixture.bad";
    private static final String BADROOT = "wang.liangchen.matrix.framework.ddd.rules.fixture.badroot";
    /** 分层依赖反例使用独立根包（fixture.badlayered），其domain/message/northbound/southbound子包满足规则的前缀匹配 */
    private static final String BADLAYERED = "wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered";
    /** 框架自身包：dogfooding验证框架代码同样满足守护规则 */
    private static final String FRAMEWORK = "wang.liangchen.matrix.framework.ddd";
    private static final String BAD_DOMAIN = BAD + ".domain.bad";
    private static final String BAD_DOMAIN_AGG = BAD + ".domain.badagg";
    private static final String BAD_DOMAIN_PORT = BAD + ".domain.port";
    private static final String BAD_MESSAGE = BAD + ".message";
    private static final String BAD_LOCAL = BAD + ".northbound.local";
    private static final String BAD_EVENT = BAD + ".northbound.event";
    private static final String BAD_REMOTE = BAD + ".northbound.remote";
    private static final String BAD_SOUTHBOUND = BAD + ".southbound";
    private static final String BAD_ADAPTER = BAD + ".southbound.adapter";
    private static final String LAYERED_DOMAIN = BADLAYERED + ".domain";
    private static final String LAYERED_DOMAIN_PORT = BADLAYERED + ".domain.port";
    private static final String LAYERED_MESSAGE = BADLAYERED + ".message";
    private static final String LAYERED_LOCAL = BADLAYERED + ".northbound.local";
    private static final String LAYERED_REMOTE = BADLAYERED + ".northbound.remote";
    private static final String LAYERED_ADAPTER = BADLAYERED + ".southbound.adapter";

    private static final JavaClasses GOOD_CLASSES = new ClassFileImporter().importPackages(GOOD);
    /** 框架自身（仅main代码，排除test-classes中的测试固件，避免固件被误检） */
    private static final JavaClasses FRAMEWORK_CLASSES = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages(FRAMEWORK);

    // ---------- 复合规则：合规夹具全部通过 ----------

    @Test
    void layeredDependencyRules_passOnGoodFixture() {
        DddArchitectureRules.layeredDependencyRules(GOOD).check(GOOD_CLASSES);
    }

    @Test
    void domainModelRules_passOnGoodFixture() {
        DddArchitectureRules.domainModelRules(GOOD).check(GOOD_CLASSES);
    }

    @Test
    void messageContractRules_passOnGoodFixture() {
        DddArchitectureRules.messageContractRules(GOOD).check(GOOD_CLASSES);
    }

    @Test
    void packageAnnotationRules_passOnGoodFixture() {
        DddArchitectureRules.packageAnnotationRules(GOOD).check(GOOD_CLASSES);
    }

    @Test
    void architectureAnnotationRules_passOnGoodFixture() {
        DddArchitectureRules.architectureAnnotationRules(GOOD).check(GOOD_CLASSES);
    }

    @Test
    void architecturePlacementRules_passOnGoodFixture() {
        DddArchitectureRules.architecturePlacementRules(GOOD).check(GOOD_CLASSES);
    }

    // ---------- 复合规则：框架自身dogfooding ----------

    @Test
    void layeredDependencyRules_passOnFrameworkItself() {
        DddArchitectureRules.layeredDependencyRules(FRAMEWORK).check(FRAMEWORK_CLASSES);
    }

    @Test
    void domainModelRules_passOnFrameworkItself() {
        DddArchitectureRules.domainModelRules(FRAMEWORK).check(FRAMEWORK_CLASSES);
    }

    @Test
    void architecturePlacementRules_passOnFrameworkItself() {
        DddArchitectureRules.architecturePlacementRules(FRAMEWORK).check(FRAMEWORK_CLASSES);
    }

    // ---------- 分层依赖规则反例（独立根包badlayered） ----------

    @Test
    void domainDoesNotDependOnMessage_rejectsMessageDependency() {
        assertViolates(DddArchitectureRules.domainDoesNotDependOnMessage(BADLAYERED), LAYERED_DOMAIN, "DomainDependsOnMessage");
    }

    @Test
    void domainDoesNotDependOnMessage_rejectsFrameworkContractDependency() {
        assertViolates(DddArchitectureRules.domainDoesNotDependOnMessage(BADLAYERED), LAYERED_DOMAIN, "DomainDependsOnFrameworkContract");
    }

    @Test
    void domainDoesNotDependOnNorthbound_rejectsAppServiceDependency() {
        assertViolates(DddArchitectureRules.domainDoesNotDependOnNorthbound(BADLAYERED), LAYERED_DOMAIN, "DomainDependsOnAppService");
    }

    @Test
    void domainDoesNotDependOnNorthbound_rejectsFrameworkNorthboundDependency() {
        assertViolates(DddArchitectureRules.domainDoesNotDependOnNorthbound(BADLAYERED), LAYERED_DOMAIN, "DomainDependsOnFrameworkNorthbound");
    }

    @Test
    void domainDoesNotDependOnSouthboundAdapter_rejectsAdapterDependency() {
        assertViolates(DddArchitectureRules.domainDoesNotDependOnSouthboundAdapter(BADLAYERED), LAYERED_DOMAIN, "DomainDependsOnAdapter");
    }

    @Test
    void domainModelClassesDoNotDependOnPorts_rejectsRepositoryField() {
        assertViolates(DddArchitectureRules.domainModelClassesDoNotDependOnPorts(BAD), BAD_DOMAIN, "BadAggregateDependsOnRepository");
    }

    @Test
    void messageDoesNotDependOnDomain_rejectsDomainDependency() {
        assertViolates(DddArchitectureRules.messageDoesNotDependOnDomain(BADLAYERED), LAYERED_MESSAGE, "MessageDependsOnDomain");
    }

    @Test
    void messageDoesNotDependOnNorthbound_rejectsAppServiceDependency() {
        assertViolates(DddArchitectureRules.messageDoesNotDependOnNorthbound(BADLAYERED), LAYERED_MESSAGE, "MessageDependsOnNorthbound");
    }

    @Test
    void messageDoesNotDependOnDomain_rejectsFrameworkDomainType() {
        assertViolates(DddArchitectureRules.messageDoesNotDependOnDomain(BAD), BAD_MESSAGE, "BadContractRefersFrameworkDomain");
    }

    @Test
    void portDoesNotDependOnAdapter_rejectsAdapterDependency() {
        assertViolates(DddArchitectureRules.portDoesNotDependOnAdapter(BADLAYERED), LAYERED_DOMAIN_PORT, "PortDependsOnAdapter");
    }

    @Test
    void applicationServiceDoesNotDependOnAdapter_rejectsAdapterDependency() {
        assertViolates(DddArchitectureRules.applicationServiceDoesNotDependOnAdapter(BADLAYERED), LAYERED_LOCAL, "AppServiceDependsOnAdapter");
    }

    @Test
    void applicationServiceDoesNotDependOnRemote_rejectsRemoteDependency() {
        assertViolates(DddArchitectureRules.applicationServiceDoesNotDependOnRemote(BADLAYERED), LAYERED_LOCAL, "AppServiceDependsOnRemote");
    }

    @Test
    void remoteDoesNotDependOnDomain_rejectsDomainDependency() {
        assertViolates(DddArchitectureRules.remoteDoesNotDependOnDomain(BADLAYERED), LAYERED_REMOTE, "RemoteDependsOnDomain");
    }

    @Test
    void remoteDoesNotDependOnDomain_rejectsFrameworkDomainDependency() {
        assertViolates(DddArchitectureRules.remoteDoesNotDependOnDomain(BADLAYERED), LAYERED_REMOTE, "RemoteDependsOnFrameworkDomain");
    }

    @Test
    void remoteDoesNotDependOnAdapter_rejectsAdapterDependency() {
        assertViolates(DddArchitectureRules.remoteDoesNotDependOnAdapter(BADLAYERED), LAYERED_REMOTE, "RemoteDependsOnAdapter");
    }

    @Test
    void adapterDoesNotDependOnNorthbound_rejectsAppServiceDependency() {
        assertViolates(DddArchitectureRules.adapterDoesNotDependOnNorthbound(BADLAYERED), LAYERED_ADAPTER, "AdapterDependsOnAppService");
    }

    // ---------- 架构放置与装配规则反例 ----------

    @Test
    void applicationServicePlacement_rejectsRemotePackage() {
        assertViolates(DddArchitectureRules.applicationServicePlacement(BAD), BAD_REMOTE, "AppServiceInWrongPackage");
    }

    @Test
    void remotePlacement_rejectsLocalPackage() {
        assertViolates(DddArchitectureRules.remotePlacement(BAD), BAD_LOCAL, "RemoteInWrongPackage");
    }

    @Test
    void applicationEventPlacement_rejectsLocalPackage() {
        assertViolates(DddArchitectureRules.applicationEventPlacement(BAD), BAD_LOCAL, "ApplicationEventInWrongPackage");
    }

    @Test
    void assemblerPlacement_rejectsLocalPackage() {
        assertViolates(DddArchitectureRules.assemblerPlacement(BAD), BAD_LOCAL, "AssemblerInWrongPackage");
    }

    @Test
    void adapterPlacement_rejectsSouthboundRootPackage() {
        assertViolates(DddArchitectureRules.adapterPlacement(BAD), BAD_SOUTHBOUND, "AdapterInWrongPackage");
    }

    @Test
    void messageContractPlacement_rejectsLocalPackage() {
        assertViolates(DddArchitectureRules.messageContractPlacement(BAD), BAD_LOCAL, "CommandInWrongPackage");
    }

    @Test
    void domainModelPlacement_rejectsMessagePackage() {
        assertViolates(DddArchitectureRules.domainModelPlacement(BAD), BAD_MESSAGE, "ValueObjectInWrongPackage");
    }

    @Test
    void portPlacement_rejectsSouthboundPackage() {
        assertViolates(DddArchitectureRules.portPlacement(BAD), BAD_SOUTHBOUND, "PortInWrongPackage");
    }

    @Test
    void messagePackageContainsOnlyContracts_rejectsNonContractClass() {
        assertViolates(DddArchitectureRules.messagePackageContainsOnlyContracts(BAD), BAD_MESSAGE, "ValueObjectInWrongPackage");
    }

    @Test
    void portsImplementedByAdapters_rejectsPortWithoutAdapter() {
        assertViolates(DddArchitectureRules.portsImplementedByAdapters(BAD), BAD_DOMAIN_PORT, "BadPortNoAnnotation");
    }

    @Test
    void aggregateRootConstructorsNotPublicWithFactory_rejectsPublicConstructor() {
        assertViolates(DddArchitectureRules.aggregateRootConstructorsNotPublicWithFactory(BAD), BAD_DOMAIN_AGG, "BadAggregateRoot");
    }

    // ---------- 单条规则反例 ----------

    @Test
    void domainNamingRule_rejectsTechnicalSuffix() {
        assertViolates(DddArchitectureRules.domainNamingRule(BAD), BAD_DOMAIN, "BadEntity");
    }

    @Test
    void domainModelsAnnotated_rejectsMissingAnnotation() {
        assertViolates(DddArchitectureRules.domainModelsAnnotated(BAD), BAD_DOMAIN, "BadNoAnnotationEntity");
    }

    @Test
    void domainModelsAnnotated_rejectsUnannotatedFactory() {
        assertViolates(DddArchitectureRules.domainModelsAnnotated(BAD), BAD_DOMAIN, "BadFactoryNoAnnotation");
    }

    @Test
    void entitiesDeclareIdentity_rejectsMissingIdentity() {
        assertViolates(DddArchitectureRules.entitiesDeclareIdentity(BAD), BAD_DOMAIN, "BadEntityNoIdentity");
    }

    @Test
    void entitiesDeclareIdentity_rejectsBareStringIdentity() {
        assertViolates(DddArchitectureRules.entitiesDeclareIdentity(BAD), BAD_DOMAIN, "BadIdentityType");
    }

    @Test
    void entitiesImplementEqualsAndHashCode_rejectsMissingEquals() {
        assertViolates(DddArchitectureRules.entitiesImplementEqualsAndHashCode(BAD), BAD_DOMAIN, "BadEntityNoEquals");
    }

    @Test
    void aggregateInternalEntityEncapsulation_rejectsPublicInternalEntity() {
        assertViolates(DddArchitectureRules.aggregateInternalEntityEncapsulation(BAD), BAD_DOMAIN, "BadPublicItem");
    }

    @Test
    void valueObjectImmutability_rejectsNonFinalClass() {
        assertViolates(DddArchitectureRules.valueObjectImmutability(BAD), BAD_DOMAIN, "BadMutableValueObject");
    }

    @Test
    void valueObjectImmutability_rejectsNonFinalField() {
        assertViolates(DddArchitectureRules.valueObjectImmutability(BAD), BAD_DOMAIN, "BadValueObjectWithMutableField");
    }

    @Test
    void eventImmutability_rejectsNonFinalClass() {
        assertViolates(DddArchitectureRules.eventImmutability(BAD), BAD_DOMAIN, "BadMutableEvent");
    }

    @Test
    void eventImmutability_rejectsNonFinalField() {
        assertViolates(DddArchitectureRules.eventImmutability(BAD), BAD_DOMAIN, "BadEventWithMutableField");
    }

    @Test
    void eventImmutability_rejectsMutableFieldType() {
        assertViolates(DddArchitectureRules.eventImmutability(BAD), BAD_DOMAIN, "BadEventWithMutableType");
    }

    @Test
    void domainDoesNotUsePublicSetters_rejectsSetter() {
        assertViolates(DddArchitectureRules.domainDoesNotUsePublicSetters(BAD), BAD_DOMAIN, "BadSetterEntity");
    }

    @Test
    void domainDoesNotImplementSerializable_rejectsSerializable() {
        assertViolates(DddArchitectureRules.domainDoesNotImplementSerializable(BAD), BAD_DOMAIN, "BadSerializableValueObject");
    }

    @Test
    void messageContractsAnnotated_rejectsMissingAnnotation() {
        assertViolates(DddArchitectureRules.messageContractsAnnotated(BAD), BAD_MESSAGE, "BadCommandNoAnnotation");
    }

    @Test
    void messageContractsAnnotated_rejectsTypeMismatch() {
        assertViolates(DddArchitectureRules.messageContractsAnnotated(BAD), BAD_MESSAGE, "BadCommandWrongType");
    }

    @Test
    void messageContractsAnnotated_rejectsWrongExchangePattern() {
        assertViolates(DddArchitectureRules.messageContractsAnnotated(BAD), BAD_MESSAGE, "BadCommandWrongExchangePattern");
    }

    @Test
    void messageContractNaming_rejectsCommandWrongName() {
        assertViolates(DddArchitectureRules.messageContractNaming(BAD), BAD_MESSAGE, "BadCommandWrongName");
    }

    @Test
    void messageContractNaming_rejectsSchedulingWrongName() {
        assertViolates(DddArchitectureRules.messageContractNaming(BAD), BAD_MESSAGE, "BadScheduleWrongName");
    }

    @Test
    void messageContractsDoNotProvideFactoryMethods_rejectsToMethod() {
        assertViolates(DddArchitectureRules.messageContractsDoNotProvideFactoryMethods(BAD), BAD_MESSAGE, "BadContractFactory");
    }

    @Test
    void boundedContextPackageAnnotated_rejectsMissingRootAnnotation() {
        assertViolates(DddArchitectureRules.boundedContextPackageAnnotated(BAD), BAD, "RootClass");
    }

    @Test
    void boundedContextPackageAnnotated_rejectsUnannotatedRootOnSubpackages() {
        // 根包无直属类（仅子包）场景：根包标注缺失同样被拦截
        assertViolates(DddArchitectureRules.boundedContextPackageAnnotated(BADROOT), BADROOT, "SubClass");
    }

    @Test
    void domainPackageAnnotated_rejectsMissingDomainPackage() {
        assertViolates(DddArchitectureRules.domainPackageAnnotated(BAD), BAD_DOMAIN, "BadEntity");
    }

    @Test
    void aggregatePackageAnnotated_rejectsMissingAggregatePackage() {
        assertViolates(DddArchitectureRules.aggregatePackageAnnotated(BAD), BAD_DOMAIN_AGG, "BadAggregateRoot");
    }

    @Test
    void applicationServicesAnnotated_rejectsMissingAnnotation() {
        assertViolates(DddArchitectureRules.applicationServicesAnnotated(BAD), BAD_LOCAL, "BadApplicationServiceNoAnnotation");
    }

    @Test
    void applicationServicesAnnotated_rejectsTypeMismatch() {
        assertViolates(DddArchitectureRules.applicationServicesAnnotated(BAD), BAD_LOCAL, "BadApplicationServiceWrongType");
    }

    @Test
    void applicationEventsExtendBase_rejectsNonBaseEvent() {
        assertViolates(DddArchitectureRules.applicationEventsExtendBase(BAD), BAD_EVENT, "BadApplicationEventNotExtendingBase");
    }

    @Test
    void remotesAnnotated_rejectsMissingAnnotation() {
        assertViolates(DddArchitectureRules.remotesAnnotated(BAD), BAD_REMOTE, "BadRemoteNoAnnotation");
    }

    @Test
    void remotesAnnotated_rejectsTypeMismatch() {
        assertViolates(DddArchitectureRules.remotesAnnotated(BAD), BAD_REMOTE, "BadRemoteWrongType");
    }

    @Test
    void portsAnnotated_rejectsMissingAnnotation() {
        assertViolates(DddArchitectureRules.portsAnnotated(BAD), BAD_DOMAIN_PORT, "BadPortNoAnnotation");
    }

    @Test
    void portsAnnotated_rejectsTypeMismatch() {
        assertViolates(DddArchitectureRules.portsAnnotated(BAD), BAD_DOMAIN_PORT, "BadPortWrongType");
    }

    @Test
    void adaptersAnnotated_rejectsMissingAnnotation() {
        assertViolates(DddArchitectureRules.adaptersAnnotated(BAD), BAD_ADAPTER, "BadAdapterNoAnnotation");
    }

    @Test
    void adaptersAnnotated_rejectsTypeMismatch() {
        assertViolates(DddArchitectureRules.adaptersAnnotated(BAD), BAD_ADAPTER, "BadAdapterWrongType");
    }

    // ---------- 单条规则正例 ----------

    @Test
    void domainNamingRule_acceptsGoodFixture() {
        DddArchitectureRules.domainNamingRule(GOOD).check(GOOD_CLASSES);
    }

    @Test
    void entitiesDeclareIdentity_acceptsGoodFixture() {
        DddArchitectureRules.entitiesDeclareIdentity(GOOD).check(GOOD_CLASSES);
    }

    @Test
    void entitiesImplementEqualsAndHashCode_acceptsGoodFixture() {
        DddArchitectureRules.entitiesImplementEqualsAndHashCode(GOOD).check(GOOD_CLASSES);
    }

    @Test
    void valueObjectImmutability_acceptsGoodFixture() {
        DddArchitectureRules.valueObjectImmutability(GOOD).check(GOOD_CLASSES);
    }

    @Test
    void eventImmutability_acceptsGoodFixture() {
        DddArchitectureRules.eventImmutability(GOOD).check(GOOD_CLASSES);
    }

    @Test
    void portsAnnotated_acceptsGoodFixture() {
        DddArchitectureRules.portsAnnotated(GOOD).check(GOOD_CLASSES);
    }

    /**
     * 按违规类所在子包独立导入后检查，断言失败消息包含目标类名：
     * 避免不同反例类相互干扰，也避免大导入集下失败消息截断风险。
     */
    private static void assertViolates(ArchRule rule, String pkg, String className) {
        JavaClasses classes = new ClassFileImporter().importPackages(pkg);
        AssertionError error = assertThrows(AssertionError.class, () -> rule.check(classes));
        assertTrue(error.getMessage().contains(className),
                "expected violation mentioning " + className + " but got: " + error.getMessage());
    }
}
