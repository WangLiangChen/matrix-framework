package wang.liangchen.matrix.framework.data.commons.domain.inifite;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import wang.liangchen.matrix.framework.data.annotation.IdStrategy;

/**
 * @author Liangchen.Wang 2023-03-28 11:36
 */
@Entity(name = "matrix_infinite")
public class Inifite {
    @Id
    @IdStrategy(IdStrategy.Strategy.MatrixFlake)
    private Long infiniteId;
    private String infiniteKey;
    private String infiniteGroup;
    private String parentCode;
    private String infiniteName;
    private Integer infiniteLeft;
    private Integer infiniteRight;
    private String infiniteLevel;

    public Long getInfiniteId() {
        return infiniteId;
    }

    public void setInfiniteId(Long infiniteId) {
        this.infiniteId = infiniteId;
    }

    public String getInfiniteKey() {
        return infiniteKey;
    }

    public void setInfiniteKey(String infiniteKey) {
        this.infiniteKey = infiniteKey;
    }

    public String getInfiniteGroup() {
        return infiniteGroup;
    }

    public void setInfiniteGroup(String infiniteGroup) {
        this.infiniteGroup = infiniteGroup;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getInfiniteName() {
        return infiniteName;
    }

    public void setInfiniteName(String infiniteName) {
        this.infiniteName = infiniteName;
    }

    public Integer getInfiniteLeft() {
        return infiniteLeft;
    }

    public void setInfiniteLeft(Integer infiniteLeft) {
        this.infiniteLeft = infiniteLeft;
    }

    public Integer getInfiniteRight() {
        return infiniteRight;
    }

    public void setInfiniteRight(Integer infiniteRight) {
        this.infiniteRight = infiniteRight;
    }

    public String getInfiniteLevel() {
        return infiniteLevel;
    }

    public void setInfiniteLevel(String infiniteLevel) {
        this.infiniteLevel = infiniteLevel;
    }
}
