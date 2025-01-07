package com.sintrue.samples.controller;

import com.sintrue.samples.service.SampleService;
import jakarta.inject.Inject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sample")
public class SampleController {
    private final SampleService sampleService;

    @Inject
    public SampleController(SampleService sampleService) {
        this.sampleService = sampleService;
    }

}
