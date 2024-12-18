package com.sintrue.samples.controller;

import com.sintrue.samples.api.SampleResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import wang.liangchen.matrix.framework.commons.runtime.ReturnWrapper;
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
