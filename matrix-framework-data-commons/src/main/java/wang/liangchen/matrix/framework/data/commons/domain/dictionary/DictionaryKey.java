package wang.liangchen.matrix.framework.data.commons.domain.dictionary;

import wang.liangchen.matrix.framework.data.dao.entity.UniqueKey;

/**
 * @author Liangchen.Wang 2023-03-27 10:14
 */
public class DictionaryKey extends UniqueKey {
    private final String dictionaryGroup;
    private final String dictionaryCode;

    public DictionaryKey(String dictionaryGroup, String dictionaryCode) {
        this.dictionaryGroup = dictionaryGroup;
        this.dictionaryCode = dictionaryCode;
    }

    public static void populateKey(Dictionary entity) {
        DictionaryKey dictionaryKey = new DictionaryKey(entity.getDictionaryGroup(), entity.getDictionaryCode());
        entity.setDictionaryKey(dictionaryKey.toKeyString());
    }

    public String getDictionaryGroup() {
        return dictionaryGroup;
    }

    public String getDictionaryCode() {
        return dictionaryCode;
    }
}
