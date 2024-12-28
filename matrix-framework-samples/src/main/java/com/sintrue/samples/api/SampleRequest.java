package com.sintrue.samples.api;

import wang.liangchen.matrix.framework.commons.object.EnhancedObject;

public class SampleRequest extends EnhancedObject {
    private String sampleName;

    public String getSampleName() {
        return sampleName;
    }

    public void setSampleName(String sampleName) {
        this.sampleName = sampleName;
    }

    @Override
    public String toString() {
        return "SampleRequest{" +
                "sampleName='" + sampleName + '\'' +
                "}";
    }
}
