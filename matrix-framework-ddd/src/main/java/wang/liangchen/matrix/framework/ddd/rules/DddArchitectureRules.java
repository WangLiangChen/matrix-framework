package wang.liangchen.matrix.framework.ddd.rules;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaField;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.domain.JavaModifier;
import com.tngtech.archunit.core.domain.JavaPackage;
import com.tngtech.archunit.core.domain.properties.HasModifiers;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.CompositeArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.lang.conditions.ArchConditions;
import wang.liangchen.matrix.framework.ddd.BoundedContextPackage;
import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainPackage;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.AggregatePackage;
import wang.liangchen.matrix.framework.ddd.domain.aggregate.IAggregateRoot;
import wang.liangchen.matrix.framework.ddd.domain.entity.IEntity;
import wang.liangchen.matrix.framework.ddd.domain.event.AbstractDomainEvent;
import wang.liangchen.matrix.framework.ddd.domain.factory.IDomainFactory;
import wang.liangchen.matrix.framework.ddd.domain.identity.IIdentity;
import wang.liangchen.matrix.framework.ddd.domain.identity.Identity;
import wang.liangchen.matrix.framework.ddd.southbound.port.IClientPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.IFilePort;
import wang.liangchen.matrix.framework.ddd.southbound.port.IPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.IPublisherPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.IRepositoryPort;
import wang.liangchen.matrix.framework.ddd.southbound.port.Port;
import wang.liangchen.matrix.framework.ddd.southbound.port.PortType;
import wang.liangchen.matrix.framework.ddd.domain.service.IDomainService;
import wang.liangchen.matrix.framework.ddd.domain.valueobject.IValueObject;
import wang.liangchen.matrix.framework.ddd.contract.MessageContract;
import wang.liangchen.matrix.framework.ddd.contract.MessageContractType;
import wang.liangchen.matrix.framework.ddd.contract.MessageDirection;
import wang.liangchen.matrix.framework.ddd.contract.MessageExchangePattern;
import wang.liangchen.matrix.framework.ddd.contract.event.AbstractContractEvent;
import wang.liangchen.matrix.framework.ddd.contract.request.ICommandRequest;
import wang.liangchen.matrix.framework.ddd.contract.request.IQueryRequest;
import wang.liangchen.matrix.framework.ddd.contract.request.IRequest;
import wang.liangchen.matrix.framework.ddd.contract.request.ISchedulingRequest;
import wang.liangchen.matrix.framework.ddd.contract.response.IResponse;
import wang.liangchen.matrix.framework.ddd.contract.response.IResult;
import wang.liangchen.matrix.framework.ddd.contract.response.IView;
import wang.liangchen.matrix.framework.ddd.northbound.event.AbstractApplicationEvent;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ApplicationServiceType;
import wang.liangchen.matrix.framework.ddd.northbound.local.IApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ICommandApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.IEventApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.IQueryApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.local.ISchedulingApplicationService;
import wang.liangchen.matrix.framework.ddd.northbound.remote.IControllerRemote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.IProviderRemote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.IRemote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.IResourceRemote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.ISchedulerRemote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.ISubscriberRemote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.Remote;
import wang.liangchen.matrix.framework.ddd.northbound.remote.RemoteType;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.Adapter;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.IAdapter;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.IClientAdapter;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.IFileAdapter;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.IPublisherAdapter;
import wang.liangchen.matrix.framework.ddd.southbound.adapter.IRepositoryAdapter;

import static com.tngtech.archunit.base.DescribedPredicate.not;
import static com.tngtech.archunit.core.domain.JavaClass.Predicates.assignableTo;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;

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
 * }
 * </pre>
 * 约定包结构：{rootPackage}.domain（含domain.port）/ .message / .northbound.{local,remote,event,exception} / .southbound.adapter
 * 业务模块需在pom中声明archunit测试依赖（框架以optional方式依赖archunit）。
 * 规则依赖RUNTIME retention的框架注解（如@AggregatePackage），注解已进入字节码。
 *
 * @author Liangchen.Wang
 */
public final class DddArchitectureRules {

    private DddArchitectureRules() {
    }

    /**
     * 分层依赖规则全集：领域层纯净性、消息契约隔离、端口依赖倒置、
     * 应用服务经端口访问外部资源、远程服务只操作消息契约、
     * 聚合等领域模型类（领域服务除外）不得使用端口。
     */
    public static ArchRule layeredDependencyRules(String rootPackage) {
        return CompositeArchRule.of(domainDoesNotDependOnMessage(rootPackage))
                .and(domainDoesNotDependOnNorthbound(rootPackage))
                .and(domainDoesNotDependOnSouthboundAdapter(rootPackage))
                .and(domainModelClassesDoNotDependOnPorts(rootPackage))
                .and(messageDoesNotDependOnDomain(rootPackage))
                .and(portDoesNotDependOnAdapter(rootPackage))
                .and(applicationServiceDoesNotDependOnAdapter(rootPackage))
                .and(applicationServiceDoesNotDependOnRemote(rootPackage))
                .and(remoteDoesNotDependOnDomain(rootPackage))
                .and(remoteDoesNotDependOnAdapter(rootPackage));
    }

    /**
     * 领域模型规则全集：统一语言命名、领域模型注解、实体身份标识与相等性、聚合封装、
     * 值对象不可变、领域事件不可变、无公共setter、领域对象不可序列化。
     */
    public static ArchRule domainModelRules(String rootPackage) {
        return CompositeArchRule.of(domainNamingRule(rootPackage))
                .and(domainModelsAnnotated(rootPackage))
                .and(entitiesDeclareIdentity(rootPackage))
                .and(entitiesImplementEqualsAndHashCode(rootPackage))
                .and(aggregateInternalEntityEncapsulation(rootPackage))
                .and(valueObjectImmutability(rootPackage))
                .and(eventImmutability(rootPackage))
                .and(domainDoesNotUsePublicSetters(rootPackage))
                .and(domainDoesNotImplementSerializable(rootPackage));
    }

    /**
     * 消息契约规则全集：契约注解与标记接口匹配、契约命名规范、契约不得担任工厂。
     */
    public static ArchRule messageContractRules(String rootPackage) {
        return CompositeArchRule.of(messageContractsAnnotated(rootPackage))
                .and(messageContractNaming(rootPackage))
                .and(messageContractsDoNotProvideFactoryMethods(rootPackage));
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
     * 架构标注规则全集：应用服务、应用事件、远程服务、端口与适配器必须标注对应注解/继承对应基类且注解值与标记接口匹配。
     */
    public static ArchRule architectureAnnotationRules(String rootPackage) {
        return CompositeArchRule.of(applicationServicesAnnotated(rootPackage))
                .and(applicationEventsExtendBase(rootPackage))
                .and(remotesAnnotated(rootPackage))
                .and(portsAnnotated(rootPackage))
                .and(adaptersAnnotated(rootPackage));
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
                .should().implement(Serializable.class)
                .allowEmptyShould(true)
                .because("领域对象不可序列化直传：跨边界通信一律经消息契约，由消息总线以发布语言序列化传输");
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
                .should(resideInOrUnderPackageAnnotatedWith(BoundedContextPackage.class))
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
     * 领域层不得依赖消息契约(message)：领域模型与消息契约的转换（装配）只发生在应用服务内。
     */
    public static ArchRule domainDoesNotDependOnMessage(String rootPackage) {
        return noClasses().that().resideInAPackage(rootPackage + ".domain..")
                .should().dependOnClassesThat().resideInAPackage(rootPackage + ".message..")
                .allowEmptyShould(true)
                .because("领域层不得依赖消息契约，领域模型与消息契约的装配只发生在应用服务内");
    }

    /**
     * 领域层不得依赖北向接口（应用服务/远程服务）：依赖方向只能由外向内。
     */
    public static ArchRule domainDoesNotDependOnNorthbound(String rootPackage) {
        return noClasses().that().resideInAPackage(rootPackage + ".domain..")
                .should().dependOnClassesThat().resideInAPackage(rootPackage + ".northbound..")
                .allowEmptyShould(true)
                .because("领域层不得依赖应用层与远程层，依赖方向只能由外向内");
    }

    /**
     * 领域层不得依赖南向适配器实现：领域层通过端口声明依赖，适配器实现端口（依赖倒置）。
     */
    public static ArchRule domainDoesNotDependOnSouthboundAdapter(String rootPackage) {
        return noClasses().that().resideInAPackage(rootPackage + ".domain..")
                .should().dependOnClassesThat().resideInAPackage(rootPackage + ".southbound.adapter..")
                .allowEmptyShould(true)
                .because("领域层通过端口声明对外部资源的依赖，适配器实现端口（依赖倒置），领域层不得依赖适配器实现");
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
                        "wang.liangchen.matrix.framework.ddd.domain..")
                .allowEmptyShould(true)
                .because("消息契约是发布语言，不得依赖领域模型（含框架领域类型），跨边界通信必须与领域对象隔离");
    }

    /**
     * 端口不得反向依赖适配器实现。
     */
    public static ArchRule portDoesNotDependOnAdapter(String rootPackage) {
        return noClasses().that().resideInAPackage(rootPackage + ".domain.port..")
                .should().dependOnClassesThat().resideInAPackage(rootPackage + ".southbound.adapter..")
                .allowEmptyShould(true)
                .because("端口由适配器实现，端口不得反向依赖适配器");
    }

    /**
     * 应用服务不得依赖南向适配器实现：应用服务通过端口接口访问外部资源。
     */
    public static ArchRule applicationServiceDoesNotDependOnAdapter(String rootPackage) {
        return noClasses().that().resideInAPackage(rootPackage + ".northbound.local..")
                .should().dependOnClassesThat().resideInAPackage(rootPackage + ".southbound.adapter..")
                .allowEmptyShould(true)
                .because("应用服务通过端口接口访问外部资源，不得依赖适配器实现");
    }

    /**
     * 应用服务不得依赖远程层：远程服务通过应用服务完成用例编排，依赖方向只能由外向内。
     */
    public static ArchRule applicationServiceDoesNotDependOnRemote(String rootPackage) {
        return noClasses().that().resideInAPackage(rootPackage + ".northbound.local..")
                .should().dependOnClassesThat().resideInAPackage(rootPackage + ".northbound.remote..")
                .allowEmptyShould(true)
                .because("应用服务不得依赖远程层，远程服务通过应用服务完成用例编排");
    }

    /**
     * 远程服务只操作消息契约，不直接访问领域对象。
     */
    public static ArchRule remoteDoesNotDependOnDomain(String rootPackage) {
        return noClasses().that().resideInAPackage(rootPackage + ".northbound.remote..")
                .should().dependOnClassesThat().resideInAPackage(rootPackage + ".domain..")
                .allowEmptyShould(true)
                .because("远程服务只操作消息契约，通过应用服务完成用例编排，不直接访问领域对象");
    }

    /**
     * 远程服务不得直接依赖南向适配器：外部资源访问须经应用服务与端口。
     */
    public static ArchRule remoteDoesNotDependOnAdapter(String rootPackage) {
        return noClasses().that().resideInAPackage(rootPackage + ".northbound.remote..")
                .should().dependOnClassesThat().resideInAPackage(rootPackage + ".southbound.adapter..")
                .allowEmptyShould(true)
                .because("远程服务不得直接依赖南向适配器，外部资源访问须经应用服务与端口");
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
     * 值对象不可变：值对象类必须为final，实例字段必须为final（静态常量除外）。
     * 接口、枚举与抽象基类不要求final，但其具体实现类必须为final。
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
                        .because("值对象必须不可变：值对象的实例字段必须为final"));
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
                        .because("领域事件必须不可变：事件实例字段必须为final"));
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
     * 交换方式与操作类型的约定：查询用RequestResponse，命令用FireAndForget，事件用RequestStream（发布/订阅），
     * 调度用FireAndForget或RequestResponse；通用REQUEST/RESPONSE/RESULT/VIEW不强制。
     */
    private static boolean exchangePatternMatches(MessageContractType type, MessageExchangePattern pattern) {
        switch (type) {
            case COMMAND_REQUEST:
                return pattern == MessageExchangePattern.FireAndForget;
            case QUERY_REQUEST:
                return pattern == MessageExchangePattern.RequestResponse;
            case EVENT:
                return pattern == MessageExchangePattern.RequestStream;
            case SCHEDULING:
                return pattern == MessageExchangePattern.FireAndForget || pattern == MessageExchangePattern.RequestResponse;
            default:
                return true;
        }
    }

    private static String expectedExchangePattern(MessageContractType type) {
        switch (type) {
            case COMMAND_REQUEST:
                return MessageExchangePattern.FireAndForget.name();
            case QUERY_REQUEST:
                return MessageExchangePattern.RequestResponse.name();
            case EVENT:
                return MessageExchangePattern.RequestStream.name();
            case SCHEDULING:
                return MessageExchangePattern.FireAndForget.name() + " or " + MessageExchangePattern.RequestResponse.name();
            default:
                return null;
        }
    }

    private static ArchCondition<JavaClass> haveTypeMatchingName() {
        return new ArchCondition<JavaClass>("have a name matching its message contract type") {
            @Override
            public void check(JavaClass item, ConditionEvents events) {
                MessageContract annotation = item.tryGetAnnotationOfType(MessageContract.class).orElse(null);
                String expectedSuffix = null;
                if (annotation != null) {
                    switch (annotation.type()) {
                        case COMMAND_REQUEST:
                            expectedSuffix = "CommandRequest";
                            break;
                        case QUERY_REQUEST:
                            expectedSuffix = "QueryRequest";
                            break;
                        case SCHEDULING:
                            expectedSuffix = "SchedulingRequest";
                            break;
                        case RESULT:
                            expectedSuffix = "Result";
                            break;
                        case VIEW:
                            expectedSuffix = "View";
                            break;
                        default:
                            expectedSuffix = null;
                    }
                }
                boolean satisfied = expectedSuffix == null || item.getSimpleName().endsWith(expectedSuffix);
                String message = String.format("%s: name suffix expected=%s", item.getSimpleName(), expectedSuffix);
                events.add(new SimpleConditionEvent(item, satisfied, message));
            }
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
        if (javaClass.isAssignableTo(AbstractDomainEvent.class)) {
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
                return javaClass.getPackageName().startsWith(rootPackage + ".domain")
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
                return javaClass.getPackageName().startsWith(rootPackage + ".domain")
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
                return javaClass.getPackageName().startsWith(rootPackage + ".domain")
                        && !javaClass.isAssignableTo(RuntimeException.class)
                        && !javaClass.isEnum();
            }
        };
    }

    private static DescribedPredicate<JavaClass> domainClassesExcludingFramework(String rootPackage) {
        return new DescribedPredicate<JavaClass>("domain classes (excluding the framework itself) in the domain layer of " + rootPackage) {
            @Override
            public boolean test(JavaClass javaClass) {
                return javaClass.getPackageName().startsWith(rootPackage + ".domain")
                        && !javaClass.getPackageName().startsWith("wang.liangchen.matrix.framework.ddd.domain");
            }
        };
    }

    private static DescribedPredicate<JavaClass> concreteMessageContractsIn(String rootPackage) {
        return new DescribedPredicate<JavaClass>("concrete message contract classes in the message package of " + rootPackage) {
            @Override
            public boolean test(JavaClass javaClass) {
                return javaClass.getPackageName().startsWith(rootPackage + ".message")
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
                return javaClass.getPackageName().startsWith(rootPackage + subPackage)
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
                return javaClass.getPackageName().startsWith(rootPackage + ".domain.port")
                        && javaClass.isAssignableTo(IPort.class)
                        && !javaClass.isEquivalentTo(IPort.class)
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
                return javaClass.getPackageName().startsWith(rootPackage + ".domain");
            }
        };
    }

    private static DescribedPredicate<JavaClass> concreteAggregateRootInDomain(String rootPackage) {
        return new DescribedPredicate<JavaClass>("concrete aggregate roots in the domain layer of " + rootPackage) {
            @Override
            public boolean test(JavaClass javaClass) {
                return javaClass.getPackageName().startsWith(rootPackage + ".domain")
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
                return javaClass.getPackage().getName().startsWith(rootPackage + ".domain")
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
                return javaClass.getPackageName().startsWith(rootPackage + ".domain")
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
                return javaClass.getPackageName().startsWith(rootPackage + ".domain")
                        && javaClass.isAssignableTo(AbstractDomainEvent.class)
                        && !javaClass.isInterface()
                        && !javaClass.isEnum();
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
                return method.getOwner().getPackageName().startsWith(rootPackage + ".domain")
                        && method.getModifiers().contains(JavaModifier.PUBLIC)
                        && method.getName().matches("set[A-Z].*");
            }
        };
    }

    private static DescribedPredicate<JavaMethod> factoryMethodInMessage(String rootPackage) {
        return new DescribedPredicate<JavaMethod>("methods named toXxx() (excluding toString) in the message package of " + rootPackage) {
            @Override
            public boolean test(JavaMethod method) {
                return method.getOwner().getPackageName().startsWith(rootPackage + ".message")
                        && !method.getName().equals("toString")
                        && method.getName().matches("to[A-Z].*");
            }
        };
    }

    private static <T> DescribedPredicate<T> describe(String description, DescribedPredicate<T> predicate) {
        return DescribedPredicate.describe(description, predicate);
    }
}
