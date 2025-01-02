package com.sintrue.samples.api;

import wang.liangchen.matrix.framework.data.entity.ExtendedColumnValue;
import wang.liangchen.matrix.framework.data.entity.ExtendedColumnValues;
import wang.liangchen.matrix.framework.data.json.JsonField;

public class SampleResponse {
    private String sampleId;
    private String sampleName;
    private JsonField sampleJson;
    private ExtendedColumnValues<ExtendedColumnValue> sampleExtended;

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(String sampleId) {
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
}
