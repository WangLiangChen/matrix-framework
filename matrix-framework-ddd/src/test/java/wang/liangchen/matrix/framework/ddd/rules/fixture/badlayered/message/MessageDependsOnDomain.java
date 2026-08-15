package wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.message;

import wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.domain.SomeDomainThing;

/** 违规：消息契约依赖领域模型（发布语言与领域模型隔离） */
public final class MessageDependsOnDomain {

    @SuppressWarnings("unused")
    private final SomeDomainThing domain;

    public MessageDependsOnDomain(SomeDomainThing domain) {
        this.domain = domain;
    }
}
