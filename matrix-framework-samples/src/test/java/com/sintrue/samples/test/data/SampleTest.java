package com.sintrue.samples.test.data;

import com.sintrue.samples.api.SampleRequest;
import com.sintrue.samples.api.SampleResponse;
import com.sintrue.samples.service.SampleService;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import wang.liangchen.matrix.framework.commons.jackson.JacksonUtil;
import wang.liangchen.matrix.framework.commons.thread.ThreadUtil;
import wang.liangchen.matrix.framework.commons.uid.NanoIdUtil;
import wang.liangchen.matrix.framework.data.entity.ExtendedColumnValue;
import wang.liangchen.matrix.framework.data.entity.ExtendedColumnValues;
import wang.liangchen.matrix.framework.springboot.json.JsonField;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class SampleTest {
    @Inject
    private SampleService sampleService;

    @Test
    public void testCreate() {
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

        SampleResponse response = sampleService.create(request);
        System.out.println(JacksonUtil.INSTANCE.writeValueAsString(response));
    }

    @Test
    public void testCreateBulk() {
        List<Long> list = new ArrayList<>();
        for (int i = 1; i < 2; i++) {
            list.add(testCreateBulk(i));
        }
        System.out.println(list);
    }

    public long testCreateBulk(int count) {
        sampleService.deleteAll();
        long ms = System.currentTimeMillis();
        List<SampleRequest> requestList = new ArrayList<>();
        for (int i = 0; i < count * 1000; i++) {
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
        List<SampleResponse> sampleResponses = sampleService.createBulk(requestList);
        //System.out.println(JacksonUtil.INSTANCE.writeValueAsString(sampleResponses));
        return System.currentTimeMillis() - ms;
    }

    @Test
    public void deleteAll() {
        sampleService.deleteAll();
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

    @Test
    public void testAsync() {
        sampleService.doAsync();
        ThreadUtil.INSTANCE.sleep(Duration.ofSeconds(10));
    }
}
