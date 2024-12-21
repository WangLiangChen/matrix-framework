package com.sintrue.samples;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import wang.liangchen.matrix.cache.sdk.configuration.EnableMatrixCaching;

@SpringBootApplication
@EnableMatrixCaching
public class SamplesApplication {
    public static void main(String[] args) {
        SpringApplication.run(SamplesApplication.class, args);
    }
}
