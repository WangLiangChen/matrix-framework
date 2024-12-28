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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@SpringBootTest
public class SampleTest {
    @Inject
    private SampleService sampleService;

    @Test
    public void testInsertAutoIncrement() {
        SampleRequest request = SampleRequest.newInstance(SampleRequest.class);
        request.setSampleName("name_" + NanoIdUtil.INSTANCE.randomNanoId());
        SampleResponse response = sampleService.insertAutoIncrement(request);
        System.out.println(JacksonUtil.INSTANCE.writeValueAsString(response));
    }

    @Test
    public void testInsertAutoIncrementBulk() {
        List<SampleRequest> requestList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            SampleRequest request = SampleRequest.newInstance(SampleRequest.class);
            request.setSampleName("name_" + NanoIdUtil.INSTANCE.randomNanoId());
            requestList.add(request);
        }
        List<SampleResponse> sampleResponses = sampleService.insertAutoIncrementBulk(requestList);
        System.out.println(JacksonUtil.INSTANCE.writeValueAsString(sampleResponses));
    }

    @Test
    public void testInsert() {
        SampleRequest request = SampleRequest.newInstance(SampleRequest.class);
        request.setSampleName("name_" + NanoIdUtil.INSTANCE.randomNanoId());
        SampleResponse response = sampleService.insert(request);
        System.out.println(JacksonUtil.INSTANCE.writeValueAsString(response));
    }

    @Test
    public void testInsertBulk() {
        List<SampleRequest> requestList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            SampleRequest request = SampleRequest.newInstance(SampleRequest.class);
            request.setSampleName("name_" + NanoIdUtil.INSTANCE.randomNanoId());
            requestList.add(request);
        }
        List<SampleResponse> sampleResponses = sampleService.insertBulk(requestList);
        System.out.println(JacksonUtil.INSTANCE.writeValueAsString(sampleResponses));
    }

    @Test
    public void findById() {
        for (int i = 0; i < 5; i++) {
            System.out.println(JacksonUtil.INSTANCE.writeValueAsString(sampleService.findById(559795816177401963L)));
            ThreadUtil.INSTANCE.sleep(TimeUnit.SECONDS, 1);
        }

    }

    @Test
    public void find() {
        SampleRequest request = new SampleRequest();
        request.setSampleName("findRequest");
        sampleService.find(request);
    }
}
