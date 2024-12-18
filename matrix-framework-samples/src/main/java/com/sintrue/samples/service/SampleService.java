package com.sintrue.samples.service;


import com.sintrue.samples.api.SampleResponse;
import com.sintrue.samples.dao.SampleMapper;
import com.sintrue.samples.dao.entity.Sample;
import com.sintrue.samples.dao.entity.SampleAutoIncrement;
import jakarta.inject.Inject;
import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.data.annotation.DataSourceRouter;
import wang.liangchen.matrix.framework.data.repository.StandaloneRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@DataSourceRouter("sample")
public class SampleService {
    private final SampleMapper sampleMapper;
    private final StandaloneRepository standaloneRepository;

    @Inject
    public SampleService(SampleMapper sampleMapper, StandaloneRepository standaloneRepository) {
        this.sampleMapper = sampleMapper;
        this.standaloneRepository = standaloneRepository;
    }

    public SampleResponse insertAutoIncrement() {
        SampleAutoIncrement sample = new SampleAutoIncrement();
        sample.setSampleName("name_" + System.currentTimeMillis());
        sample.setCreateDatetime(LocalDateTime.now());
        sample.setDeleted((byte) 0);
        standaloneRepository.insert(sample);
        return sample.copyPropertiesTo(SampleResponse.class);
    }

    public void insertAutoIncrementBulk() {
        List<SampleAutoIncrement> sampleList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            SampleAutoIncrement sample = new SampleAutoIncrement();
            sample.setSampleName("name_" + i);
            sample.setCreateDatetime(LocalDateTime.now());
            sample.setDeleted((byte) 0);
            sampleList.add(sample);
        }
        standaloneRepository.insert(sampleList);
        System.out.println();
    }

    public void insert() {
        Sample sample = new Sample();
        sample.setSampleName("name_" + System.currentTimeMillis());
        sample.setCreateDatetime(LocalDateTime.now());
        sample.setDeleted((byte) 0);
        standaloneRepository.insert(sample);
    }

    public void insertBulk() {
        List<Sample> sampleList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Sample sample = new Sample();
            sample.setSampleName("name_" + i);
            sample.setCreateDatetime(LocalDateTime.now());
            sample.setDeleted((byte) 0);
            sampleList.add(sample);
        }
        standaloneRepository.insert(sampleList);
    }
}
