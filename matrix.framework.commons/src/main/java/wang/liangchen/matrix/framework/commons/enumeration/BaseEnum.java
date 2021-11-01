package wang.liangchen.matrix.framework.commons.enumeration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LiangChen.Wang
 */
public class BaseEnum {
    private final static Map<String, String> map = new HashMap<>();
    private final String name;
    private final String text;

    public BaseEnum(String name, String text) {
        this.name = name;
        this.text = text;
        map.put(this.name, this.text);
    }

    public static String textByName(String name) {
        return map.get(name);
    }

    public String name() {
        return name;
    }

    public String text() {
        return text;
    }
}
