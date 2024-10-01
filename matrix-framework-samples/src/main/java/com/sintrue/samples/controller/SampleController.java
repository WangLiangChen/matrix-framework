package com.sintrue.samples.controller;

import com.sintrue.samples.vo.INativeObject;
import com.sintrue.samples.vo.NativeObjectA;
import org.springframework.web.bind.annotation.*;
import wang.liangchen.matrix.framework.commons.exception.MatrixWarnException;
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
    public NativeObjectA returnNativeObject() {
        return new NativeObjectA();
    }

    @GetMapping("/returnWrapper")
    public ReturnWrapper<?> returnWrapper() {
        return ReturnWrapper.success();
    }

    @GetMapping("/jsonResponse")
    public JsonResponse<?> returnJsonResponse() {
        return JsonResponse.success();
    }

    @GetMapping("/warnException")
    public void warnException() {
        throw new MatrixWarnException("warn exception").withCode("warn");
    }

    @PostMapping("/interfaceBody")
    public String interfaceBody(@RequestBody INativeObject nativeObject) {
        return nativeObject.getClass().toString();
    }
}
