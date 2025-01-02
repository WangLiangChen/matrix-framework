package com.sintrue.samples.test.data;

import com.sintrue.samples.api.SampleRequest;
import com.sintrue.samples.api.SampleResponse;
import com.sintrue.samples.service.SampleService;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import wang.liangchen.matrix.framework.commons.jackson.JacksonUtil;
import wang.liangchen.matrix.framework.commons.uid.NanoIdUtil;
import wang.liangchen.matrix.framework.data.entity.ExtendedColumnValue;
import wang.liangchen.matrix.framework.data.entity.ExtendedColumnValues;
import wang.liangchen.matrix.framework.data.json.JsonField;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class SampleTest {
    @Inject
    private SampleService sampleService;

    @Test
    public void testInsert() {
        SampleRequest request = new SampleRequest();
        request.setSampleName("name_" + NanoIdUtil.INSTANCE.randomNanoId());
        JsonField sampleJson = new JsonField();
        sampleJson.put("name", "Liangchen.Wang");
        sampleJson.put("age", 18);
        sampleJson.put("datetime", LocalDateTime.now());
        request.setSampleJson(sampleJson);

        ExtendedColumnValues<ExtendedColumnValue> sampleExtended = new ExtendedColumnValues<>();
        ExtendedColumnValue extendedColumnValue = new ExtendedColumnValue();
        extendedColumnValue.setColumnName("extended_name");
        extendedColumnValue.setColumnValue("Fengyuan.Wang");
        sampleExtended.add(extendedColumnValue);
        request.setSampleExtended(sampleExtended);

        SampleResponse response = sampleService.insert(request);
        System.out.println(JacksonUtil.INSTANCE.writeValueAsString(response));
    }

    @Test
    public void testInsertBulk() {
        List<SampleRequest> requestList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            SampleRequest request = new SampleRequest();
            request.setSampleName("name_" + NanoIdUtil.INSTANCE.randomNanoId());
            JsonField sampleJson = new JsonField();
            sampleJson.put("name", "Liangchen.Wang");
            sampleJson.put("age", 18);
            sampleJson.put("datetime", LocalDateTime.now());
            request.setSampleJson(sampleJson);

            ExtendedColumnValues<ExtendedColumnValue> sampleExtended = new ExtendedColumnValues<>();
            ExtendedColumnValue extendedColumnValue = new ExtendedColumnValue();
            extendedColumnValue.setColumnName("extended_name");
            extendedColumnValue.setColumnValue("Fengyuan.Wang");
            sampleExtended.add(extendedColumnValue);
            request.setSampleExtended(sampleExtended);
            requestList.add(request);
        }
        List<SampleResponse> sampleResponses = sampleService.insertBulk(requestList);
        System.out.println(JacksonUtil.INSTANCE.writeValueAsString(sampleResponses));
    }

    @Test
    public void testDeleteById() {
        sampleService.deleteById(1L);
    }

    @Test
    public void testDeleteByName() {
        sampleService.deleteByName("hello");
    }

    @Test
    public void testUpdateById() {
        sampleService.updateById(1L);
    }

    @Test
    public void testUpdateByName() {
        sampleService.updateByName("hello");
    }

    @Test
    public void testFindById() {
        SampleResponse response = sampleService.findById(561953428834633026L);
        System.out.println(JacksonUtil.INSTANCE.writeValueAsString(response));
    }
}
