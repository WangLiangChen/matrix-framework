package wang.liangchen.matrix.framework.web.configuration;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import wang.liangchen.matrix.framework.commons.CollectionUtil;
import wang.liangchen.matrix.framework.commons.runtime.ReturnWrapper;
import wang.liangchen.matrix.framework.web.response.JsonResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class RequestMappingHandlerAdapterEnhancer {

    public RequestMappingHandlerAdapterEnhancer(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        enhanceReturnValueHandlers(requestMappingHandlerAdapter);
    }

    private void enhanceReturnValueHandlers(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        List<HandlerMethodReturnValueHandler> returnValueHandlers = requestMappingHandlerAdapter.getReturnValueHandlers();
        if (CollectionUtil.INSTANCE.isEmpty(returnValueHandlers)) {
            return;
        }
        List<HandlerMethodReturnValueHandler> enhancedReturnValueHandlers = new ArrayList<>();
        returnValueHandlers.forEach(handler -> {
            enhancedReturnValueHandlers.add(new HandlerMethodReturnValueHandlerEnhancer(handler));
        });
        requestMappingHandlerAdapter.setReturnValueHandlers(enhancedReturnValueHandlers);
    }

    private static class HandlerMethodReturnValueHandlerEnhancer implements HandlerMethodReturnValueHandler {
        private final HandlerMethodReturnValueHandler delegate;

        public HandlerMethodReturnValueHandlerEnhancer(HandlerMethodReturnValueHandler delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean supportsReturnType(MethodParameter returnType) {
            return this.delegate.supportsReturnType(returnType);
        }

        @Override
        public void handleReturnValue(Object returnValue, MethodParameter methodParameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
            Object enhancedValue = null;
            if (this.delegate instanceof RequestResponseBodyMethodProcessor) {
                Class<?> returnType = Objects.requireNonNull(methodParameter.getMethod()).getReturnType();
                if (void.class.isAssignableFrom(returnType)) {
                    enhancedValue = JsonResponse.success();
                } else if (JsonResponse.class.isAssignableFrom(returnType)) {
                    enhancedValue = returnValue;
                } else if (ReturnWrapper.class.isAssignableFrom(returnType)) {
                    enhancedValue = JsonResponse.of((ReturnWrapper<?>) returnValue);
                } else {
                    enhancedValue = JsonResponse.success(returnValue);
                }
            }
            this.delegate.handleReturnValue(enhancedValue, methodParameter, mavContainer, webRequest);
        }
    }
}
