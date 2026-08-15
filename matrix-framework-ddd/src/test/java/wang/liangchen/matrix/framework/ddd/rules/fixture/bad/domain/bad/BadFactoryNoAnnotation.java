package wang.liangchen.matrix.framework.ddd.rules.fixture.bad.domain.bad;

import wang.liangchen.matrix.framework.ddd.domain.factory.IDomainFactory;

/** 违规：实现IDomainFactory但未标注@DomainModel(DomainFactory) */
public final class BadFactoryNoAnnotation implements IDomainFactory {
}
