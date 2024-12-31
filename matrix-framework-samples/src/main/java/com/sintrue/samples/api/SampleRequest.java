package com.sintrue.samples.api;

import wang.liangchen.matrix.framework.commons.object.EnhancedObject;

import java.util.Objects;

public class SampleRequest extends EnhancedObject {
    private String sampleName;

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    // for caffeine cache
    @Override
    public int hashCode() {
        return Objects.hashCode(this.sampleName);
    }

    // for caffeine cache
    @Override
    public boolean equals(Object object) {
        return (this == object || (object instanceof SampleRequest thatRequest && Objects.equals(this.sampleName, thatRequest.sampleName)));
    }

    // for redis cache
    @Override
    public String toString() {
        return "SampleRequest{" +
                "sampleName='" + sampleName + '\'' +
                "}";
    }
}
