package com.sintrue.samples.test.data;

import com.sintrue.samples.api.SampleResponse;
import com.sintrue.samples.service.SampleService;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SampleTest {
    @Inject
    private SampleService sampleService;

    @Test
    public void testInsertAutoIncrement() {
        SampleResponse response = sampleService.insertAutoIncrement();
        System.out.println();
    }
    @Test
    public void testInsertAutoIncrementBulk() {
        sampleService.insertAutoIncrementBulk();
    }

    @Test
    public void testInsert() {
        sampleService.insert();
    }

    @Test
    public void testInsertBulk() {
        sampleService.insertBulk();
    }
}
