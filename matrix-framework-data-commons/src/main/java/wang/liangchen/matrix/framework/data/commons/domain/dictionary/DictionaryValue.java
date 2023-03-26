package wang.liangchen.matrix.framework.data.commons.domain.dictionary;

import wang.liangchen.matrix.framework.commons.type.ClassUtil;

/**
 * @author Liangchen.Wang 2023-03-26 10:31
 */
public class DictionaryValue {
    private String value;
    private String label;
    private String desc;

    public static DictionaryValue newInstance() {
        return ClassUtil.INSTANCE.instantiate(DictionaryValue.class);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
