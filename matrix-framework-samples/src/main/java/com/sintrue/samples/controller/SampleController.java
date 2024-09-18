package com.sintrue.samples.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sample")
public class SampleController {
    @GetMapping("/returnVoid")
    public void returnVoid() {
        System.out.println("==========");
        if(true){
            throw new RuntimeException("ddddd");
        }
    }
}
