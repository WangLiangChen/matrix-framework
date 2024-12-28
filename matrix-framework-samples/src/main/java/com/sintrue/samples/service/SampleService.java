package com.sintrue.samples.service;


import com.sintrue.samples.api.SampleRequest;
import com.sintrue.samples.api.SampleResponse;
import com.sintrue.samples.dao.SampleMapper;
import com.sintrue.samples.dao.entity.Sample;
import com.sintrue.samples.dao.entity.SampleAutoIncrement;
import jakarta.inject.Inject;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import wang.liangchen.matrix.cache.sdk.annotation.CacheExpire;
import wang.liangchen.matrix.framework.commons.object.JavaBeanUtil;
import wang.liangchen.matrix.framework.data.annotation.DataSourceRouter;
import wang.liangchen.matrix.framework.data.criteria.Criteria;
import wang.liangchen.matrix.framework.data.repository.StandaloneRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@DataSourceRouter("sample")
public class SampleService implements BeanFactoryAware {
    private final SampleMapper sampleMapper;
    private final StandaloneRepository standaloneRepository;

    @Inject
    public SampleService(SampleMapper sampleMapper, StandaloneRepository standaloneRepository) {
        this.sampleMapper = sampleMapper;
        this.standaloneRepository = standaloneRepository;
    }

    public SampleResponse insertAutoIncrement(SampleRequest request) {
        // copy properties to entity
        SampleAutoIncrement entity = request.copyPropertiesTo(SampleAutoIncrement.class);
        entity.setCreateDatetime(LocalDateTime.now());
        entity.setDeleted((byte) 0);
        standaloneRepository.insert(entity);
        // copy properties to MessageContract
        return entity.copyPropertiesTo(SampleResponse.class);
    }

    public List<SampleResponse> insertAutoIncrementBulk(List<SampleRequest> requests) {
        // copy properties to entities, and populate entity
        List<SampleAutoIncrement> entities = JavaBeanUtil.INSTANCE.copyProperties(requests, SampleAutoIncrement.class, (o, entity) -> {
            entity.setCreateDatetime(LocalDateTime.now());
            entity.setDeleted((byte) 0);
        });
        standaloneRepository.insert(entities);
        // copy properties to MessageContract
        return JavaBeanUtil.INSTANCE.copyProperties(entities, SampleResponse.class);
    }

    public SampleResponse insert(SampleRequest request) {
        // copy properties to entity
        Sample entity = request.copyPropertiesTo(Sample.class);
        entity.setCreateDatetime(LocalDateTime.now());
        entity.setDeleted((byte) 0);
        standaloneRepository.insert(entity);
        // copy properties to MessageContract
        return entity.copyPropertiesTo(SampleResponse.class);
    }

    public List<SampleResponse> insertBulk(List<SampleRequest> requests) {
        // copy properties to entities, and populate entity
        List<Sample> entities = JavaBeanUtil.INSTANCE.copyProperties(requests, Sample.class, (o, entity) -> {
            entity.setCreateDatetime(LocalDateTime.now());
            entity.setDeleted((byte) 0);
        });
        standaloneRepository.insert(entities);
        // copy properties to MessageContract
        return JavaBeanUtil.INSTANCE.copyProperties(entities, SampleResponse.class);
    }

    public void delete(Long sampleId) {
        Sample sample = Sample.newInstance(Sample.class);
        sample.setSampleId(sampleId);
        standaloneRepository.delete(sample);
    }

    public SampleResponse findById(long sampleId) {
        Criteria<Sample> criteria = Criteria.of(Sample.class)._equals(Sample::getSampleId, sampleId);
        Sample sample = standaloneRepository.select(criteria);
        return sample.copyPropertiesTo(SampleResponse.class);
    }

    @Cacheable("Sample")
    @CacheExpire(ttl = 10, timeUnit = TimeUnit.MINUTES)
    public SampleResponse find(SampleRequest request) {
        SampleResponse response = new SampleResponse();
        response.setSampleName("findName");
        return response;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

    }
}
