package wang.liangchen.matrix.framework.data.dao.entity;

import wang.liangchen.matrix.framework.commons.enumeration.Symbol;

import javax.persistence.Column;
import javax.persistence.Version;
import java.time.LocalDateTime;

/**
 * @author Liangchen.Wang 2022-07-08 10:58
 */
public class CommonEntity extends RootEntity {

    @Version
    @Column(name = "version")
    private Integer version = 0;
    @Column(name = "owner")
    private String owner = Symbol.BLANK.getSymbol();
    @Column(name = "creator")
    private String creator = Symbol.BLANK.getSymbol();
    @Column(name = "create_datetime")
    private LocalDateTime createDatetime = LocalDateTime.now();
    @Column(name = "modifier")
    private String modifier = Symbol.BLANK.getSymbol();
    @Column(name = "modify_datetime")
    private LocalDateTime modifyDatetime = LocalDateTime.now();
    @Column(name = "summary")
    private String summary = Symbol.BLANK.getSymbol();
    ;
    @Column(name = "state")
    private String state = Symbol.BLANK.getSymbol();
    ;


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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
