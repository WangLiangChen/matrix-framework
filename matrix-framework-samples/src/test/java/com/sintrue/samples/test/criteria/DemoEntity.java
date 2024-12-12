package com.sintrue.samples.test.criteria;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import wang.liangchen.matrix.framework.data.annotation.IdStrategy;
import wang.liangchen.matrix.framework.data.entity.RootEntity;

/**
 * @author LiangChen.Wang 2024/11/16 11:44
 */
@Entity(name = "matrix_demo")
public class DemoEntity extends RootEntity {
    @Id
    @IdStrategy(IdStrategy.Strategy.MATRIX_FLAKE)
    private Long demoId;
    private String demoName;

    public Long getDemoId() {
        return demoId;
    }

    public void setDemoId(Long demoId) {
        this.demoId = demoId;
    }

    public String getDemoName() {
        return demoName;
    }

    public void setDemoName(String demoName) {
        this.demoName = demoName;
    }
}
