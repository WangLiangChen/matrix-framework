package com.sintrue.samples.service;

import jakarta.inject.Inject;
import org.springframework.stereotype.Service;
import wang.liangchen.matrix.framework.data.annotation.DataSourceRouter;
import wang.liangchen.matrix.framework.data.repository.StandaloneRepository;

@Service
@DataSourceRouter("test")
public class TestService {
    private final StandaloneRepository standaloneRepository;

    @Inject
    public TestService(StandaloneRepository standaloneRepository) {
        this.standaloneRepository = standaloneRepository;
    }
}
