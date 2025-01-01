package com.sintrue.samples.dao.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import wang.liangchen.matrix.framework.data.annotation.ColumnSoftDelete;
import wang.liangchen.matrix.framework.data.annotation.IdStrategy;
import wang.liangchen.matrix.framework.data.entity.RootEntity;
import wang.liangchen.matrix.framework.data.json.JsonField;

import java.time.LocalDateTime;

@Entity(name = "sample")
public class Sample extends RootEntity {
    @Id
    @IdStrategy(IdStrategy.Strategy.MATRIX_FLAKE)
    private Long sampleId;
    private String sampleName;
    private Integer sampleNumber;
    private JsonField sampleJson;
    private LocalDateTime createDatetime;
    @ColumnSoftDelete(value = "1", type = Byte.class)
    private Byte deleted;


}
