package com.sintrue.samples.test;

import com.sintrue.samples.dao.entity.Sample;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import wang.liangchen.matrix.cache.sdk.cache.MatrixCache;
import wang.liangchen.matrix.cache.sdk.cache.mlc.MultilevelMatrixCacheManager;
import wang.liangchen.matrix.framework.commons.jackson.JacksonUtil;

import java.time.Duration;

@SpringBootTest
public class MatrixCacheTest {
    @Inject
    private MultilevelMatrixCacheManager cacheManager;

    @Test
    public void testWithCacheManager() {
        Cache cache = cacheManager.getCache("SampleCache", Duration.ofHours(1));
        if (null == cache) {
            return;
        }
        cache.put("name", "Liangchen.Wang");
        cache.put("age", 25);
        Sample sample = new Sample();
        sample.setSampleId(0L);
        sample.setSampleName("Sample");
        cache.put("sample", sample);
        System.out.println(cache.get("name").get());
        System.out.println(cache.get("age").get());
        System.out.println(JacksonUtil.INSTANCE.writeValueAsString(cache.get("sample").get()));
        System.out.println(((MatrixCache) cache).keys());
    }
}
