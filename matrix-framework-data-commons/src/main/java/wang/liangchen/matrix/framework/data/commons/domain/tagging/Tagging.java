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
    private String taggingKey;
    private String taggingGroup;
    private String businessType;
    private String businessId;
    private String taggingCode;
    private String taggingValue;

    public Long getTaggingId() {
        return taggingId;
    }

    public void setTaggingId(Long taggingId) {
        this.taggingId = taggingId;
    }

    public String getTaggingKey() {
        return taggingKey;
    }

    public void setTaggingKey(String taggingKey) {
        this.taggingKey = taggingKey;
    }

    public String getTaggingGroup() {
        return taggingGroup;
    }

    public void setTaggingGroup(String taggingGroup) {
        this.taggingGroup = taggingGroup;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getBusinessId() {
        return businessId;
    }

    public void setBusinessId(String businessId) {
        this.businessId = businessId;
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
