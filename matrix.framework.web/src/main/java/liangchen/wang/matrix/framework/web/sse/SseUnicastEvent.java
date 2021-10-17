package liangchen.wang.matrix.framework.web.sse;

/**
 * @author LiangChen.Wang
 */
public class SseUnicastEvent extends SseEvent {
    private final SseKey sseKey;

    public SseUnicastEvent newInstance(Object source, SseKey sseKey, String message) {
        return new SseUnicastEvent(source, sseKey, message);
    }

    public SseUnicastEvent newInstance(Object source, String name, String group, String message) {
        return new SseUnicastEvent(source, name, group, message);
    }

    private SseUnicastEvent(Object source, SseKey sseKey, String message) {
        super(source, message);
        this.sseKey = sseKey;
    }

    private SseUnicastEvent(Object source, String name, String group, String message) {
        super(source, message);
        this.sseKey = SseKey.newInstance(name, group);
    }

    public SseKey getSseKey() {
        return sseKey;
    }
}
