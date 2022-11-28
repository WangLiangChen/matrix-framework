package wang.liangchen.matrix.framework.data.dao.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Version;
import wang.liangchen.matrix.framework.commons.enumeration.ConstantEnum;

import java.time.LocalDateTime;

/**
 * @author Liangchen.Wang 2022-07-08 10:58
 */
public class CommonEntity extends RootEntity {

    @Version
    @Column(name = "version")
    private Integer version;
    @Column(name = "owner")
    private String owner;
    @Column(name = "creator")
    private String creator;
    @Column(name = "create_datetime")
    private LocalDateTime createDatetime;
    @Column(name = "modifier")
    private String modifier;
    @Column(name = "modify_datetime")
    private LocalDateTime modifyDatetime;
    @Column(name = "summary")
    private String summary;
    @Column(name = "state")
    private ConstantEnum state;


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
