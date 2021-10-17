package liangchen.wang.matrix.framework.web.sse;

/**
 * @author LiangChen.Wang
 */
public class SseMulticastEvent extends SseEvent {
    private final String group;

    public SseMulticastEvent newInstance(Object source, String group, String message) {
        return new SseMulticastEvent(source, group, message);
    }

    private SseMulticastEvent(Object source, String group, String message) {
        super(source, message);
        this.group = group;
    }

    public String getGroup() {
        return group;
    }
}
