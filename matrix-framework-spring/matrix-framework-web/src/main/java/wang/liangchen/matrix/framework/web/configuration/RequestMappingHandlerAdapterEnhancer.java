package wang.liangchen.matrix.framework.web.configuration;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.GenericHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import wang.liangchen.matrix.framework.commons.CollectionUtil;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.runtime.ReturnWrapper;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.web.response.JsonResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class RequestMappingHandlerAdapterEnhancer {

    public RequestMappingHandlerAdapterEnhancer(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        enhanceReturnValueHandlers(requestMappingHandlerAdapter);
        enhanceMessageConverters(requestMappingHandlerAdapter);
    }

    private void enhanceMessageConverters(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        List<HttpMessageConverter<?>> messageConverters = requestMappingHandlerAdapter.getMessageConverters();
        if (CollectionUtil.INSTANCE.isEmpty(messageConverters)) {
            return;
        }
        List<HttpMessageConverter<?>> enhancedMessageConverters = new ArrayList<>();
        messageConverters.forEach(converter -> {
            if (converter instanceof GenericHttpMessageConverter<?> genericConverter) {
                enhancedMessageConverters.add(new GenericHttpMessageConverterEnhancer<>(genericConverter));
            } else {
                enhancedMessageConverters.add(converter);
            }
        });
        requestMappingHandlerAdapter.setMessageConverters(enhancedMessageConverters);
    }

    private void enhanceReturnValueHandlers(RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        List<HandlerMethodReturnValueHandler> returnValueHandlers = requestMappingHandlerAdapter.getReturnValueHandlers();
        if (CollectionUtil.INSTANCE.isEmpty(returnValueHandlers)) {
            return;
        }
        List<HandlerMethodReturnValueHandler> enhancedReturnValueHandlers = new ArrayList<>();
        returnValueHandlers.forEach(handler -> enhancedReturnValueHandlers.add(new HandlerMethodReturnValueHandlerEnhancer(handler)));
        requestMappingHandlerAdapter.setReturnValueHandlers(enhancedReturnValueHandlers);
    }


    private static class HandlerMethodReturnValueHandlerEnhancer implements HandlerMethodReturnValueHandler {
        private final HandlerMethodReturnValueHandler delegate;

        public HandlerMethodReturnValueHandlerEnhancer(HandlerMethodReturnValueHandler delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean supportsReturnType(@NonNull MethodParameter returnType) {
            return this.delegate.supportsReturnType(returnType);
        }

        @Override
        public void handleReturnValue(Object returnValue, @NonNull MethodParameter methodParameter, @NonNull ModelAndViewContainer mavContainer, @NonNull NativeWebRequest webRequest) throws Exception {
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

    private static class HttpInputMessageWrapper implements HttpInputMessage {
        private final HttpInputMessage delegate;
        private final InputStream inputStream;
        private final String bodyString;

        private HttpInputMessageWrapper(HttpInputMessage delegate) {
            this.delegate = delegate;
            byte[] buffer = new byte[2048];
            int length;
            try (FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream()) {
                try (InputStream body = delegate.getBody();) {
                    while ((length = body.read(buffer)) > -1) {
                        outputStream.write(buffer, 0, length);
                    }
                } catch (IOException e) {
                    throw new MatrixErrorException("read request body error.", e);
                }
                byte[] bytes = outputStream.toByteArray();
                this.bodyString = new String(bytes);
                this.inputStream = new ByteArrayInputStream(bytes);
            }
        }

        @NonNull
        @Override
        public InputStream getBody() throws IOException {
            return this.inputStream;
        }

        @NonNull
        @Override
        public HttpHeaders getHeaders() {
            return this.delegate.getHeaders();
        }

        public String getBodyString() {
            return bodyString;
        }
    }

    private static class GenericHttpMessageConverterEnhancer<T> implements GenericHttpMessageConverter<T> {
        private static final Pattern classNamePattern = Pattern.compile("(className\":\\s*\")(.*?)(\"[,\\n}])");
        private final GenericHttpMessageConverter<T> delegate;

        private GenericHttpMessageConverterEnhancer(GenericHttpMessageConverter<T> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean canRead(@NonNull Type type, Class<?> contextClass, MediaType mediaType) {
            return this.delegate.canRead(type, contextClass, mediaType);
        }

        @NonNull
        @SuppressWarnings("unchecked")
        @Override
        public T read(@NonNull Type type, Class<?> contextClass, @NonNull HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
            if (type instanceof Class<?> clazz) {
                return readWithClassName((Class<? extends T>) clazz, inputMessage);
            }
            return this.delegate.read(type, contextClass, inputMessage);
        }

        @Override
        public boolean canWrite(Type type, @NonNull Class<?> clazz, MediaType mediaType) {
            return this.delegate.canWrite(type, clazz, mediaType);
        }

        @Override
        public void write(@NonNull T t, Type type, MediaType contentType, @NonNull HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
            this.delegate.write(t, type, contentType, outputMessage);
        }

        @Override
        public boolean canRead(@NonNull Class<?> clazz, MediaType mediaType) {
            return this.delegate.canRead(clazz, mediaType);
        }

        @Override
        public boolean canWrite(@NonNull Class<?> clazz, MediaType mediaType) {
            return this.delegate.canWrite(clazz, mediaType);
        }

        @NonNull
        @Override
        public List<MediaType> getSupportedMediaTypes() {
            return this.delegate.getSupportedMediaTypes();
        }

        @NonNull
        @Override
        public T read(@NonNull Class<? extends T> clazz, @NonNull HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
            return readWithClassName(clazz, inputMessage);
        }

        @Override
        public void write(@NonNull T t, MediaType contentType, @NonNull HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
            this.delegate.write(t, contentType, outputMessage);
        }

        @NonNull
        @Override
        public List<MediaType> getSupportedMediaTypes(@NonNull Class<?> clazz) {
            return this.delegate.getSupportedMediaTypes(clazz);
        }

        @SuppressWarnings("unchecked")
        @NonNull
        private T readWithClassName(Class<? extends T> clazz, HttpInputMessage inputMessage) throws IOException {
            // 如果是Map、Collection 则直接处理
            if (Map.class.isAssignableFrom(clazz) || Collection.class.isAssignableFrom(clazz)) {
                return this.delegate.read(clazz, inputMessage);
            }
            int modifiers = clazz.getModifiers();
            if (Modifier.isAbstract(modifiers) || Modifier.isInterface(modifiers)) {
                HttpInputMessageWrapper inputMessageWrapper = new HttpInputMessageWrapper(inputMessage);
                String bodyString = inputMessageWrapper.getBodyString();
                Matcher matcher = classNamePattern.matcher(bodyString);
                if (matcher.find()) {
                    String className = matcher.group(2);
                    Class<?> subclass = ClassUtil.INSTANCE.forName(className);
                    // 子类&&同包
                    if (clazz.isAssignableFrom(subclass) && clazz.getPackageName().equals(subclass.getPackageName())) {
                        return this.delegate.read((Class<? extends T>) subclass, inputMessageWrapper);
                    }
                }
            }
            return this.delegate.read(clazz, inputMessage);
        }
    }


}
