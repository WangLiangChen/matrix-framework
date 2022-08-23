package wang.liangchen.matrix.framework.web.push;

import java.util.Objects;

/**
 * @author LiangChen.Wang
 */
public final class PusherKey {
    private final String name;
    private final String group;

    public static PusherKey newInstance(String name, String group) {
        return new PusherKey(name, group);
    }

    public PusherKey(String name, String group) {
        this.name = name;
        this.group = group;
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PusherKey pusherKey = (PusherKey) o;

        if (!Objects.equals(name, pusherKey.name)) return false;
        return Objects.equals(group, pusherKey.group);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (group != null ? group.hashCode() : 0);
        return result;
    }
}
