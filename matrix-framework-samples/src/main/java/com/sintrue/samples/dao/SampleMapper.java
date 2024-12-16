package com.sintrue.samples.dao;

import com.sintrue.samples.dao.entity.Sample;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SampleMapper {
    @Select("select * from sample where sample_id=#{sampleId}")
    Sample findById(Long sampleId);
}
