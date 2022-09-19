package wang.liangchen.matrix.framework.commons.enumeration;

import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Liangchen.Wang 2022-07-08 22:51
 * <code>
 * public final static ConstantEnum NONE = new ConstantEnum("NONE", "无状态");
 * </code>
 */
public class ConstantEnum implements Serializable {
    private final static Map<String, ? super ConstantEnum> map = new ConcurrentHashMap<>(16);
    private final String name;
    private final String value;

    public ConstantEnum(String name, String value) {
        this.name = name;
        this.value = value;
        map.put(this.name, this);
    }

    public static <T extends ConstantEnum> T valueOf(String name) {
        ValidationUtil.INSTANCE.notBlank(name);
        return ObjectUtil.INSTANCE.cast(map.get(name));
    }

    public static String value(String name) {
        ValidationUtil.INSTANCE.notBlank(name);
        Object object = map.get(name);
        if (null == object) {
            return null;
        }
        return ((ConstantEnum) object).value;
    }

    public String name() {
        return this.name;
    }

    public String value() {
        return this.value;
    }
}
