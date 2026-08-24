package wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.message;

import wang.liangchen.matrix.framework.ddd.rules.fixture.badlayered.northbound.local.SomeAppService;

/** 违规：消息契约依赖北向应用服务（发布语言是自治的通信语言，不引用进程内协作机制） */
public final class MessageDependsOnNorthbound {

    @SuppressWarnings("unused")
    private final SomeAppService appService;

    public MessageDependsOnNorthbound(SomeAppService appService) {
        this.appService = appService;
    }
}
