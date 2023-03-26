package wang.liangchen.matrix.framework.data.dao.entity;

import jakarta.persistence.Version;
import wang.liangchen.matrix.framework.commons.enumeration.ConstantEnum;
import wang.liangchen.matrix.framework.commons.string.StringUtil;

import java.time.LocalDateTime;

/**
 * @author Liangchen.Wang 2022-07-08 10:58
 */
public class CommonEntity extends RootEntity {

    @Version
    private Integer version = 0;

    private String owner = StringUtil.INSTANCE.blankString();

    private String creator = StringUtil.INSTANCE.blankString();

    private LocalDateTime createDatetime = LocalDateTime.now();

    private String modifier = StringUtil.INSTANCE.blankString();

    private LocalDateTime modifyDatetime = LocalDateTime.now();

    private String summary = StringUtil.INSTANCE.blankString();
    private ConstantEnum state = ConstantEnum.DEFAULT;

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }


    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public LocalDateTime getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(LocalDateTime createDatetime) {
        this.createDatetime = createDatetime;
    }

    public String getModifier() {
        return modifier;
    }

    public void setModifier(String modifier) {
        this.modifier = modifier;
    }

    public LocalDateTime getModifyDatetime() {
        return modifyDatetime;
    }

    public void setModifyDatetime(LocalDateTime modifyDatetime) {
        this.modifyDatetime = modifyDatetime;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public ConstantEnum getState() {
        return state;
    }

    public void setState(ConstantEnum state) {
        this.state = state;
    }
}
