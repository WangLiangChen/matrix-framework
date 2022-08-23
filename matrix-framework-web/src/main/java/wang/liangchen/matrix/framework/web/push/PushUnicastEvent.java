package wang.liangchen.matrix.framework.web.push;

/**
 * @author LiangChen.Wang
 */
public final class PushUnicastEvent extends PushEvent {
    private final PusherKey pusherKey;

    public PushUnicastEvent newInstance(Object source, PusherKey pusherKey, String message) {
        return new PushUnicastEvent(source, pusherKey, message);
    }

    public PushUnicastEvent newInstance(Object source, String name, String group, String message) {
        return new PushUnicastEvent(source, name, group, message);
    }

    private PushUnicastEvent(Object source, PusherKey pusherKey, String message) {
        super(source, message);
        this.pusherKey = pusherKey;
    }

    private PushUnicastEvent(Object source, String name, String group, String message) {
        super(source, message);
        this.pusherKey = PusherKey.newInstance(name, group);
    }

    public PusherKey getPusherKey() {
        return pusherKey;
    }
}
