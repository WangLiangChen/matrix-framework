package wang.liangchen.matrix.framework.ddd.domain.factory;

import wang.liangchen.matrix.framework.ddd.domain.DomainMetaModel;
import wang.liangchen.matrix.framework.ddd.domain.DomainModel;

import java.util.Collection;
import java.util.Objects;

/**
 * 领域工厂抽象基类，提供领域对象创建的骨架支持与常用验证方法。
 * <p>
 * <b>一、本质</b>
 * <ul>
 *   <li>工厂是领域模型的一部分，封装的是<b>领域知识</b>（创建规则、不变式保证），
 *       而非技术细节。工厂方法参数应为领域概念（值对象、身份标识等），
 *       而非基本类型，以保证领域语义清晰。</li>
 *   <li>将复杂的创建逻辑从聚合根中剥离，保持聚合根的内聚性，
 *       同时让创建过程可测试、可复用、易于维护。</li>
 *   <li>保证聚合不变式：工厂在创建聚合时必须确保聚合满足所有业务规则与约束条件，
 *       使产出的聚合始终处于合法状态。</li>
 * </ul>
 * <p>
 * <b>二、两种创建模式</b>
 * <ul>
 *   <li><b>create</b>：创建全新的聚合。通常包括身份标识的生成与初始状态的赋予，
 *       必须执行完整的业务验证以保证不变式。</li>
 *   <li><b>reconstitute</b>：从持久化数据重建已有聚合，由领域仓储委托调用。
 *       仓储将重建逻辑委托给工厂而非在适配器中直接构造，保证创建逻辑集中——
 *       无论 create 还是 reconstitute，创建规则与不变式保证都统一由工厂承载。
 *       重建时假定历史数据已合法，可跳过部分业务验证（如唯一性校验），
 *       但仍需保证聚合结构完整性。</li>
 * </ul>
 * <p>
 * <b>三、工厂角色承担方式</b>（从简到繁）
 * <ol>
 *   <li><b>聚合自身担任工厂</b>：在聚合根中提供静态工厂方法来创建聚合产品实例，
 *       聚合产品的构造方法设置为私有。方法可以使用 of、valueOf、from 等方法名。
 *       适用于创建逻辑简单、与聚合自身紧密相关的场景。</li>
 *   <li><b>被依赖聚合担任工厂</b>：如 Blog 与 Post 分属两个聚合时，
 *       可由 Blog.createPost() 创建 Post 聚合根；
 *       若 Post 是 Blog 聚合的内部实体，createPost 属于聚合根的普通职责，不在此列。</li>
 *   <li><b>专门的聚合工厂类</b>：将工厂类和聚合产品放在同一个包，
 *       且将聚合根的构造方法设置为包内可见，来保证聚合根只能通过工厂来创建。
 *       适用于创建逻辑复杂、需要独立可测试的场景。</li>
 *   <li><b>装配器担任工厂</b>：装配器位于应用层，入站装配时兼任工厂创建领域对象；
 *       消息契约不能引用领域模型，更不能担任工厂。</li>
 *   <li><b>构建者模式组装聚合</b>：对创建步骤多、参数可选的聚合，以 Builder 逐步组装。</li>
 * </ol>
 * <p>
 * <b>四、引入条件</b>
 * <ul>
 *   <li>引入：创建逻辑复杂（多对象组装、依赖初始化、复杂验证规则、reconstitute 重建）；
 *       或需要保证创建逻辑集中（仓储重建委托工厂）。</li>
 *   <li>不引入：构造简单，构造函数足以表达创建意图。</li>
 * </ul>
 * <p>
 * <b>五、约束</b>
 * <ul>
 *   <li>工厂不得依赖端口（资源库等外部资源），外部资源访问由应用服务或领域服务完成
 *       （不能在聚合内部使用资源库）。</li>
 *   <li>消息契约不能担任工厂（契约是纯数据模型，不得引用领域模型）。</li>
 *   <li>实现类必须自行标注 @DomainModel(DomainMetaModel.DomainFactory)
 *       （@DomainModel 无 @Inherited，标注不会沿继承传播）。</li>
 *   <li>存在领域工厂时，聚合根构造方法不得 public（由框架 ArchUnit 规则守护）。</li>
 *   <li>身份标识在创建时生成（工厂/静态工厂/构造函数），不由持久化层生成。</li>
 * </ul>
 *
 * @author Liangchen.Wang
 */
@DomainModel(DomainMetaModel.DomainFactory)
public abstract class AbstractDomainFactory implements IDomainFactory {

    protected void requireNonNull(Object value, String name) {
        Objects.requireNonNull(value, name + " must not be null");
    }

    protected void requireNonBlank(String value, String name) {
        Objects.requireNonNull(value, name + " must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
    }

    protected void requireNonEmpty(Collection<?> value, String name) {
        Objects.requireNonNull(value, name + " must not be null");
        if (value.isEmpty()) {
            throw new IllegalArgumentException(name + " must not be empty");
        }
    }

    protected void requirePositive(long value, String name) {
        if (value <= 0) {
            throw new IllegalArgumentException(name + " must be positive, but was: " + value);
        }
    }

    protected void requireNonNegative(long value, String name) {
        if (value < 0) {
            throw new IllegalArgumentException(name + " must not be negative, but was: " + value);
        }
    }
}