package wang.liangchen.matrix.framework.commons.enumeration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Liangchen.Wang 2022-07-08 22:51
 */
public class CommonEnum {
    private final static Map<String, String> map = new HashMap<>();
    private final String name;
    private final String value;

    public CommonEnum(String name, String value) {
        this.name = name;
        this.value = value;
        map.put(this.name, this.value);
    }

    public static String value(String key) {
        return map.get(key);
    }

    public String name() {
        return this.name;
    }

    public String value() {
        return this.value;
    }
}
