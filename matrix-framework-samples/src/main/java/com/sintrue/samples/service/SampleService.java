package com.sintrue.samples.service;


import com.sintrue.samples.dao.SampleMapper;
import com.sintrue.samples.dao.entity.Sample;
import jakarta.inject.Inject;
import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.data.annotation.DataSourceRouter;

@Service
public class SampleService {
    private final SampleMapper sampleMapper;

    @Inject
    public SampleService(SampleMapper sampleMapper) {
        this.sampleMapper = sampleMapper;
    }

    @DataSourceRouter("sample")
    public Sample findById(Long sampleId) {
        return sampleMapper.findById(sampleId);
    }
}
