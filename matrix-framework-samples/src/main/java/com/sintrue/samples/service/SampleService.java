package com.sintrue.samples.service;


import com.sintrue.samples.api.SampleRequest;
import com.sintrue.samples.api.SampleResponse;
import com.sintrue.samples.dao.SampleMapper;
import com.sintrue.samples.dao.entity.Sample;
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
import wang.liangchen.matrix.framework.data.criteria.DeleteCriteria;
import wang.liangchen.matrix.framework.data.criteria.UpdateCriteria;
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

    public void deleteById(Long sampleId) {
        Sample sample = new Sample();
        sample.setSampleId(sampleId);
        standaloneRepository.delete(sample);
    }

    public void deleteByName(String sampleName) {
        DeleteCriteria<Sample> criteria = DeleteCriteria.of(Sample.class)._equals(Sample::getSampleName, sampleName);
        criteria.disableClearCache();
        standaloneRepository.delete(criteria);
    }

    public void updateById(Long sampleId) {
        Sample sample = new Sample();
        sample.setSampleId(sampleId);
        sample.setSampleName(String.valueOf(sampleId));
        sample.addUpdateToNullColumn(Sample::getCreateDatetime);
        standaloneRepository.update(sample);
    }

    public void updateByName(String sampleName) {
        Sample sample = new Sample();
        sample.setSampleName(sampleName);
        UpdateCriteria<Sample> criteria = UpdateCriteria.of(sample)._equals(Sample::getSampleName, sampleName).addUpdateToNullColumn(Sample::getCreateDatetime);
        standaloneRepository.update(criteria);
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
        response.setSampleName("foundName");
        return response;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

    }
}
