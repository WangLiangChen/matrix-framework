package com.sintrue.samples.test;

import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import wang.liangchen.matrix.cache.sdk.cache.mlc.MultiLevelMatrixCacheManager;

import java.time.Duration;

@SpringBootTest
public class MatrixCacheTest {
    @Inject
    private MultiLevelMatrixCacheManager cacheManager;

    @Test
    public void testWithCacheManager() {
        Cache cache = cacheManager.getCache("SampleCache", Duration.ofHours(1));
        if (null == cache) {
            return;
        }
        cache.put("name", "Liangchen.Wang");
        cache.put("age", 25);
        cache.evict("name");
        cache.clear();
        System.out.println();
    }
}
