package wang.liangchen.matrix.framework.web.push;

/**
 * @author LiangChen.Wang
 */
public final class PushMulticastEvent extends PushEvent {
    private final String group;

    public PushMulticastEvent newInstance(Object source, String group, String message) {
        return new PushMulticastEvent(source, group, message);
    }

    private PushMulticastEvent(Object source, String group, String message) {
        super(source, message);
        this.group = group;
    }

    public String getGroup() {
        return group;
    }
}
