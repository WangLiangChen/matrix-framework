package wang.liangchen.matrix.framework.data.commons.domain.dictionary;

import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.data.annotation.IdStrategy;
import wang.liangchen.matrix.framework.data.dao.entity.CommonEntity;
import wang.liangchen.matrix.framework.data.enumeration.DataType;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author Liangchen.Wang 2023-03-26 10:31
 */
@Entity(name = "matrix_dictionary")
public class Dictionary extends CommonEntity {
    @Id
    @IdStrategy(IdStrategy.Strategy.MatrixFlake)
    private Long dictionaryId;
    private String dictionaryKey;
    private String dictionaryGroup;
    private String dictionaryCode;
    private String dictionaryName;
    private DictionaryType dictionaryType;
    private DataType dataType;

    public static Dictionary newInstance() {
        return ClassUtil.INSTANCE.instantiate(Dictionary.class);
    }

    public Long getDictionaryId() {
        return dictionaryId;
    }

    public void setDictionaryId(Long dictionaryId) {
        this.dictionaryId = dictionaryId;
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

    public DictionaryType getDictionaryType() {
        return dictionaryType;
    }

    public void setDictionaryType(DictionaryType dictionaryType) {
        this.dictionaryType = dictionaryType;
    }

    public String getDictionaryCode() {
        return dictionaryCode;
    }

    public void setDictionaryCode(String dictionaryCode) {
        this.dictionaryCode = dictionaryCode;
    }

    public String getDictionaryName() {
        return dictionaryName;
    }

    public void setDictionaryName(String dictionaryName) {
        this.dictionaryName = dictionaryName;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

}
