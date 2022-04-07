package wang.liangchen.matrix.framework.web.sse;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;

/**
 * @author LiangChen.Wang
 */
@Component
public class SseEventListener implements ApplicationListener<SseEvent> {
    @Override
    public void onApplicationEvent(SseEvent sseEvent) {
        if (sseEvent instanceof SseUnicastEvent) {
            SseUnicastEvent sseUnicastEvent = ObjectUtil.INSTANCE.cast(sseEvent);
            SseUtil.INSTANCE.unicast(sseUnicastEvent.getSseKey(), sseEvent.getMessage());
            return;
        }
        if (sseEvent instanceof SseMulticastEvent) {
            SseMulticastEvent sseMulticastEvent = ObjectUtil.INSTANCE.cast(sseEvent);
            SseUtil.INSTANCE.multicast(sseMulticastEvent.getGroup(), sseEvent.getMessage());
            return;
        }
        SseUtil.INSTANCE.broadcast(sseEvent.getMessage());
    }
}
