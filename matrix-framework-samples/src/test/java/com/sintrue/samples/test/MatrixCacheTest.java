package com.sintrue.samples.test;

import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import wang.liangchen.matrix.cache.sdk.cache.mlc.MultilevelMatrixCacheManager;

import java.time.Duration;

@SpringBootTest
public class MatrixCacheTest {
    @Inject
    private MultilevelMatrixCacheManager cacheManager;

    @Test
    public void test() {
        Cache hello = cacheManager.getCache("hello", Duration.ofMinutes(1));
        hello.put("world", "hello world");
    }
}
