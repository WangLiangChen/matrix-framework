package wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.domain;

import wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.message.SomeMessage;

/** 违规：领域层依赖消息契约 */
public final class DomainDependsOnMessage {

    @SuppressWarnings("unused")
    private final SomeMessage message;

    public DomainDependsOnMessage(SomeMessage message) {
        this.message = message;
    }
}
