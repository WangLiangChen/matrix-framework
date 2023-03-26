package wang.liangchen.matrix.framework.data.commons.domain.tagging;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import wang.liangchen.matrix.framework.data.annotation.IdStrategy;
import wang.liangchen.matrix.framework.data.dao.entity.CommonEntity;

/**
 * @author Liangchen.Wang 2023-03-26 10:33
 */
@Entity(name = "matrix_tagging")
public class Tagging extends CommonEntity {
    @Id
    @IdStrategy(IdStrategy.Strategy.MatrixFlake)
    private Long taggingId;
    private String taggingGroup;
    private String tableName;
    private String dataId;
    private String taggingCode;
    private String taggingValue;

    public Long getTaggingId() {
        return taggingId;
    }

    public void setTaggingId(Long taggingId) {
        this.taggingId = taggingId;
    }

    public String getTaggingGroup() {
        return taggingGroup;
    }

    public void setTaggingGroup(String taggingGroup) {
        this.taggingGroup = taggingGroup;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getTaggingCode() {
        return taggingCode;
    }

    public void setTaggingCode(String taggingCode) {
        this.taggingCode = taggingCode;
    }

    public String getTaggingValue() {
        return taggingValue;
    }

    public void setTaggingValue(String taggingValue) {
        this.taggingValue = taggingValue;
    }
}
