package wang.liangchen.matrix.framework.ddd.domain.valueobject;

/**
 * @author Liangchen.Wang
 * Marker interface for a Value Object that is part of an Aggregate.
 * 领域对象不可序列化直传：跨边界通信必须使用消息契约（发布语言），避免领域模型泄漏。
 */
public interface IValueObject {

}
