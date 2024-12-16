package com.sintrue.samples.test.data;

import com.sintrue.samples.dao.entity.Sample;
import com.sintrue.samples.service.SampleService;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SampleTest {
    @Inject
    private SampleService sampleService;

    @Test
    public void testFindById() {
        Sample byId = sampleService.findById(0L);
        System.out.println();
    }
}
