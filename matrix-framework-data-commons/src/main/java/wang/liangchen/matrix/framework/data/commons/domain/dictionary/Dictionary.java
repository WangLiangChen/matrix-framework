package wang.liangchen.matrix.framework.data.commons.domain.dictionary;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import wang.liangchen.matrix.framework.commons.string.StringUtil;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.data.annotation.ColumnJson;
import wang.liangchen.matrix.framework.data.annotation.IdStrategy;
import wang.liangchen.matrix.framework.data.dao.entity.CommonEntity;
import wang.liangchen.matrix.framework.data.enumeration.DataType;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Liangchen.Wang 2023-03-26 10:31
 */
@Entity(name = "matrix_dictionary")
public class Dictionary extends CommonEntity {
    @Id
    @IdStrategy(IdStrategy.Strategy.MatrixFlake)
    private Long dictionaryId;
    private String dictionaryGroup;
    private DictionaryType dictionaryType;
    private String dictionaryCode;
    private String dictionaryName;
    private DataType dataType;
    private String dictionaryDesc = StringUtil.INSTANCE.blankString();
    @ColumnJson
    private List<DictionaryValue> dictionaryValues = new ArrayList<>();

    public static Dictionary newInstance() {
        return ClassUtil.INSTANCE.instantiate(Dictionary.class);
    }

    public Long getDictionaryId() {
        return dictionaryId;
    }

    public void setDictionaryId(Long dictionaryId) {
        this.dictionaryId = dictionaryId;
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

    public String getDictionaryDesc() {
        return dictionaryDesc;
    }

    public void setDictionaryDesc(String dictionaryDesc) {
        this.dictionaryDesc = dictionaryDesc;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public List<DictionaryValue> getDictionaryValues() {
        return dictionaryValues;
    }

    public void setDictionaryValues(List<DictionaryValue> dictionaryValues) {
        this.dictionaryValues = dictionaryValues;
    }

    public void addDictionaryValue(DictionaryValue dictionaryValue) {
        this.dictionaryValues.add(dictionaryValue);
    }
}
