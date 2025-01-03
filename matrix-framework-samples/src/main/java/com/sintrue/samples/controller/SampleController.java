package com.sintrue.samples.controller;

import com.sintrue.samples.api.SampleRequest;
import com.sintrue.samples.api.SampleResponse;
import com.sintrue.samples.service.SampleService;
import jakarta.inject.Inject;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import wang.liangchen.matrix.framework.commons.runtime.ReturnWrapper;
import wang.liangchen.matrix.framework.web.response.JsonResponse;

@RestController
@RequestMapping("/sample")
public class SampleController {
    private final SampleService sampleService;

    @Inject
    public SampleController(SampleService sampleService) {
        this.sampleService = sampleService;
    }

    /**
     * <pre>{@code
     * {
     *     "sampleName": "Sample",
     *     "sampleJson": {
     *         "json_name": "Liangchen.Wang",
     *         "json_age": 18,
     *         "json_datetime": "2000-05-01 12:20:30"
     *     },
     *     "sampleExtended": [
     *         {
     *             "columnName": "extended_name",
     *             "columnValue": "Fengyuan.Wang"
     *         }
     *     ]
     * }
     * }</pre>
     * @param request
     * @return
     */

    @PostMapping("/create")
    public SampleResponse create(@RequestBody SampleRequest request) {
        return sampleService.insert(request);
    }

    @GetMapping("/void")
    public void returnVoid() {
    }

    @GetMapping("/null")
    public SampleResponse returnNull() {
        return null;
    }

    @GetMapping("/nativeObject")
    public SampleResponse nativeObject() {
        SampleResponse response = new SampleResponse();
        response.setSampleId("0");
        response.setSampleName("name_0");
        return response;
    }

    @GetMapping("/monoNull")
    public Mono<SampleResponse> monoNull() {
        SampleResponse response = null;
        return Mono.justOrEmpty(response);
    }

    @GetMapping("/monoNativeObject")
    public Mono<SampleResponse> monoNativeObject() {
        SampleResponse response = new SampleResponse();
        response.setSampleId("0");
        response.setSampleName("name_0");
        return Mono.just(response);
    }

    @GetMapping("/monoReturnWrapper")
    public Mono<ReturnWrapper<SampleResponse>> monoReturnWrapper() {
        SampleResponse response = new SampleResponse();
        response.setSampleId("0");
        response.setSampleName("name_0");
        return Mono.just(ReturnWrapper.success(response));
    }

    @GetMapping("/monoJsonResponse")
    public Mono<JsonResponse<SampleResponse>> monoJsonResponse() {
        SampleResponse response = new SampleResponse();
        response.setSampleId("0");
        response.setSampleName("name_0");
        return Mono.just(JsonResponse.success(response));
    }

    @GetMapping("/string")
    public String string() {
        return "Welcome to Matrix!";
    }

    @GetMapping("/integer")
    public Integer integer() {
        return Integer.MAX_VALUE;
    }

}
