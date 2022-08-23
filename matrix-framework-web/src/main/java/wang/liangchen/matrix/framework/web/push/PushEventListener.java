package wang.liangchen.matrix.framework.web.push;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import wang.liangchen.matrix.framework.commons.object.ObjectUtil;

/**
 * @author LiangChen.Wang
 */
@Component
public class PushEventListener implements ApplicationListener<PushEvent> {
    @Override
    public void onApplicationEvent(PushEvent pushEvent) {
        if (pushEvent instanceof PushUnicastEvent) {
            PushUnicastEvent sseUnicastEvent = ObjectUtil.INSTANCE.cast(pushEvent);
            PushUtil.INSTANCE.unicast(sseUnicastEvent.getPusherKey(), pushEvent.getMessage());
            return;
        }
        if (pushEvent instanceof PushMulticastEvent) {
            PushMulticastEvent sseMulticastEvent = ObjectUtil.INSTANCE.cast(pushEvent);
            PushUtil.INSTANCE.multicast(sseMulticastEvent.getGroup(), pushEvent.getMessage());
            return;
        }
        PushUtil.INSTANCE.broadcast(pushEvent.getMessage());
    }
}
