package com.sintrue.samples.controller;

import com.sintrue.samples.api.SampleResponse;
import org.springframework.web.bind.annotation.*;
import wang.liangchen.matrix.framework.commons.runtime.ReturnWrapper;
import wang.liangchen.matrix.framework.springboot.json.JsonField;
import wang.liangchen.matrix.framework.web.annotation.ReturnRawText;
import wang.liangchen.matrix.framework.web.response.JsonResponse;

@RestController
@RequestMapping("/webmvc")
public class WebMvcController {

    @GetMapping("/returnNull")
    public SampleResponse returnNull() {
        return null;
    }

    @GetMapping("/returnVoid")
    public void returnVoid() {
    }

    @GetMapping("/returnString")
    public String returnString() {
        return "Welcome to Matrix!";
    }

    @GetMapping("/returnRawString")
    @ReturnRawText
    public String returnRawString() {
        return "Welcome to Matrix!";
    }

    @GetMapping("/returnNativeObject")
    public SampleResponse returnNativeObject() {
        SampleResponse response = new SampleResponse();
        response.setSampleId("0");
        response.setSampleName("name_0");
        return response;
    }

    @GetMapping("/returnReturnWrapper")
    public ReturnWrapper<SampleResponse> returnReturnWrapper() {
        SampleResponse response = new SampleResponse();
        response.setSampleId("0");
        response.setSampleName("name_0");
        return ReturnWrapper.success(response);
    }

    @GetMapping("/returnJsonResponse")
    public JsonResponse<SampleResponse> returnJsonResponse() {
        SampleResponse response = new SampleResponse();
        response.setSampleId("0");
        response.setSampleName("name_0");
        return JsonResponse.success(response);
    }

    @PostMapping("/requestWithJsonField")
    public JsonField requestWithJsonField(@RequestBody JsonField jsonField) {
        return jsonField;
    }
}
