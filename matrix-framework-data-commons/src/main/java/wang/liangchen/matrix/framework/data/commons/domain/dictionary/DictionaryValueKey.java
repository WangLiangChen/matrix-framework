package wang.liangchen.matrix.framework.data.commons.domain.dictionary;

import wang.liangchen.matrix.framework.data.dao.entity.UniqueKey;

/**
 * @author Liangchen.Wang 2023-03-27 10:14
 */
public class DictionaryValueKey extends UniqueKey {
    private final String dictionaryKey;
    private final String valueCode;

    public DictionaryValueKey(String dictionaryKey, String valueCode) {
        this.dictionaryKey = dictionaryKey;
        this.valueCode = valueCode;
    }

    public static DictionaryValueKey newInstance(String dictionaryKey, String valueCode) {
        return new DictionaryValueKey(dictionaryKey, valueCode);
    }

    public String getDictionaryKey() {
        return dictionaryKey;
    }

    public String getValueCode() {
        return valueCode;
    }
}
