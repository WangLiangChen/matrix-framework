package wang.liangchen.matrix.framework.web.configuration;

import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import wang.liangchen.matrix.framework.web.response.FormattedResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Liangchen.Wang 2022-09-08 6:47
 */
public class RequestResponseBodyMethodProcessorConfiguration {

    public RequestResponseBodyMethodProcessorConfiguration(final RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        List<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<>(requestMappingHandlerAdapter.getReturnValueHandlers());
        decorateHandlers(returnValueHandlers);
        requestMappingHandlerAdapter.setReturnValueHandlers(returnValueHandlers);
    }

    private void decorateHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        for (HandlerMethodReturnValueHandler returnValueHandler : returnValueHandlers) {
            if (returnValueHandler instanceof RequestResponseBodyMethodProcessor) {
                HandlerMethodReturnValueHandler delegate = new RequestResponseBodyMethodProcessorDelegate((RequestResponseBodyMethodProcessor) returnValueHandler);
                int index = returnValueHandlers.indexOf(returnValueHandler);
                returnValueHandlers.set(index, delegate);
                break;
            }
        }
    }
    class RequestResponseBodyMethodProcessorDelegate implements HandlerMethodReturnValueHandler{
        private final RequestResponseBodyMethodProcessor delegate;

        RequestResponseBodyMethodProcessorDelegate(RequestResponseBodyMethodProcessor delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean supportsReturnType(MethodParameter returnType) {
            return delegate.supportsReturnType(returnType);
        }

        @Override
        public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
            if(!(returnValue instanceof FormattedResponse)){
                returnValue = FormattedResponse.success().payload(returnValue);
            }
            delegate.handleReturnValue(returnValue,returnType,mavContainer,webRequest);
        }
    }
}
