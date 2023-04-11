package wang.liangchen.matrix.framework.data.commons.domain.dictionary;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.data.annotation.IdStrategy;
import wang.liangchen.matrix.framework.data.dao.entity.CommonEntity;

/**
 * @author Liangchen.Wang 2023-03-26 10:31
 */
@Entity(name = "matrix_dictionary_value")
public class DictionaryValue extends CommonEntity {
    @Id
    @IdStrategy(IdStrategy.Strategy.MatrixFlake)
    private Long valueId;
    private String valueKey;
    private String dictionaryKey;
    private String dictionaryGroup;
    private String dictionaryCode;
    private String valueCode;
    private String valueLabel;

    public DictionaryValue newInstance() {
        return ClassUtil.INSTANCE.instantiate(DictionaryValue.class);
    }

    public Long getValueId() {
        return valueId;
    }

    public void setValueId(Long valueId) {
        this.valueId = valueId;
    }

    public String getValueKey() {
        return valueKey;
    }

    public void setValueKey(String valueKey) {
        this.valueKey = valueKey;
    }

    public String getDictionaryKey() {
        return dictionaryKey;
    }

    public void setDictionaryKey(String dictionaryKey) {
        this.dictionaryKey = dictionaryKey;
    }

    public String getDictionaryGroup() {
        return dictionaryGroup;
    }

    public void setDictionaryGroup(String dictionaryGroup) {
        this.dictionaryGroup = dictionaryGroup;
    }

    public String getDictionaryCode() {
        return dictionaryCode;
    }

    public void setDictionaryCode(String dictionaryCode) {
        this.dictionaryCode = dictionaryCode;
    }

    public String getValueCode() {
        return valueCode;
    }

    public void setValueCode(String valueCode) {
        this.valueCode = valueCode;
    }

    public String getValueLabel() {
        return valueLabel;
    }

    public void setValueLabel(String valueLabel) {
        this.valueLabel = valueLabel;
    }
}
