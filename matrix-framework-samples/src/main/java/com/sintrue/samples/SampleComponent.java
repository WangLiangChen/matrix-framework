package com.sintrue.samples;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class SampleComponent implements InitializingBean,DisposableBean {
    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("------ SampleComponent afterPropertiesSet");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("------ SampleComponent destroy");
    }

}
