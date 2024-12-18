package com.sintrue.samples.dao.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import wang.liangchen.matrix.framework.data.annotation.IdStrategy;
import wang.liangchen.matrix.framework.data.entity.RootEntity;

import java.time.LocalDateTime;

@Entity(name = "sample_auto_increment")
public class SampleAutoIncrement extends RootEntity {
    @Id
    @IdStrategy(IdStrategy.Strategy.AUTO_INCREMENT)
    private Long sampleId;
    private String sampleName;
    private LocalDateTime createDatetime;
    private Byte deleted;

    public Long getSampleId() {
        return sampleId;
    }

    public void setSampleId(Long sampleId) {
        this.sampleId = sampleId;
    }

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    public LocalDateTime getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(LocalDateTime createDatetime) {
        this.createDatetime = createDatetime;
    }

    public Byte getDeleted() {
        return deleted;
    }

    public void setDeleted(Byte deleted) {
        this.deleted = deleted;
    }
}
