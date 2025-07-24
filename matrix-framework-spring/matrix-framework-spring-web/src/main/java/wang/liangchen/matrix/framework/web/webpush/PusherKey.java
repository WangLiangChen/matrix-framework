package wang.liangchen.matrix.framework.web.webpush;

import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * @author LiangChen.Wang
 */
public final class PusherKey {
    private final static String NAMEKEY = "name";
    private final static String GROUPKEY = "group";

    private final String name;
    private final String group;
    private final Map<String, String> queryParams;
    private final String body;

    public PusherKey(Map<String, String> queryParams, String body) {
        ValidationUtil.INSTANCE.notNullAndEmpty(queryParams, "queryParams must not be empty,at least include name and group");
        this.queryParams = queryParams;
        this.body = body;
        this.name = queryParams.get(NAMEKEY);
        ValidationUtil.INSTANCE.notNullAndBlank(this.name, "name must not be blank");
        this.group = queryParams.get(GROUPKEY);
        ValidationUtil.INSTANCE.notNullAndBlank(this.group, "group must not be blank");
    }

    public static PusherKey newInstance(Map<String, String> queryParams, String body) {
        return new PusherKey(queryParams, body);
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public String getBody() {
        return body;
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

    @Override
    public String toString() {
        return new StringJoiner(", ", PusherKey.class.getSimpleName() + "[", "]")
                .add("name='" + name + "'")
                .add("group='" + group + "'")
                .add("queryParams=" + queryParams)
                .add("body='" + body + "'")
                .toString();
    }
}
