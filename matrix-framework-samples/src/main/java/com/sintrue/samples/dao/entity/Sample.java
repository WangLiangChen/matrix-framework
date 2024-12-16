package com.sintrue.samples.dao.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import wang.liangchen.matrix.framework.data.annotation.IdStrategy;

@Entity(name = "sample")
public class Sample {
    @Id
    @IdStrategy(IdStrategy.Strategy.MATRIX_FLAKE)
    private Long sampleId;
    private String sampleName;

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
}
