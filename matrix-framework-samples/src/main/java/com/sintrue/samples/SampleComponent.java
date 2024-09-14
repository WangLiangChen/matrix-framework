package com.sintrue.samples;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.stereotype.Component;

@Component
public class SampleComponent implements MessageSourceAware,DisposableBean {
    @Override
    public void destroy() throws Exception {
        System.out.println("------ SampleComponent destroy");
    }

    @Override
    public void setMessageSource(MessageSource messageSource) {
        System.out.println("------ SampleComponent: MessageSourceAware:"+messageSource);
    }
}
