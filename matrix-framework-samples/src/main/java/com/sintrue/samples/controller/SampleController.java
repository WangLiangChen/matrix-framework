package com.sintrue.samples.controller;

import com.sintrue.samples.api.SampleResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import wang.liangchen.matrix.framework.web.response.JsonResponse;

@RestController
@RequestMapping("/sample")
public class SampleController {

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
        response.setSampleId(0L);
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
        response.setSampleId(0L);
        response.setSampleName("name_0");
        return Mono.just(response);
    }

    @GetMapping("/monoJsonResponse")
    public Mono<JsonResponse<SampleResponse>> monoJsonResponse() {
        SampleResponse response = new SampleResponse();
        response.setSampleId(0L);
        response.setSampleName("name_0");
        return Mono.just(JsonResponse.success(response));
    }

    @GetMapping("/fluxNativeObject")
    public Flux<SampleResponse> fluxNativeObject() {
        SampleResponse response_0 = new SampleResponse();
        response_0.setSampleId(0L);
        response_0.setSampleName("name_0");
        SampleResponse response_1 = new SampleResponse();
        response_1.setSampleId(1L);
        response_1.setSampleName("name_1");

        return Flux.just(response_0, response_1);
    }


}
