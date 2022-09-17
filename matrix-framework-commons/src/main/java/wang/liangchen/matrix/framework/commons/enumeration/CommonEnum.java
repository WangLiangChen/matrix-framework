package wang.liangchen.matrix.framework.commons.enumeration;

import wang.liangchen.matrix.framework.commons.object.ObjectUtil;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Liangchen.Wang 2022-07-08 22:51
 */
public class CommonEnum implements Serializable {
    private final static Map<String, ? super CommonEnum> map = new ConcurrentHashMap<>(16);
    private final String name;
    private final String value;

    public CommonEnum(String name, String value) {
        this.name = name;
        this.value = value;
        map.put(this.name, this);
    }

    public static <T extends CommonEnum> T valueOf(String name) {
        ValidationUtil.INSTANCE.notBlank(name);
        return ObjectUtil.INSTANCE.cast(map.get(name));
    }

    public static String value(String name) {
        ValidationUtil.INSTANCE.notBlank(name);
        Object object = map.get(name);
        if (null == object) {
            return null;
        }
        return ((CommonEnum) object).value;
    }

    public String name() {
        return this.name;
    }

    public String value() {
        return this.value;
    }
}
