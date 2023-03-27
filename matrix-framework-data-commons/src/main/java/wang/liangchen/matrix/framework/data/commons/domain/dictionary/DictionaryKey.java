package wang.liangchen.matrix.framework.data.commons.domain.dictionary;

/**
 * @author Liangchen.Wang 2023-03-27 10:14
 */
public class DictionaryKey {
    private final String dictionaryGroup;
    private final String dictionaryCode;

    public DictionaryKey(String dictionaryGroup, String dictionaryCode) {
        this.dictionaryGroup = dictionaryGroup;
        this.dictionaryCode = dictionaryCode;
    }

    public static DictionaryKey newInstance(String dictionaryGroup, String dictionaryCode) {
        return new DictionaryKey(dictionaryGroup, dictionaryCode);
    }

    public String getDictionaryGroup() {
        return dictionaryGroup;
    }

    public String getDictionaryCode() {
        return dictionaryCode;
    }
}
