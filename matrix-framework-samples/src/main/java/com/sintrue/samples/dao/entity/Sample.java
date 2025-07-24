package com.sintrue.samples.dao.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import wang.liangchen.matrix.framework.spring.data.annotation.ColumnSoftDelete;
import wang.liangchen.matrix.framework.spring.data.annotation.IdStrategy;
import wang.liangchen.matrix.framework.spring.data.entity.ExtendedColumnValue;
import wang.liangchen.matrix.framework.spring.data.entity.ExtendedColumnValues;
import wang.liangchen.matrix.framework.spring.data.entity.RootEntity;
import wang.liangchen.matrix.framework.spring.boot.json.JsonField;

import java.time.LocalDateTime;

@Entity(name = "sample")
public class Sample extends RootEntity {
    @Id
    @IdStrategy(IdStrategy.Strategy.MATRIX_FLAKE)
    private Long sampleId;
    private String sampleName;
    private JsonField sampleJson;
    private ExtendedColumnValues<ExtendedColumnValue> sampleExtended;
    private LocalDateTime createDatetime;
    @ColumnSoftDelete(value = "1", type = Byte.class)
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

    public JsonField getSampleJson() {
        return sampleJson;
    }

    public void setSampleJson(JsonField sampleJson) {
        this.sampleJson = sampleJson;
    }

    public ExtendedColumnValues<ExtendedColumnValue> getSampleExtended() {
        return sampleExtended;
    }

    public void setSampleExtended(ExtendedColumnValues<ExtendedColumnValue> sampleExtended) {
        this.sampleExtended = sampleExtended;
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
