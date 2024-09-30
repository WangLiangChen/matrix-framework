package com.sintrue.samples.controller;

import com.sintrue.samples.vo.NativeObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.liangchen.matrix.framework.commons.runtime.ReturnWrapper;
import wang.liangchen.matrix.framework.web.response.JsonResponse;

@RestController
@RequestMapping("/sample")
public class SampleController {

    @GetMapping("/void")
    public void returnVoid() {
    }

    @GetMapping("/string")
    public String returnString() {
        return "Hello, World!";
    }

    @GetMapping("/nativeObject")
    public NativeObject returnNativeObject() {
        return new NativeObject();
    }

    @GetMapping("/returnWrapper")
    public ReturnWrapper<?> returnWrapper() {
        return ReturnWrapper.success();
    }

    @GetMapping("/jsonResponse")
    public JsonResponse<?> returnJsonResponse() {
        return JsonResponse.success();
    }
}
