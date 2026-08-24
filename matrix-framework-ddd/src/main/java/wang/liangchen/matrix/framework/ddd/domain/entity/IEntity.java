package wang.liangchen.matrix.framework.ddd.domain.entity;

import wang.liangchen.matrix.framework.ddd.domain.identity.IIdentity;

/**
 * @author Liangchen.Wang 2022-11-25 15:23
 * Marker interface for a Entity that is part of an Aggregate.
 * 实体具有唯一身份标识：实体的身份标识字段使用@Identity注解标注。
 * 领域对象不可序列化直传, 不继承Serializable接口：跨边界通信必须使用消息契约（发布语言），避免领域模型泄漏。
 */
public interface IEntity<ID extends IIdentity> {

}
