package com.sintrue.samples;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

@Component
public class SampleComponent implements DisposableBean {
    @Override
    public void destroy() throws Exception {
        System.out.println("SampleComponent destroy");
    }
}
