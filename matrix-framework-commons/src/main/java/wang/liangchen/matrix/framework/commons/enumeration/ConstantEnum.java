package wang.liangchen.matrix.framework.commons.enumeration;

import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.io.Serializable;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Liangchen.Wang 2022-07-08 22:51
 * <code>
 * public final static ConstantEnum NONE = new ConstantEnum("NONE", "无状态");
 * </code>
 */
public class ConstantEnum implements Serializable {
    private final static Map<String, ? super ConstantEnum> map = new ConcurrentHashMap<>(16);
    private final String key;
    private final String value;
    public final static ConstantEnum DEFAULT = new ConstantEnum("DEFAULT", "默认");

    public ConstantEnum(String key, String value) {
        this.key = key;
        this.value = value;
        map.put(this.key, this);
    }

    public static <T extends ConstantEnum> T valueOf(String key) {
        ValidationUtil.INSTANCE.notBlank(key);
        return ObjectUtil.INSTANCE.cast(map.get(key));
    }

    public static String value(String key) {
        ValidationUtil.INSTANCE.notBlank(key);
        Object object = map.get(key);
        if (null == object) {
            return null;
        }
        return ((ConstantEnum) object).value;
    }

    public String key() {
        return this.key;
    }

    public String value() {
        return this.value;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("key='" + key + "'")
                .add("value='" + value + "'")
                .toString();
    }
}
