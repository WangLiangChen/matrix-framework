package wang.liangchen.matrix.framework.ddd.rules;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.*;
import com.tngtech.archunit.core.domain.properties.HasModifiers;
import com.tngtech.archunit.lang.*;
import com.tngtech.archunit.lang.conditions.ArchConditions;
import wang.liangchen.matrix.framework.ddd.BoundedContextPackage;
import wang.liangchen.matrix.framework.ddd.assembler.Assembler;
import wang.liangchen.matrix.framework.ddd.assembler.IAssembler;
import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.event.AbstractContractEvent;
import wang.liangchen.matrix.framework.ddd.contract.event.IContractEvent;
import wang.liangchen.matrix.framework.ddd.contract.request.ICommandRequest;
import wang.liangchen.matrix.framework.ddd.contract.request.IQueryRequest;
import wang.liangchen.matrix.framework.ddd.contract.request.IRequest;
import wang.liangchen.matrix.framework.ddd.contract.request.ISchedulingRequest;
import wang.liangchen.matrix.framework.ddd.contract.response.IResponse;
import wang.liangchen.matrix.framework.ddd.contract.response.IResult;
import wang.liangchen.matrix.framework.ddd.contract.response.IView;
import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainPackage;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.AggregatePackage;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.IAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.entity.IEntity;
import wang.liangchen.matrix.framework.ddd.domain.event.AbstractDomainEvent;
import wang.liangchen.matrix.framework.ddd.domain.event.IDomainEvent;
import wang.liangchen.matrix.framework.ddd.domain.factory.IDomainFactory;
import wang.liangchen.matrix.framework.ddd.domain.identity.IIdentity;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;
import wang.liangchen.matrix.framework.ddd.domain.service.IDomainService;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject;
import wang.liangchen.matrix.framework.ddd.northbound.event.AbstractApplicationEvent;
import wang.liangchen.matrix.framework.ddd.northbound.event.IApplicationEvent;
import wang.liangchen.matrix.framework.ddd.northbound.local.*;
import wang.liangchen.matrix.framework.ddd.northbound.remote.*;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.*;
import wang.liangchen.matrix.framework.ddd.southbound.port.*;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.assignableTo;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

/**
 * 领域驱动架构规则集：基于ArchUnit守护分层依赖规则、领域模型规则、消息契约规则、包与架构标注规则（《解构领域驱动设计》的领域驱动架构风格）。
 * 业务模块在自己的测试中通过 @AnalyzeClasses(packages = "...") 与 @ArchTest 引用本规则集：
 * <pre>
 * &#64;AnalyzeClasses(packages = "wang.liangchen.matrix.shop.order")
 * class ArchitectureTest {
 *     &#64;ArchTest
 *     static final ArchRule layered = DddArchitectureRules.layeredDependencyRules("wang.liangchen.matrix.shop.order");
 *     &#64;ArchTest
 *     static final ArchRule domain = DddArchitectureRules.domainModelRules("wang.liangchen.matrix.shop.order");
 *     &#64;ArchTest
 *     static final ArchRule contract = DddArchitectureRules.messageContractRules("wang.liangchen.matrix.shop.order");
 *     &#64;ArchTest
 *     static final ArchRule packages = DddArchitectureRules.packageAnnotationRules("wang.liangchen.matrix.shop.order");
 *     &#64;ArchTest
 *     static final ArchRule annotated = DddArchitectureRules.architectureAnnotationRules("wang.liangchen.matrix.shop.order");
 *     &#64;ArchTest
 *     static final ArchRule placement = DddArchitectureRules.architecturePlacementRules("wang.liangchen.matrix.shop.order");
 * }
 * </pre>
 * 约定包结构：{rootPackage}.domain（含domain.port）/ .message / .northbound.{local,remote,event,exception} / .southbound.adapter
 * 业务模块需在pom中声明archunit测试依赖（框架以optional方式依赖archunit）。
 * 规则依赖RUNTIME retention的框架注解（如@AggregatePackage），注解已进入字节码。
 *
 * @author Liangchen.Wang
 */
public final class DddArchitectureRules {

    /**
     * 框架自身的基础包：守护规则在拦截业务模块内部越界依赖的同时，
     * 也拦截对框架契约(contract)/北向(northbound)/南向适配器(southbound.adapter)/领域层(domain)类型的越界依赖，
     * 避免"领域类继承框架AbstractContractEvent、远程服务直接使用领域类型"等跨层依赖从包前缀匹配中漏网。
     */
    private static final String FRAMEWORK_BASE_PACKAGE = "wang.liangchen.matrix.framework.ddd";

    private DddArchitectureRules() {
    }

    /**
     * 分层依赖规则全集：领域层纯净性、消息契约隔离（不依赖领域模型与北向）、端口依赖倒置、
     * 应用服务经端口访问外部资源、远程服务只操作消息契约、南向适配器不反向依赖北向、
     * 聚合等领域模型类（领域服务除外）不得使用端口。
     */
    public static ArchRule layeredDependencyRules(String rootPackage) {
        return CompositeArchRule.of(domainDoesNotDependOnMessage(rootPackage))
                .and(domainDoesNotDependOnNorthbound(rootPackage))
                .and(domainDoesNotDependOnSouthboundAdapter(rootPackage))
                .and(domainModelClassesDoNotDependOnPorts(rootPackage))
                .and(messageDoesNotDependOnDomain(rootPackage))
                .and(messageDoesNotDependOnNorthbound(rootPackage))
                .and(portDoesNotDependOnAdapter(rootPackage))
                .and(applicationServiceDoesNotDependOnAdapter(rootPackage))
                .and(applicationServiceDoesNotDependOnRemote(rootPackage))
                .and(remoteDoesNotDependOnDomain(rootPackage))
                .and(remoteDoesNotDependOnAdapter(rootPackage))
                .and(adapterDoesNotDependOnNorthbound(rootPackage));
    }

    /**
     * 领域模型规则全集：统一语言命名、领域模型注解、实体身份标识与相等性、聚合封装、
     * 值对象不可变（含深度不可变字段类型校验）、领域事件不可变、无公共setter、领域对象不可序列化、
     * 有领域工厂时聚合根构造不得public。
     */
    public static ArchRule domainModelRules(String rootPackage) {
        return CompositeArchRule.of(domainNamingRule(rootPackage))
                .and(domainModelsAnnotated(rootPackage))
                .and(entitiesDeclareIdentity(rootPackage))
                .and(entitiesImplementEqualsAndHashCode(rootPackage))
                .and(aggregateInternalEntityEncapsulation(rootPackage))
                .and(valueObjectImmutability(rootPackage))
                .and(eventImmutability(rootPackage))
                .and(domainEventsExtendBase(rootPackage))
                .and(domainDoesNotUsePublicSetters(rootPackage))
                .and(domainDoesNotImplementSerializable(rootPackage))
                .and(aggregateRootConstructorsNotPublicWithFactory(rootPackage));
    }

    /**
     * 消息契约规则全集：契约注解与标记接口匹配、契约命名规范、契约不得担任工厂、message包只放契约。
     */
    public static ArchRule messageContractRules(String rootPackage) {
        return CompositeArchRule.of(messageContractsAnnotated(rootPackage))
                .and(messageContractNaming(rootPackage))
                .and(messageContractsDoNotProvideFactoryMethods(rootPackage))
                .and(messagePackageContainsOnlyContracts(rootPackage));
    }

    /**
     * 包标注规则全集：限界上下文根包、领域层包与聚合包必须通过package-info.java标注对应注解。
     */
    public static ArchRule packageAnnotationRules(String rootPackage) {
        return CompositeArchRule.of(boundedContextPackageAnnotated(rootPackage))
                .and(domainPackageAnnotated(rootPackage))
                .and(aggregatePackageAnnotated(rootPackage));
    }

    /**
     * 架构标注规则全集：应用服务、应用事件、远程服务、端口与适配器、装配器必须标注对应注解/继承对应基类且注解值与标记接口匹配。
     */
    public static ArchRule architectureAnnotationRules(String rootPackage) {
        return CompositeArchRule.of(applicationServicesAnnotated(rootPackage))
                .and(applicationEventsExtendBase(rootPackage))
                .and(remotesAnnotated(rootPackage))
                .and(portsAnnotated(rootPackage))
                .and(adaptersAnnotated(rootPackage))
                .and(assemblersAnnotated(rootPackage));
    }

    /**
     * 架构放置规则全集：架构元素的具体实现类必须位于约定包内——
     * 应用服务→northbound.local、远程服务→northbound.remote、应用事件→northbound.event、
     * 装配器→northbound.assembler、适配器→southbound.adapter、消息契约→message、领域模型→domain、
     * 业务端口→domain.port（框架自身的端口基接口位于框架包southbound.port，豁免）；
     * 并守护装配完整性：message包只放消息契约，业务端口必须由至少一个适配器实现。
     */
    public static ArchRule architecturePlacementRules(String rootPackage) {
        return CompositeArchRule.of(applicationServicePlacement(rootPackage))
                .and(remotePlacement(rootPackage))
                .and(applicationEventPlacement(rootPackage))
                .and(assemblerPlacement(rootPackage))
                .and(adapterPlacement(rootPackage))
                .and(messageContractPlacement(rootPackage))
                .and(domainModelPlacement(rootPackage))
                .and(portPlacement(rootPackage))
                .and(messagePackageContainsOnlyContracts(rootPackage))
                .and(portsImplementedByAdapters(rootPackage));
    }

    /**
     * 实体身份标识规则：实体必须具有唯一身份标识，且以值对象表达（《解构领域驱动设计》）。
     * domain包下IEntity的非抽象实现类必须声明且仅声明一个@Identity字段（含继承），
     * 且该字段类型必须实现IIdentity（不能是裸String/Long等基本类型）。
     */
    public static ArchRule entitiesDeclareIdentity(String rootPackage) {
        return classes().that(describe("concrete entities (IEntity implementations) in the domain layer of " + rootPackage,
                        concreteEntityInDomain(rootPackage)))
                .should(declareExactlyOneIdentityField())
                .allowEmptyShould(true)
                .because("实体必须具有唯一身份标识，且身份标识以值对象表达：@Identity字段有且仅有一个，字段类型必须实现IIdentity");
    }

    /**
     * 实体相等性规则：实体相等按身份标识定义（《解构领域驱动设计》），具体实体类（含聚合根）必须重写
     * equals()与hashCode()（声明于自身或其超类，而非继承自Object）。
     */
    public static ArchRule entitiesImplementEqualsAndHashCode(String rootPackage) {
        return classes().that(describe("concrete entities (IEntity implementations) in the domain layer of " + rootPackage,
                        concreteEntityInDomain(rootPackage)))
                .should(implementEqualsAndHashCode())
                .allowEmptyShould(true)
                .because("实体相等按身份标识定义，实体必须重写equals()和hashCode()");
    }

    /**
     * 领域模型注解规则：领域类必须实现对应标记接口并自行标注匹配的@DomainModel（@Inherited不适用于类上的
     * 注解传播，业务类必须自行标注）。接口与枚举豁免；抽象基类同样要求标注（子类不会继承注解）。
     */
    public static ArchRule domainModelsAnnotated(String rootPackage) {
        return classes().that(describe("domain model classes (marker implementations) in the domain layer of " + rootPackage,
                        domainModelCandidatesIn(rootPackage)))
                .should(haveMatchingDomainModelAnnotation())
                .allowEmptyShould(true)
                .because("领域模型类必须标注与所实现标记接口匹配的@DomainModel注解");
    }

    /**
     * 领域对象不可序列化直传：领域层类（异常类豁免，异常经Throwable天然可序列化）不得实现Serializable，
     * 跨边界通信一律经消息契约（发布语言）序列化传输。
     */
    public static ArchRule domainDoesNotImplementSerializable(String rootPackage) {
        return noClasses().that(describe("domain classes (excluding exceptions) in the domain layer of " + rootPackage,
                        domainClassesExcludingExceptions(rootPackage)))
                .should(implementSerializable())
                .allowEmptyShould(true)
                .because("领域对象不可序列化直传：跨边界通信一律经消息契约，由消息总线以发布语言序列化传输");
    }

    /**
     * 基于反射判断是否实现Serializable：implement(Class)条件依赖已导入类图的完整性，
     * JDK接口在部分导入集下（如仅导入单个固件包）可能未被解析而漏判，反射判断不依赖导入集。
     */
    private static ArchCondition<JavaClass> implementSerializable() {
        return new ArchCondition<JavaClass>("implement java.io.Serializable") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                boolean satisfied = Serializable.class.isAssignableFrom(item.reflect());
                String message = String.format("%s implements Serializable=%s", item.getSimpleName(), satisfied);
                events.add(new SimpleConditionEvent(item, satisfied, message));
            }
        };
    }

    /**
     * 消息契约注解规则：message包的具体契约类必须实现对应标记接口（或继承AbstractContractEvent），
     * 并标注@MessageContract，其type必须与标记接口匹配（direction/type/exchangePattern为注解必填项）。
     */
    public static ArchRule messageContractsAnnotated(String rootPackage) {
        return classes().that(describe("concrete message contract classes in the message package of " + rootPackage,
                        concreteMessageContractsIn(rootPackage)))
                .should(haveConsistentMessageContractAnnotation())
                .allowEmptyShould(true)
                .because("消息契约必须实现对应标记接口并标注type匹配的@MessageContract注解");
    }

    /**
     * 消息契约命名规则：命令请求命名动名词+CommandRequest，查询请求动名词+QueryRequest，
     * 调度消息动名词+SchedulingRequest，命令响应动名词+Result，查询视图业务名词+View；
     * 事件契约与通用REQUEST/RESPONSE契约不强制后缀（事件契约遵循业务名词+过去式动词）。
     */
    public static ArchRule messageContractNaming(String rootPackage) {
        return classes().that(describe("concrete message contract classes in the message package of " + rootPackage,
                        concreteMessageContractsIn(rootPackage)))
                .should(haveTypeMatchingName())
                .allowEmptyShould(true)
                .because("消息契约命名必须与契约类型匹配：xxxCommandRequest/xxxQueryRequest/xxxSchedulingRequest/xxxResult/xxxView");
    }

    /**
     * 消息契约不得担任工厂：契约是纯数据模型，不得提供toXxx()等创建领域对象的公共方法，
     * 领域对象的创建由应用层的装配器承担（《解构领域驱动设计》：消息契约模型与领域模型隔离）。
     */
    public static ArchRule messageContractsDoNotProvideFactoryMethods(String rootPackage) {
        return noMethods().that(describe("methods named toXxx() in the message package of " + rootPackage,
                        factoryMethodInMessage(rootPackage)))
                .should(ArchConditions.bePublic())
                .allowEmptyShould(true)
                .because("消息契约是纯数据模型，不得提供toXxx()等公共工厂方法创建领域对象，装配由应用层装配器承担");
    }

    /**
     * 限界上下文根包标注：根包及其所有子包的类必须位于标注了@BoundedContextPackage(name=..., domainType=...)的包内。
     * 覆盖根包无直属类（仅package-info与子包）的场景：根包标注缺失时子包类同样被拦截。
     */
    public static ArchRule boundedContextPackageAnnotated(String rootPackage) {
        return classes().that(describe("classes in the root package tree of " + rootPackage,
                        resideInRootPackageTree(rootPackage)))
                .should(resideUnderSpecifiedPackageAnnotatedWith(rootPackage, BoundedContextPackage.class))
                .allowEmptyShould(true)
                .because("限界上下文根包必须通过package-info.java添加@BoundedContextPackage(name=..., domainType=...)");
    }

    /**
     * 领域层包标注：domain包及其子包的类必须位于（含父包）标注了@DomainPackage的包体系内。
     */
    public static ArchRule domainPackageAnnotated(String rootPackage) {
        return classes().that(describe("classes in the domain layer of " + rootPackage,
                        resideInDomainPackageTree(rootPackage)))
                .should(resideInOrUnderPackageAnnotatedWith(DomainPackage.class))
                .allowEmptyShould(true)
                .because("领域层包（domain）必须通过package-info.java添加@DomainPackage");
    }

    /**
     * 聚合包标注：具体聚合根类所在包必须标注@AggregatePackage("{聚合名称}")。
     */
    public static ArchRule aggregatePackageAnnotated(String rootPackage) {
        return classes().that(describe("concrete aggregate roots in the domain layer of " + rootPackage,
                        concreteAggregateRootInDomain(rootPackage)))
                .should(resideInPackageAnnotatedWith(AggregatePackage.class))
                .allowEmptyShould(true)
                .because("聚合包必须通过package-info.java添加@AggregatePackage(name=聚合名称)");
    }

    /**
     * 应用服务标注：实现IApplicationService的具体类必须标注@ApplicationService，
     * 且其value与所实现的I*ApplicationService子接口匹配（@Inherited仅对类继承生效，
     * 接口上的注解不会传播给实现类，业务类必须自行标注）。
     */
    public static ArchRule applicationServicesAnnotated(String rootPackage) {
        return classes().that(describe("concrete application services in " + rootPackage,
                        concreteClassIn(rootPackage, ".northbound.local").and(assignableTo(IApplicationService.class))))
                .should(haveMatchingApplicationServiceAnnotation())
                .allowEmptyShould(true)
                .because("应用服务必须自行标注@ApplicationService(ApplicationServiceType.xxx)，且类型与实现的子接口匹配");
    }

    /**
     * 应用事件基类规则：northbound.event包的具体类必须继承AbstractApplicationEvent基类
     * （应用事件仅限进程内应用层协作与通知，不跨限界上下文发布）。
     */
    public static ArchRule applicationEventsExtendBase(String rootPackage) {
        return classes().that(describe("concrete application events in the northbound.event package of " + rootPackage,
                        concreteClassIn(rootPackage, ".northbound.event")))
                .should(new ArchCondition<JavaClass>("extend AbstractApplicationEvent") {
                    @Override
                    public void check(JavaClass item, ConditionEvents events) {
                        boolean satisfied = item.isAssignableTo(AbstractApplicationEvent.class);
                        String message = String.format("%s extends AbstractApplicationEvent=%s", item.getSimpleName(), satisfied);
                        events.add(new SimpleConditionEvent(item, satisfied, message));
                    }
                })
                .allowEmptyShould(true)
                .because("应用事件必须继承AbstractApplicationEvent基类");
    }

    /**
     * 领域事件必须继承AbstractDomainEvent基类：domain包下实现IDomainEvent的具体类必须继承AbstractDomainEvent，
     * 从而统一获得eventId/occurredOn、值相等与不可变约束，并纳入其余以AbstractDomainEvent为基准的事件规则守护
     * （命名、不可变、不可序列化、@DomainModel标注等）。与应用事件的applicationEventsExtendBase对称。
     */
    public static ArchRule domainEventsExtendBase(String rootPackage) {
        return classes().that(describe("concrete IDomainEvent implementations in the domain layer of " + rootPackage,
                        concreteDomainEventCandidatesIn(rootPackage)))
                .should(new ArchCondition<JavaClass>("extend AbstractDomainEvent") {
                    @Override
                    public void check(JavaClass item, ConditionEvents events) {
                        boolean satisfied = item.isAssignableTo(AbstractDomainEvent.class);
                        String message = String.format("%s extends AbstractDomainEvent=%s", item.getSimpleName(), satisfied);
                        events.add(new SimpleConditionEvent(item, satisfied, message));
                    }
                })
                .allowEmptyShould(true)
                .because("领域事件必须继承AbstractDomainEvent基类");
    }

    /**
     * 远程服务标注：实现IRemote的具体类必须标注@Remote，且其value与所实现的I*Remote子接口匹配。
     */
    public static ArchRule remotesAnnotated(String rootPackage) {
        return classes().that(describe("concrete remote services in " + rootPackage,
                        concreteClassIn(rootPackage, ".northbound.remote").and(assignableTo(IRemote.class))))
                .should(haveMatchingRemoteAnnotation())
                .allowEmptyShould(true)
                .because("远程服务必须自行标注@Remote(RemoteType.xxx)，且类型与实现的子接口匹配");
    }

    /**
     * 端口标注：domain.port包下实现IPort的端口接口/类（IPort基接口自身除外）必须标注@Port，
     * 且其value与所实现的I*Port子接口匹配。业务端口通常为接口（如OrderRepositoryPort），故包含接口。
     */
    public static ArchRule portsAnnotated(String rootPackage) {
        return classes().that(describe("ports (IPort implementations, excluding IPort itself) in " + rootPackage,
                        portCandidatesIn(rootPackage)))
                .should(haveMatchingPortAnnotation())
                .allowEmptyShould(true)
                .because("端口必须自行标注@Port(PortType.xxx)，且类型与实现的子接口匹配");
    }

    /**
     * 适配器标注：southbound.adapter包下实现IAdapter的具体类必须标注@Adapter，且其value与所实现的I*Adapter子接口匹配。
     */
    public static ArchRule adaptersAnnotated(String rootPackage) {
        return classes().that(describe("concrete adapters in " + rootPackage,
                        concreteClassIn(rootPackage, ".southbound.adapter").and(assignableTo(IAdapter.class))))
                .should(haveMatchingAdapterAnnotation())
                .allowEmptyShould(true)
                .because("适配器必须自行标注@Adapter(PortType.xxx)，且类型与实现的子接口匹配");
    }

    /**
     * 装配器标注：northbound.assembler包内实现IAssembler的具体类必须标注@Assembler。
     * 装配器是消息契约与领域对象互转的唯一装配点（消息契约不担任工厂），命名xxxAssembler。
     */
    public static ArchRule assemblersAnnotated(String rootPackage) {
        return classes().that(describe("concrete assemblers in the northbound.assembler package of " + rootPackage,
                        concreteClassIn(rootPackage, ".northbound.assembler").and(assignableTo(IAssembler.class))))
                .should(ArchConditions.beAnnotatedWith(Assembler.class))
                .allowEmptyShould(true)
                .because("装配器必须自行标注@Assembler（接口上的注解不会传播给实现类）");
    }

    /**
     * 应用服务放置规则：实现IApplicationService的具体类必须位于northbound.local包（含子包）。
     * 防止应用服务实现类放错包位后，依赖方向守护规则（按包前缀匹配）对其失明。
     */
    public static ArchRule applicationServicePlacement(String rootPackage) {
        return classes().that(describe("concrete application services in " + rootPackage,
                        concreteClassInRoot(rootPackage).and(assignableTo(IApplicationService.class))))
                .should(ArchConditions.resideInAPackage(rootPackage + ".northbound.local.."))
                .allowEmptyShould(true)
                .because("应用服务具体类必须位于northbound.local包");
    }

    /**
     * 远程服务放置规则：实现IRemote的具体类必须位于northbound.remote包（含子包）。
     */
    public static ArchRule remotePlacement(String rootPackage) {
        return classes().that(describe("concrete remote services in " + rootPackage,
                        concreteClassInRoot(rootPackage).and(assignableTo(IRemote.class))))
                .should(ArchConditions.resideInAPackage(rootPackage + ".northbound.remote.."))
                .allowEmptyShould(true)
                .because("远程服务具体类必须位于northbound.remote包");
    }

    /**
     * 应用事件放置规则：继承AbstractApplicationEvent（或实现IApplicationEvent）的具体类必须位于northbound.event包（含子包）。
     */
    public static ArchRule applicationEventPlacement(String rootPackage) {
        return classes().that(describe("concrete application events in " + rootPackage,
                        concreteClassInRoot(rootPackage).and(assignableTo(IApplicationEvent.class))))
                .should(ArchConditions.resideInAPackage(rootPackage + ".northbound.event.."))
                .allowEmptyShould(true)
                .because("应用事件具体类必须位于northbound.event包");
    }

    /**
     * 装配器放置规则：实现IAssembler的具体类必须位于northbound.assembler包（含子包）。
     */
    public static ArchRule assemblerPlacement(String rootPackage) {
        return classes().that(describe("concrete assemblers in " + rootPackage,
                        concreteClassInRoot(rootPackage).and(assignableTo(IAssembler.class))))
                .should(ArchConditions.resideInAPackage(rootPackage + ".northbound.assembler.."))
                .allowEmptyShould(true)
                .because("装配器具体类必须位于northbound.assembler包");
    }

    /**
     * 适配器放置规则：实现IAdapter的具体类必须位于southbound.adapter包（含子包）。
     */
    public static ArchRule adapterPlacement(String rootPackage) {
        return classes().that(describe("concrete adapters in " + rootPackage,
                        concreteClassInRoot(rootPackage).and(assignableTo(IAdapter.class))))
                .should(ArchConditions.resideInAPackage(rootPackage + ".southbound.adapter.."))
                .allowEmptyShould(true)
                .because("适配器具体类必须位于southbound.adapter包");
    }

    /**
     * 消息契约放置规则：实现契约标记接口（或继承AbstractContractEvent）的具体类必须位于message包（含子包）。
     * 应用事件（AbstractApplicationEvent体系）是进程内协作机制而非发布语言，不按消息契约放置，位于northbound.event。
     */
    public static ArchRule messageContractPlacement(String rootPackage) {
        return classes().that(describe("concrete message contracts in " + rootPackage,
                        concreteClassInRoot(rootPackage).and(implementsContractMarker())))
                .should(ArchConditions.resideInAPackage(rootPackage + ".message.."))
                .allowEmptyShould(true)
                .because("消息契约具体类必须位于message包（应用事件除外，它位于northbound.event）");
    }

    /**
     * 领域模型放置规则：实现领域标记接口（实体/聚合根/值对象/身份标识/领域服务/领域事件/领域工厂）的具体类必须位于domain包（含子包）。
     */
    public static ArchRule domainModelPlacement(String rootPackage) {
        return classes().that(describe("concrete domain model classes in " + rootPackage,
                        concreteClassInRoot(rootPackage).and(implementsDomainModelMarker())))
                .should(ArchConditions.resideInAPackage(rootPackage + ".domain.."))
                .allowEmptyShould(true)
                .because("领域模型具体类必须位于domain包");
    }

    /**
     * 端口放置规则：业务端口（IPort实现，适配器实现类除外）必须位于domain.port包（含子包）。
     * 端口放错包位后，按包前缀匹配的端口守护规则（portDoesNotDependOnAdapter/portsAnnotated等）对其失明。
     * 框架自身的端口基接口（IRepositoryPort等）位于框架包southbound.port，予以豁免（dogfooding）。
     */
    public static ArchRule portPlacement(String rootPackage) {
        return classes().that(describe("port candidates (IPort implementations, excluding adapters and the framework itself) in " + rootPackage,
                        portCandidatesInRoot(rootPackage)))
                .should(ArchConditions.resideInAPackage(rootPackage + ".domain.port.."))
                .allowEmptyShould(true)
                .because("业务端口必须位于domain.port包（框架自身的端口基接口位于框架包southbound.port，豁免）");
    }

    /**
     * message包只放契约：message包内的具体类必须实现契约标记接口（IRequest/IResponse）或继承AbstractContractEvent，
     * 禁止在message包放置普通POJO/工具类。
     */
    public static ArchRule messagePackageContainsOnlyContracts(String rootPackage) {
        return classes().that(describe("concrete classes in the message package of " + rootPackage,
                        concreteClassIn(rootPackage, ".message")))
                .should(implementContractMarker())
                .allowEmptyShould(true)
                .because("message包只放消息契约：具体类必须实现契约标记接口或继承AbstractContractEvent");
    }

    /**
     * 端口装配完整性：domain.port包下每个业务端口必须由至少一个适配器（IAdapter实现类）实现。
     * 依赖倒置若只有端口没有适配器，缺口会在运行期才暴露，本规则将其提前到构建期。
     */
    public static ArchRule portsImplementedByAdapters(String rootPackage) {
        return classes().that(describe("ports (IPort implementations, excluding IPort itself) in " + rootPackage,
                        portCandidatesIn(rootPackage)))
                .should(beImplementedByAdapter())
                .allowEmptyShould(true)
                .because("每个业务端口必须由至少一个适配器（IAdapter实现类）实现，装配缺口应静态暴露");
    }

    /**
     * 聚合根构造可见性规则：聚合包内存在领域工厂时，聚合根的构造方法不得为public，
     * 保证聚合根只能经工厂创建（专门的聚合工厂模式）；无工厂的简单聚合不受此限（构造方法即可表达创建意图）。
     */
    public static ArchRule aggregateRootConstructorsNotPublicWithFactory(String rootPackage) {
        return classes().that(describe("concrete aggregate roots in aggregate packages containing a domain factory of " + rootPackage,
                        concreteAggregateRootInDomain(rootPackage).and(inAggregatePackageWithDomainFactory())))
                .should(notHavePublicConstructors())
                .allowEmptyShould(true)
                .because("聚合包内存在领域工厂时聚合根构造方法不得public，聚合根只能经工厂创建");
    }

    /**
     * 领域层不得依赖消息契约(message)：领域模型与消息契约的转换（装配）只发生在应用服务内。
     * 同时拦截对框架契约类型（ICommandRequest/AbstractContractEvent/IView等）的依赖。
     */
    public static ArchRule domainDoesNotDependOnMessage(String rootPackage) {
        return noClasses().that().resideInAPackage(rootPackage + ".domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        rootPackage + ".message..",
                        FRAMEWORK_BASE_PACKAGE + ".contract..")
                .allowEmptyShould(true)
                .because("领域层不得依赖消息契约（含框架契约类型），领域模型与消息契约的装配只发生在应用服务内");
    }

    /**
     * 领域层不得依赖北向接口（应用服务/远程服务/应用事件/应用异常）：依赖方向只能由外向内。
     * 同时拦截对框架北向类型（IApplicationService/IRemote/AbstractApplicationEvent等）的依赖。
     */
    public static ArchRule domainDoesNotDependOnNorthbound(String rootPackage) {
        return noClasses().that().resideInAPackage(rootPackage + ".domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        rootPackage + ".northbound..",
                        FRAMEWORK_BASE_PACKAGE + ".northbound..")
                .allowEmptyShould(true)
                .because("领域层不得依赖应用层与远程层（含框架北向类型），依赖方向只能由外向内");
    }

    /**
     * 领域层不得依赖南向适配器实现：领域层通过端口声明依赖，适配器实现端口（依赖倒置）。
     * 同时拦截对框架适配器类型（IRepositoryAdapter等）的依赖。
     */
    public static ArchRule domainDoesNotDependOnSouthboundAdapter(String rootPackage) {
        return noClasses().that().resideInAPackage(rootPackage + ".domain..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        rootPackage + ".southbound.adapter..",
                        FRAMEWORK_BASE_PACKAGE + ".southbound.adapter..")
                .allowEmptyShould(true)
                .because("领域层通过端口声明对外部资源的依赖，适配器实现端口（依赖倒置），领域层不得依赖适配器实现（含框架适配器类型）");
    }

    /**
     * 领域模型类不得使用端口：聚合及其实体、值对象、事件、工厂不得依赖端口（资源库等外部资源）
     * （《解构领域驱动设计》：不能在聚合内部使用资源库）；领域服务可依赖领域仓储，予以豁免。
     * 外部资源访问由应用服务或领域服务通过端口完成。
     */
    public static ArchRule domainModelClassesDoNotDependOnPorts(String rootPackage) {
        return noClasses().that(describe("domain model classes (excluding domain services) in the domain layer of " + rootPackage,
                        domainModelCandidatesIn(rootPackage).and(not(assignableTo(IDomainService.class)))))
                .should().dependOnClassesThat(assignableTo(IPort.class))
                .allowEmptyShould(true)
                .because("聚合及其实体、值对象、事件、工厂不得使用端口（资源库等外部资源），外部资源访问由应用服务或领域服务通过端口完成");
    }

    /**
     * 消息契约不得依赖领域模型：发布语言与领域模型隔离（《解构领域驱动设计》）。
     * 消息契约中如需携带身份标识，以基本类型值承载（如字符串形式的标识值）；
     * 同时禁止契约依赖框架的领域类型（如IIdentity/AbstractValueObject等）。
     */
    public static ArchRule messageDoesNotDependOnDomain(String rootPackage) {
        return noClasses().that().resideInAPackage(rootPackage + ".message..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        rootPackage + ".domain..",
                        FRAMEWORK_BASE_PACKAGE + ".domain..")
                .allowEmptyShould(true)
                .because("消息契约是发布语言，不得依赖领域模型（含框架领域类型），跨边界通信必须与领域对象隔离");
    }

    /**
     * 消息契约不得依赖北向（应用服务/远程服务/应用事件/装配器）：发布语言是自治的通信语言，
     * 不得引用应用事件等进程内协作机制（应用事件面向应用层进程内通知，不作为跨边界发布语言）。
     * 同时拦截对框架北向类型的依赖。
     */
    public static ArchRule messageDoesNotDependOnNorthbound(String rootPackage) {
        return noClasses().that().resideInAPackage(rootPackage + ".message..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        rootPackage + ".northbound..",
                        FRAMEWORK_BASE_PACKAGE + ".northbound..")
                .allowEmptyShould(true)
                .because("消息契约是自治的发布语言，不得依赖北向（应用服务/远程服务/应用事件/装配器，含框架北向类型）");
    }

    /**
     * 端口不得反向依赖适配器实现（含框架适配器类型）。
     */
    public static ArchRule portDoesNotDependOnAdapter(String rootPackage) {
        return noClasses().that().resideInAPackage(rootPackage + ".domain.port..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        rootPackage + ".southbound.adapter..",
                        FRAMEWORK_BASE_PACKAGE + ".southbound.adapter..")
                .allowEmptyShould(true)
                .because("端口由适配器实现，端口不得反向依赖适配器（含框架适配器类型）");
    }

    /**
     * 应用服务不得依赖南向适配器实现：应用服务通过端口接口访问外部资源。
     * 同时拦截对框架适配器类型的依赖。
     */
    public static ArchRule applicationServiceDoesNotDependOnAdapter(String rootPackage) {
        return noClasses().that().resideInAPackage(rootPackage + ".northbound.local..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        rootPackage + ".southbound.adapter..",
                        FRAMEWORK_BASE_PACKAGE + ".southbound.adapter..")
                .allowEmptyShould(true)
                .because("应用服务通过端口接口访问外部资源，不得依赖适配器实现（含框架适配器类型）");
    }

    /**
     * 应用服务不得依赖远程层：远程服务通过应用服务完成用例编排，依赖方向只能由外向内。
     * 同时拦截对框架远程类型（IRemote/RemoteType等）的依赖。
     */
    public static ArchRule applicationServiceDoesNotDependOnRemote(String rootPackage) {
        return noClasses().that().resideInAPackage(rootPackage + ".northbound.local..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        rootPackage + ".northbound.remote..",
                        FRAMEWORK_BASE_PACKAGE + ".northbound.remote..")
                .allowEmptyShould(true)
                .because("应用服务不得依赖远程层（含框架远程类型），远程服务通过应用服务完成用例编排");
    }

    /**
     * 远程服务只操作消息契约，不直接访问领域对象。
     * 同时拦截对框架领域类型（IEntity/IIdentity/领域基类等）的依赖。
     */
    public static ArchRule remoteDoesNotDependOnDomain(String rootPackage) {
        return noClasses().that().resideInAPackage(rootPackage + ".northbound.remote..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        rootPackage + ".domain..",
                        FRAMEWORK_BASE_PACKAGE + ".domain..")
                .allowEmptyShould(true)
                .because("远程服务只操作消息契约，通过应用服务完成用例编排，不直接访问领域对象（含框架领域类型）");
    }

    /**
     * 远程服务不得直接依赖南向适配器：外部资源访问须经应用服务与端口。
     * 同时拦截对框架适配器类型的依赖。
     */
    public static ArchRule remoteDoesNotDependOnAdapter(String rootPackage) {
        return noClasses().that().resideInAPackage(rootPackage + ".northbound.remote..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        rootPackage + ".southbound.adapter..",
                        FRAMEWORK_BASE_PACKAGE + ".southbound.adapter..")
                .allowEmptyShould(true)
                .because("远程服务不得直接依赖南向适配器（含框架适配器类型），外部资源访问须经应用服务与端口");
    }

    /**
     * 南向适配器不得反向依赖北向（应用服务/远程服务/应用事件/装配器）：依赖方向只能由外向内，
     * 适配器实现端口（依赖倒置），反向依赖应用层会形成"北向→端口→适配器→北向"的潜在环。
     * 同时拦截对框架北向类型的依赖。
     */
    public static ArchRule adapterDoesNotDependOnNorthbound(String rootPackage) {
        return noClasses().that().resideInAPackage(rootPackage + ".southbound.adapter..")
                .should().dependOnClassesThat().resideInAnyPackage(
                        rootPackage + ".northbound..",
                        FRAMEWORK_BASE_PACKAGE + ".northbound..")
                .allowEmptyShould(true)
                .because("南向适配器不得反向依赖北向（应用服务/远程服务/应用事件/装配器，含框架北向类型），依赖方向只能由外向内");
    }

    /**
     * 领域类名禁止技术后缀：命名必须取自统一语言。
     * 豁免框架自身的领域基类与标记接口（AbstractEntity/IEntity等），使框架可对自己的领域包运行本规则（dogfooding）。
     */
    public static ArchRule domainNamingRule(String rootPackage) {
        return classes().that(describe("domain classes (excluding the framework itself) in the domain layer of " + rootPackage,
                        domainClassesExcludingFramework(rootPackage)))
                .should().haveNameNotMatching(".*(Entity|VO|DO|PO|DTO|Model)$")
                .allowEmptyShould(true)
                .because("命名必须取自统一语言，领域类名禁止技术后缀");
    }

    /**
     * 聚合封装：聚合包（@AggregatePackage）内的非聚合根实体必须包内可见，
     * 外部只能通过聚合根访问和修改聚合内部状态。
     * 身份标识（跨聚合引用）、领域事件（应用服务发布）与领域工厂（适配器重建）不受此限。
     */
    public static ArchRule aggregateInternalEntityEncapsulation(String rootPackage) {
        return classes().that(describe("internal entities (non aggregate-root) inside an @AggregatePackage",
                        internalEntityInAggregatePackage(rootPackage)))
                .should(ArchConditions.notBePublic())
                .allowEmptyShould(true)
                .because("聚合内部实体只能通过聚合根访问，非聚合根实体在聚合包内不得public");
    }

    /**
     * 值对象不可变：值对象类必须为final，实例字段必须为final（静态常量除外），实例字段类型必须为不可变类型。
     * 接口、枚举与抽象基类不要求final，但其具体实现类必须为final。
     * 深度不可变：实例字段类型限定为基本类型、包装类型、String、时间类型、BigDecimal/BigInteger、枚举、
     * 或自身不可变的值对象（IValueObject，含其子接口 IIdentity）。
     */
    public static ArchRule valueObjectImmutability(String rootPackage) {
        return CompositeArchRule
                .of(classes().that(domainValueObjectsIn(rootPackage))
                        .and(not(HasModifiers.Predicates.modifier(JavaModifier.ABSTRACT)))
                        .should(ArchConditions.beFinal())
                        .allowEmptyShould(true)
                        .because("值对象必须不可变：值对象类必须为final"))
                .and(fields().that(describe("non-static instance fields declared in domain value objects",
                                nonStaticFieldOfDomainValueObject(rootPackage)))
                        .should(ArchConditions.beFinal())
                        .allowEmptyShould(true)
                        .because("值对象必须不可变：值对象的实例字段必须为final"))
                .and(valueObjectFieldTypeImmutability(rootPackage));
    }

    public static ArchRule valueObjectFieldTypeImmutability(String rootPackage) {
        return fields().that(nonStaticFieldOfDomainValueObject(rootPackage))
                .should(haveImmutableFieldType())
                .allowEmptyShould(true)
                .because("值对象深度不可变：实例字段类型必须是基本类型、包装类型、String、时间类型、BigDecimal、枚举或不可变值对象/身份标识");
    }

    private static ArchCondition<JavaField> haveImmutableFieldType() {
        return new ArchCondition<JavaField>("have immutable types (primitive, wrapper, String, temporal, BigDecimal, enum, or IValueObject)") {
            @Override
            public void check(JavaField field, ConditionEvents events) {
                JavaClass fieldType = field.getRawType();
                boolean satisfied = isImmutableType(fieldType);
                String message = String.format("%s.%s has type %s, immutable=%s",
                        field.getOwner().getSimpleName(), field.getName(),
                        fieldType.getSimpleName(), satisfied);
                events.add(new SimpleConditionEvent(field, satisfied, message));
            }
        };
    }

    /**
     * 领域事件不可变：领域事件类必须为final，实例字段必须为final（静态常量除外）。
     * 抽象基类不要求final，但其具体子类必须为final。
     */
    public static ArchRule eventImmutability(String rootPackage) {
        return CompositeArchRule
                .of(classes().that(domainEventsIn(rootPackage))
                        .and(not(HasModifiers.Predicates.modifier(JavaModifier.ABSTRACT)))
                        .should(ArchConditions.beFinal())
                        .allowEmptyShould(true)
                        .because("领域事件必须不可变：事件类必须为final"))
                .and(fields().that(describe("non-static instance fields declared in domain events",
                                nonStaticFieldOfDomainEvent(rootPackage)))
                        .should(ArchConditions.beFinal())
                        .allowEmptyShould(true)
                        .because("领域事件必须不可变：事件实例字段必须为final"))
                .and(fields().that(nonStaticFieldOfDomainEvent(rootPackage))
                        .should(haveImmutableFieldType())
                        .allowEmptyShould(true)
                        .because("领域事件必须深度不可变：事件实例字段类型必须是不可变类型或不可变值对象"));
    }

    /**
     * 领域模型不得暴露公共setter：实体状态变更必须通过业务方法完成。
     */
    public static ArchRule domainDoesNotUsePublicSetters(String rootPackage) {
        return noMethods().that(publicSetterInDomain(rootPackage))
                .should(ArchConditions.bePublic())
                .allowEmptyShould(true)
                .because("实体状态变更必须通过业务方法完成，领域类不得暴露公共setter");
    }

    private static ArchCondition<JavaClass> declareExactlyOneIdentityField() {
        return new ArchCondition<JavaClass>("declare exactly one @Identity field whose type implements IIdentity") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                List<JavaField> identityFields = item.getAllFields().stream()
                        .filter(field -> !field.getModifiers().contains(JavaModifier.STATIC))
                        .filter(field -> field.isAnnotatedWith(Identity.class))
                        .collect(Collectors.toList());
                boolean satisfied = identityFields.size() == 1
                        && identityFields.get(0).getRawType().isAssignableTo(IIdentity.class);
                String message = String.format("%s declares %d @Identity field(s) [%s]",
                        item.getSimpleName(),
                        identityFields.size(),
                        identityFields.stream()
                                .map(field -> field.getName() + ":" + field.getRawType().getName())
                                .collect(Collectors.joining(", ")));
                events.add(new SimpleConditionEvent(item, satisfied, message));
            }
        };
    }

    private static ArchCondition<JavaClass> implementEqualsAndHashCode() {
        return new ArchCondition<JavaClass>("override equals() and hashCode() (not inherited from Object)") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                boolean equalsOverridden = item.tryGetMethod("equals", Object.class)
                        .map(method -> !method.getOwner().isEquivalentTo(Object.class))
                        .orElse(false);
                boolean hashCodeOverridden = item.tryGetMethod("hashCode")
                        .map(method -> !method.getOwner().isEquivalentTo(Object.class))
                        .orElse(false);
                boolean satisfied = equalsOverridden && hashCodeOverridden;
                String message = String.format("%s overrides equals=%s, hashCode=%s",
                        item.getSimpleName(), equalsOverridden, hashCodeOverridden);
                events.add(new SimpleConditionEvent(item, satisfied, message));
            }
        };
    }

    private static ArchCondition<JavaClass> haveMatchingDomainModelAnnotation() {
        return new ArchCondition<JavaClass>("be annotated with @DomainModel matching the implemented domain marker") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                DomainModel annotation = item.tryGetAnnotationOfType(DomainModel.class).orElse(null);
                DomainMetaModel expected = expectedDomainMetaModel(item);
                boolean satisfied = annotation != null && expected != null && annotation.value() == expected;
                String message = String.format("%s: @DomainModel=%s, expected=%s",
                        item.getSimpleName(), annotation == null ? "absent" : annotation.value(), expected);
                events.add(new SimpleConditionEvent(item, satisfied, message));
            }
        };
    }

    private static ArchCondition<JavaClass> haveConsistentMessageContractAnnotation() {
        return new ArchCondition<JavaClass>("be annotated with @MessageContract whose type and exchangePattern match the implemented marker") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                MessageContract annotation = item.tryGetAnnotationOfType(MessageContract.class).orElse(null);
                MessageContractType expected = expectedContractType(item);
                boolean typeMatched = annotation != null && expected != null && annotation.type() == expected;
                boolean patternMatched = annotation != null && exchangePatternMatches(annotation.type(), annotation.exchangePattern());
                boolean satisfied = typeMatched && patternMatched;
                String message = String.format("%s: @MessageContract.type=%s (expected=%s), exchangePattern=%s (expected=%s)",
                        item.getSimpleName(),
                        annotation == null ? "absent" : annotation.type(),
                        expected,
                        annotation == null ? "absent" : annotation.exchangePattern(),
                        annotation == null ? null : expectedExchangePattern(annotation.type()));
                events.add(new SimpleConditionEvent(item, satisfied, message));
            }
        };
    }

    /**
     * 交换方式与操作类型的约定：查询用RequestResponse，命令用FireAndForget（需要请求确认时可用RequestResponse），
     * 事件用RequestStream（发布/订阅），调度用FireAndForget或RequestResponse；通用REQUEST/RESPONSE/RESULT/VIEW不强制。
     */
    private static boolean exchangePatternMatches(MessageContractType type, MessageExchangePattern pattern) {
        return switch (type) {
            case COMMAND_REQUEST, SCHEDULING -> pattern == MessageExchangePattern.FireAndForget
                    || pattern == MessageExchangePattern.RequestResponse;
            case QUERY_REQUEST -> pattern == MessageExchangePattern.RequestResponse;
            case EVENT -> pattern == MessageExchangePattern.RequestStream;
            default -> true;
        };
    }

    private static String expectedExchangePattern(MessageContractType type) {
        return switch (type) {
            case COMMAND_REQUEST -> MessageExchangePattern.FireAndForget.name()
                    + " or " + MessageExchangePattern.RequestResponse.name()
                    + " (when acknowledgement is required)";
            case QUERY_REQUEST -> MessageExchangePattern.RequestResponse.name();
            case EVENT -> MessageExchangePattern.RequestStream.name();
            case SCHEDULING -> MessageExchangePattern.FireAndForget.name()
                    + " or " + MessageExchangePattern.RequestResponse.name();
            default -> null;
        };
    }

    private static ArchCondition<JavaClass> haveTypeMatchingName() {
        return new ArchCondition<JavaClass>("have a name matching its message contract type") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                MessageContract annotation = item.tryGetAnnotationOfType(MessageContract.class).orElse(null);
                String expectedSuffix = annotation == null ? null : expectedContractSuffix(annotation.type());
                boolean satisfied = expectedSuffix == null || item.getSimpleName().endsWith(expectedSuffix);
                String message = String.format("%s: name suffix expected=%s", item.getSimpleName(), expectedSuffix);
                events.add(new SimpleConditionEvent(item, satisfied, message));
            }
        };
    }

    private static String expectedContractSuffix(MessageContractType type) {
        return switch (type) {
            case COMMAND_REQUEST -> "CommandRequest";
            case QUERY_REQUEST -> "QueryRequest";
            case SCHEDULING -> "SchedulingRequest";
            case RESULT -> "Result";
            case VIEW -> "View";
            default -> null;
        };
    }

    private static ArchCondition<JavaClass> haveMatchingApplicationServiceAnnotation() {
        return new ArchCondition<JavaClass>("be annotated with @ApplicationService matching the implemented marker") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                ApplicationService annotation = item.tryGetAnnotationOfType(ApplicationService.class).orElse(null);
                ApplicationServiceType expected = expectedApplicationServiceType(item);
                boolean satisfied = annotation != null && (expected == null || annotation.value() == expected);
                String message = String.format("%s: @ApplicationService=%s, expected=%s",
                        item.getSimpleName(), annotation == null ? "absent" : annotation.value(), expected);
                events.add(new SimpleConditionEvent(item, satisfied, message));
            }
        };
    }

    private static ArchCondition<JavaClass> haveMatchingRemoteAnnotation() {
        return new ArchCondition<JavaClass>("be annotated with @Remote matching the implemented marker") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                Remote annotation = item.tryGetAnnotationOfType(Remote.class).orElse(null);
                RemoteType expected = expectedRemoteType(item);
                boolean satisfied = annotation != null && expected != null && annotation.value() == expected;
                String message = String.format("%s: @Remote=%s, expected=%s",
                        item.getSimpleName(), annotation == null ? "absent" : annotation.value(), expected);
                events.add(new SimpleConditionEvent(item, satisfied, message));
            }
        };
    }

    private static ArchCondition<JavaClass> haveMatchingPortAnnotation() {
        return new ArchCondition<JavaClass>("be annotated with @Port matching the implemented marker") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                Port annotation = item.tryGetAnnotationOfType(Port.class).orElse(null);
                PortType expected = expectedPortType(item);
                boolean satisfied = annotation != null && expected != null && annotation.value() == expected;
                String message = String.format("%s: @Port=%s, expected=%s",
                        item.getSimpleName(), annotation == null ? "absent" : annotation.value(), expected);
                events.add(new SimpleConditionEvent(item, satisfied, message));
            }
        };
    }

    private static ArchCondition<JavaClass> haveMatchingAdapterAnnotation() {
        return new ArchCondition<JavaClass>("be annotated with @Adapter matching the implemented marker") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                Adapter annotation = item.tryGetAnnotationOfType(Adapter.class).orElse(null);
                PortType expected = expectedAdapterType(item);
                boolean satisfied = annotation != null && expected != null && annotation.value() == expected;
                String message = String.format("%s: @Adapter=%s, expected=%s",
                        item.getSimpleName(), annotation == null ? "absent" : annotation.value(), expected);
                events.add(new SimpleConditionEvent(item, satisfied, message));
            }
        };
    }

    private static ArchCondition<JavaClass> resideInPackageAnnotatedWith(Class<? extends java.lang.annotation.Annotation> annotationType) {
        return new ArchCondition<JavaClass>("reside in a package annotated with @" + annotationType.getSimpleName()) {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                boolean satisfied = item.getPackage().isAnnotatedWith(annotationType);
                String message = String.format("%s resides in package %s annotated=%s",
                        item.getSimpleName(), item.getPackageName(), satisfied);
                events.add(new SimpleConditionEvent(item, satisfied, message));
            }
        };
    }

    private static ArchCondition<JavaClass> resideInOrUnderPackageAnnotatedWith(Class<? extends java.lang.annotation.Annotation> annotationType) {
        return new ArchCondition<JavaClass>("reside in or under a package annotated with @" + annotationType.getSimpleName()) {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                boolean satisfied = false;
                for (JavaPackage pkg = item.getPackage(); pkg != null; pkg = pkg.getParent().orElse(null)) {
                    if (pkg.isAnnotatedWith(annotationType)) {
                        satisfied = true;
                        break;
                    }
                }
                String message = String.format("%s resides in package %s annotated=%s",
                        item.getSimpleName(), item.getPackageName(), satisfied);
                events.add(new SimpleConditionEvent(item, satisfied, message));
            }
        };
    }

    private static ArchCondition<JavaClass> resideUnderSpecifiedPackageAnnotatedWith(
            String packageName, Class<? extends java.lang.annotation.Annotation> annotationType) {
        return new ArchCondition<JavaClass>("reside under package " + packageName + " annotated with @"
                + annotationType.getSimpleName()) {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                JavaPackage targetPackage = item.getPackage();
                while (targetPackage != null && !targetPackage.getName().equals(packageName)) {
                    targetPackage = targetPackage.getParent().orElse(null);
                }
                boolean satisfied = targetPackage != null && targetPackage.isAnnotatedWith(annotationType);
                String message = String.format("%s resides under package %s annotated=%s",
                        item.getSimpleName(), packageName, satisfied);
                events.add(new SimpleConditionEvent(item, satisfied, message));
            }
        };
    }

    private static DomainMetaModel expectedDomainMetaModel(JavaClass javaClass) {
        if (javaClass.isAssignableTo(IAggregateRoot.class)) {
            return DomainMetaModel.AggregateRoot;
        }
        if (javaClass.isAssignableTo(IIdentity.class)) {
            return DomainMetaModel.Identity;
        }
        if (javaClass.isAssignableTo(IDomainService.class)) {
            return DomainMetaModel.DomainService;
        }
        if (javaClass.isAssignableTo(IDomainEvent.class)) {
            return DomainMetaModel.DomainEvent;
        }
        if (javaClass.isAssignableTo(IValueObject.class)) {
            return DomainMetaModel.ValueObject;
        }
        if (javaClass.isAssignableTo(IEntity.class)) {
            return DomainMetaModel.Entity;
        }
        if (javaClass.isAssignableTo(IDomainFactory.class)) {
            return DomainMetaModel.DomainFactory;
        }
        return null;
    }

    private static MessageContractType expectedContractType(JavaClass javaClass) {
        if (javaClass.isAssignableTo(AbstractContractEvent.class)) {
            return MessageContractType.EVENT;
        }
        if (javaClass.isAssignableTo(ICommandRequest.class)) {
            return MessageContractType.COMMAND_REQUEST;
        }
        if (javaClass.isAssignableTo(IQueryRequest.class)) {
            return MessageContractType.QUERY_REQUEST;
        }
        if (javaClass.isAssignableTo(ISchedulingRequest.class)) {
            return MessageContractType.SCHEDULING;
        }
        if (javaClass.isAssignableTo(IResult.class)) {
            return MessageContractType.RESULT;
        }
        if (javaClass.isAssignableTo(IView.class)) {
            return MessageContractType.VIEW;
        }
        if (javaClass.isAssignableTo(IRequest.class)) {
            return MessageContractType.REQUEST;
        }
        if (javaClass.isAssignableTo(IResponse.class)) {
            return MessageContractType.RESPONSE;
        }
        return null;
    }

    private static ApplicationServiceType expectedApplicationServiceType(JavaClass javaClass) {
        if (javaClass.isAssignableTo(ICommandApplicationService.class)) {
            return ApplicationServiceType.COMMAND;
        }
        if (javaClass.isAssignableTo(IQueryApplicationService.class)) {
            return ApplicationServiceType.QUERY;
        }
        if (javaClass.isAssignableTo(IEventApplicationService.class)) {
            return ApplicationServiceType.EVENT;
        }
        if (javaClass.isAssignableTo(ISchedulingApplicationService.class)) {
            return ApplicationServiceType.SCHEDULING;
        }
        return null;
    }

    private static RemoteType expectedRemoteType(JavaClass javaClass) {
        if (javaClass.isAssignableTo(IControllerRemote.class)) {
            return RemoteType.Controller;
        }
        if (javaClass.isAssignableTo(IResourceRemote.class)) {
            return RemoteType.Resource;
        }
        if (javaClass.isAssignableTo(IProviderRemote.class)) {
            return RemoteType.Provider;
        }
        if (javaClass.isAssignableTo(ISubscriberRemote.class)) {
            return RemoteType.Subscriber;
        }
        if (javaClass.isAssignableTo(ISchedulerRemote.class)) {
            return RemoteType.Scheduler;
        }
        return null;
    }

    private static PortType expectedPortType(JavaClass javaClass) {
        if (javaClass.isAssignableTo(IRepositoryPort.class)) {
            return PortType.Repository;
        }
        if (javaClass.isAssignableTo(IClientPort.class)) {
            return PortType.Client;
        }
        if (javaClass.isAssignableTo(IFilePort.class)) {
            return PortType.File;
        }
        if (javaClass.isAssignableTo(IPublisherPort.class)) {
            return PortType.Publisher;
        }
        return null;
    }

    private static PortType expectedAdapterType(JavaClass javaClass) {
        if (javaClass.isAssignableTo(IRepositoryAdapter.class)) {
            return PortType.Repository;
        }
        if (javaClass.isAssignableTo(IClientAdapter.class)) {
            return PortType.Client;
        }
        if (javaClass.isAssignableTo(IFileAdapter.class)) {
            return PortType.File;
        }
        if (javaClass.isAssignableTo(IPublisherAdapter.class)) {
            return PortType.Publisher;
        }
        return null;
    }

    private static DescribedPredicate<JavaClass> concreteEntityInDomain(String rootPackage) {
        return new DescribedPredicate<JavaClass>("concrete IEntity implementations in the domain layer of " + rootPackage) {
            @Override
            public boolean test(JavaClass javaClass) {
                return inPackageTree(javaClass.getPackageName(), rootPackage + ".domain")
                        && javaClass.isAssignableTo(IEntity.class)
                        && !javaClass.isInterface()
                        && !javaClass.isEnum()
                        && !javaClass.getModifiers().contains(JavaModifier.ABSTRACT);
            }
        };
    }

    private static DescribedPredicate<JavaClass> domainModelCandidatesIn(String rootPackage) {
        return new DescribedPredicate<JavaClass>("domain model classes (marker implementations) in the domain layer of " + rootPackage) {
            @Override
            public boolean test(JavaClass javaClass) {
                return inPackageTree(javaClass.getPackageName(), rootPackage + ".domain")
                        && !javaClass.isInterface()
                        && !javaClass.isEnum()
                        && (javaClass.isAssignableTo(IEntity.class)
                        || javaClass.isAssignableTo(IValueObject.class)
                        || javaClass.isAssignableTo(IDomainService.class)
                        || javaClass.isAssignableTo(AbstractDomainEvent.class)
                        || javaClass.isAssignableTo(IDomainFactory.class));
            }
        };
    }

    private static DescribedPredicate<JavaClass> domainClassesExcludingExceptions(String rootPackage) {
        return new DescribedPredicate<JavaClass>("domain classes (excluding exceptions and enums) in the domain layer of " + rootPackage) {
            @Override
            public boolean test(JavaClass javaClass) {
                // 异常经Throwable天然可序列化，枚举按JLS规范隐式实现Serializable，均豁免
                return inPackageTree(javaClass.getPackageName(), rootPackage + ".domain")
                        && !javaClass.isAssignableTo(RuntimeException.class)
                        && !javaClass.isEnum();
            }
        };
    }

    private static DescribedPredicate<JavaClass> domainClassesExcludingFramework(String rootPackage) {
        return new DescribedPredicate<JavaClass>("domain classes (excluding the framework itself) in the domain layer of " + rootPackage) {
            @Override
            public boolean test(JavaClass javaClass) {
                return inPackageTree(javaClass.getPackageName(), rootPackage + ".domain")
                        && !inPackageTree(javaClass.getPackageName(), FRAMEWORK_BASE_PACKAGE + ".domain");
            }
        };
    }

    private static DescribedPredicate<JavaClass> concreteMessageContractsIn(String rootPackage) {
        return new DescribedPredicate<JavaClass>("concrete message contract classes in the message package of " + rootPackage) {
            @Override
            public boolean test(JavaClass javaClass) {
                return inPackageTree(javaClass.getPackageName(), rootPackage + ".message")
                        && !javaClass.isInterface()
                        && !javaClass.isEnum()
                        && !javaClass.getModifiers().contains(JavaModifier.ABSTRACT)
                        && (javaClass.isAssignableTo(IRequest.class)
                        || javaClass.isAssignableTo(IResponse.class)
                        || javaClass.isAssignableTo(AbstractContractEvent.class));
            }
        };
    }

    private static DescribedPredicate<JavaClass> concreteClassIn(String rootPackage, String subPackage) {
        return new DescribedPredicate<JavaClass>("concrete classes in the " + subPackage + " package of " + rootPackage) {
            @Override
            public boolean test(JavaClass javaClass) {
                return inPackageTree(javaClass.getPackageName(), rootPackage + subPackage)
                        && !javaClass.isInterface()
                        && !javaClass.isEnum()
                        && !javaClass.getModifiers().contains(JavaModifier.ABSTRACT);
            }
        };
    }

    private static DescribedPredicate<JavaClass> portCandidatesIn(String rootPackage) {
        return new DescribedPredicate<JavaClass>("port interfaces or classes (excluding IPort itself) in the domain.port package of " + rootPackage) {
            @Override
            public boolean test(JavaClass javaClass) {
                // 接口在ArchUnit中带有ABSTRACT修饰符，需放行接口（业务端口通常为接口），仅排除抽象类
                return inPackageTree(javaClass.getPackageName(), rootPackage + ".domain.port")
                        && javaClass.isAssignableTo(IPort.class)
                        && !javaClass.isEquivalentTo(IPort.class)
                        && !javaClass.isEnum()
                        && (javaClass.isInterface() || !javaClass.getModifiers().contains(JavaModifier.ABSTRACT));
            }
        };
    }

    /**
     * 根包树内的端口候选（IPort实现）：排除IPort自身、适配器实现类（适配器实现端口是依赖倒置的常态）
     * 与框架端口基接口所在包（框架包southbound.port，dogfooding豁免；不能按框架根前缀整体排除，
     * 框架包下的测试固件仍是待检对象）。
     */
    private static DescribedPredicate<JavaClass> portCandidatesInRoot(String rootPackage) {
        return new DescribedPredicate<JavaClass>("port candidates (IPort implementations, excluding adapters and the framework base ports) in " + rootPackage) {
            @Override
            public boolean test(JavaClass javaClass) {
                return (javaClass.getPackageName().equals(rootPackage) || javaClass.getPackageName().startsWith(rootPackage + "."))
                        && !javaClass.getPackageName().startsWith(FRAMEWORK_BASE_PACKAGE + ".southbound.port")
                        && javaClass.isAssignableTo(IPort.class)
                        && !javaClass.isEquivalentTo(IPort.class)
                        && !javaClass.isAssignableTo(IAdapter.class)
                        && !javaClass.isEnum()
                        && (javaClass.isInterface() || !javaClass.getModifiers().contains(JavaModifier.ABSTRACT));
            }
        };
    }

    private static DescribedPredicate<JavaClass> resideInRootPackageTree(String rootPackage) {
        return new DescribedPredicate<JavaClass>("classes in the root package tree of " + rootPackage) {
            @Override
            public boolean test(JavaClass javaClass) {
                String packageName = javaClass.getPackageName();
                return packageName.equals(rootPackage) || packageName.startsWith(rootPackage + ".");
            }
        };
    }

    private static DescribedPredicate<JavaClass> resideInDomainPackageTree(String rootPackage) {
        return new DescribedPredicate<JavaClass>("classes in the domain package tree of " + rootPackage) {
            @Override
            public boolean test(JavaClass javaClass) {
                return inPackageTree(javaClass.getPackageName(), rootPackage + ".domain");
            }
        };
    }

    private static DescribedPredicate<JavaClass> concreteAggregateRootInDomain(String rootPackage) {
        return new DescribedPredicate<JavaClass>("concrete aggregate roots in the domain layer of " + rootPackage) {
            @Override
            public boolean test(JavaClass javaClass) {
                return inPackageTree(javaClass.getPackageName(), rootPackage + ".domain")
                        && javaClass.isAssignableTo(IAggregateRoot.class)
                        && !javaClass.isInterface()
                        && !javaClass.isEnum()
                        && !javaClass.getModifiers().contains(JavaModifier.ABSTRACT);
            }
        };
    }

    private static DescribedPredicate<JavaClass> internalEntityInAggregatePackage(String rootPackage) {
        return new DescribedPredicate<JavaClass>("reside in an @AggregatePackage under the domain layer of " + rootPackage + " and implement IEntity but not IAggregateRoot") {
            @Override
            public boolean test(JavaClass javaClass) {
                // 聚合包位于领域层domain包下；嵌套类同样纳入检查
                return inPackageTree(javaClass.getPackage().getName(), rootPackage + ".domain")
                        && javaClass.getPackage().isAnnotatedWith(AggregatePackage.class)
                        && javaClass.isAssignableTo(IEntity.class)
                        && !javaClass.isAssignableTo(IAggregateRoot.class);
            }
        };
    }

    private static DescribedPredicate<JavaClass> domainValueObjectsIn(String rootPackage) {
        return new DescribedPredicate<JavaClass>("value objects (IValueObject implementations) in the domain layer of " + rootPackage) {
            @Override
            public boolean test(JavaClass javaClass) {
                return inPackageTree(javaClass.getPackageName(), rootPackage + ".domain")
                        && javaClass.isAssignableTo(IValueObject.class)
                        && !javaClass.isInterface()
                        && !javaClass.isEnum();
            }
        };
    }

    private static DescribedPredicate<JavaField> nonStaticFieldOfDomainValueObject(String rootPackage) {
        return new DescribedPredicate<JavaField>("non-static fields of domain value objects of " + rootPackage) {
            @Override
            public boolean test(JavaField field) {
                return domainValueObjectsIn(rootPackage).test(field.getOwner())
                        && !field.getModifiers().contains(JavaModifier.STATIC);
            }
        };
    }

    private static DescribedPredicate<JavaClass> domainEventsIn(String rootPackage) {
        return new DescribedPredicate<JavaClass>("domain events (AbstractDomainEvent subclasses) in the domain layer of " + rootPackage) {
            @Override
            public boolean test(JavaClass javaClass) {
                return inPackageTree(javaClass.getPackageName(), rootPackage + ".domain")
                        && javaClass.isAssignableTo(AbstractDomainEvent.class)
                        && !javaClass.isInterface()
                        && !javaClass.isEnum();
            }
        };
    }

    /**
     * domain包下实现IDomainEvent标记接口的具体类（无论是否已继承AbstractDomainEvent）——
     * 供domainEventsExtendBase强制其继承基类。
     */
    private static DescribedPredicate<JavaClass> concreteDomainEventCandidatesIn(String rootPackage) {
        return new DescribedPredicate<JavaClass>("concrete IDomainEvent implementations in the domain layer of " + rootPackage) {
            @Override
            public boolean test(JavaClass javaClass) {
                return inPackageTree(javaClass.getPackageName(), rootPackage + ".domain")
                        && javaClass.isAssignableTo(IDomainEvent.class)
                        && !javaClass.isInterface()
                        && !javaClass.isEnum()
                        && !javaClass.getModifiers().contains(JavaModifier.ABSTRACT);
            }
        };
    }

    private static DescribedPredicate<JavaField> nonStaticFieldOfDomainEvent(String rootPackage) {
        return new DescribedPredicate<JavaField>("non-static fields of domain events of " + rootPackage) {
            @Override
            public boolean test(JavaField field) {
                return domainEventsIn(rootPackage).test(field.getOwner())
                        && !field.getModifiers().contains(JavaModifier.STATIC);
            }
        };
    }

    private static DescribedPredicate<JavaMethod> publicSetterInDomain(String rootPackage) {
        return new DescribedPredicate<JavaMethod>("public setter methods in the domain layer of " + rootPackage) {
            @Override
            public boolean test(JavaMethod method) {
                return inPackageTree(method.getOwner().getPackageName(), rootPackage + ".domain")
                        && method.getModifiers().contains(JavaModifier.PUBLIC)
                        && method.getName().matches("set[A-Z].*");
            }
        };
    }

    private static DescribedPredicate<JavaMethod> factoryMethodInMessage(String rootPackage) {
        return new DescribedPredicate<JavaMethod>("methods named toXxx() (excluding toString) in the message package of " + rootPackage) {
            @Override
            public boolean test(JavaMethod method) {
                return inPackageTree(method.getOwner().getPackageName(), rootPackage + ".message")
                        && !method.getName().equals("toString")
                        && method.getName().matches("to[A-Z].*");
            }
        };
    }

    /**
     * rootPackage包树内的具体类（非接口、非枚举、非抽象类；注解类型按接口语义排除）。
     */
    private static DescribedPredicate<JavaClass> concreteClassInRoot(String rootPackage) {
        return new DescribedPredicate<JavaClass>("concrete classes in " + rootPackage) {
            @Override
            public boolean test(JavaClass javaClass) {
                return (javaClass.getPackageName().equals(rootPackage) || javaClass.getPackageName().startsWith(rootPackage + "."))
                        && !javaClass.isInterface()
                        && !javaClass.isEnum()
                        && !javaClass.getModifiers().contains(JavaModifier.ABSTRACT);
            }
        };
    }

    /**
     * 契约标记判断：实现IRequest/IResponse或继承AbstractContractEvent（即IContractEvent体系），
     * 但排除应用事件（AbstractApplicationEvent/IApplicationEvent体系——进程内协作机制，位于northbound.event而非message）。
     */
    private static boolean isContractMarkerType(JavaClass javaClass) {
        return (javaClass.isAssignableTo(IRequest.class)
                || javaClass.isAssignableTo(IResponse.class)
                || javaClass.isAssignableTo(IContractEvent.class))
                && !javaClass.isAssignableTo(IApplicationEvent.class);
    }

    private static DescribedPredicate<JavaClass> implementsContractMarker() {
        return new DescribedPredicate<JavaClass>("implement a message contract marker (IRequest/IResponse/IContractEvent, excluding application events)") {
            @Override
            public boolean test(JavaClass javaClass) {
                return isContractMarkerType(javaClass);
            }
        };
    }

    /**
     * 领域模型标记判断：实体（含聚合根）、值对象（含身份标识）、领域服务、领域事件、领域工厂。
     */
    private static DescribedPredicate<JavaClass> implementsDomainModelMarker() {
        return new DescribedPredicate<JavaClass>("implement a domain model marker (entity/aggregate root/value object/identity/domain service/domain event/domain factory)") {
            @Override
            public boolean test(JavaClass javaClass) {
                return javaClass.isAssignableTo(IEntity.class)
                        || javaClass.isAssignableTo(IValueObject.class)
                        || javaClass.isAssignableTo(IDomainService.class)
                        || javaClass.isAssignableTo(IDomainEvent.class)
                        || javaClass.isAssignableTo(IDomainFactory.class);
            }
        };
    }

    private static ArchCondition<JavaClass> implementContractMarker() {
        return new ArchCondition<JavaClass>("implement a message contract marker (IRequest/IResponse or AbstractContractEvent)") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                boolean satisfied = isContractMarkerType(item);
                String message = String.format("%s implements contract marker=%s", item.getSimpleName(), satisfied);
                events.add(new SimpleConditionEvent(item, satisfied, message));
            }
        };
    }

    private static ArchCondition<JavaClass> beImplementedByAdapter() {
        return new ArchCondition<JavaClass>("be implemented by at least one adapter (IAdapter implementation)") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                boolean satisfied = item.getAllSubclasses().stream()
                        .anyMatch(subclass -> subclass.isAssignableTo(IAdapter.class));
                String message = String.format("%s implemented by adapter=%s", item.getSimpleName(), satisfied);
                events.add(new SimpleConditionEvent(item, satisfied, message));
            }
        };
    }

    /**
     * 所在包内存在领域工厂实现类（IDomainFactory的非接口实现）。
     */
    private static DescribedPredicate<JavaClass> inAggregatePackageWithDomainFactory() {
        return new DescribedPredicate<JavaClass>("reside in a package containing a domain factory implementation") {
            @Override
            public boolean test(JavaClass javaClass) {
                return javaClass.getPackage().getClasses().stream()
                        .anyMatch(clazz -> clazz.isAssignableTo(IDomainFactory.class) && !clazz.isInterface());
            }
        };
    }

    private static ArchCondition<JavaClass> notHavePublicConstructors() {
        return new ArchCondition<JavaClass>("not have public constructors") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                boolean satisfied = item.getConstructors().stream()
                        .noneMatch(constructor -> constructor.getModifiers().contains(JavaModifier.PUBLIC));
                String message = String.format("%s has no public constructors=%s", item.getSimpleName(), satisfied);
                events.add(new SimpleConditionEvent(item, satisfied, message));
            }
        };
    }

    private static boolean isImmutableType(JavaClass type) {
        if (type.isPrimitive()) {
            return true;
        }
        if (DomainMetaModel.IMMUTABLE_TYPE_NAMES.contains(type.getName())) {
            return true;
        }
        if (type.isEnum()) {
            return true;
        }
        if (type.isAssignableTo(IValueObject.class)) {
            return true;
        }
        return false;
    }

    private static boolean inPackageTree(String packageName, String packageRoot) {
        return packageName.equals(packageRoot) || packageName.startsWith(packageRoot + ".");
    }

    private static <T> DescribedPredicate<T> describe(String description, DescribedPredicate<T> predicate) {
        return DescribedPredicate.describe(description, predicate);
    }
}