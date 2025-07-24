package com.sintrue.samples.controller;

import com.sintrue.samples.api.SampleResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import wang.liangchen.matrix.framework.commons.runtime.ReturnWrapper;
import wang.liangchen.matrix.framework.spring.web.annotation.ReturnRawText;
import wang.liangchen.matrix.framework.spring.web.response.JsonResponse;

@RestController
@RequestMapping("/webflux")
public class WebFluxController {

    @GetMapping("/returnNull")
    public Mono<SampleResponse> returnNull() {
        return Mono.empty();
    }

    @GetMapping("/returnString")
    public Mono<String> returnString() {
        return Mono.just("Welcome to Matrix!");
    }

    @GetMapping("/returnRawString")
    @ReturnRawText
    public Mono<String> returnRawString() {
        return Mono.just("Welcome to Matrix!");
    }

    @GetMapping("/returnNativeObject")
    public Mono<SampleResponse> returnNativeObject() {
        SampleResponse response = new SampleResponse();
        response.setSampleId("0");
        response.setSampleName("name_0");
        return Mono.just(response);
    }

    @GetMapping("/returnReturnWrapper")
    public Mono<ReturnWrapper<SampleResponse>> returnReturnWrapper() {
        SampleResponse response = new SampleResponse();
        response.setSampleId("0");
        response.setSampleName("name_0");
        return Mono.just(ReturnWrapper.success(response));
    }

    @GetMapping("/returnJsonResponse")
    public Mono<JsonResponse<SampleResponse>> returnJsonResponse() {
        SampleResponse response = new SampleResponse();
        response.setSampleId("0");
        response.setSampleName("name_0");
        return Mono.just(JsonResponse.success(response));
    }

}
