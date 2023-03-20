package wang.liangchen.matrix.framework.web.configuration;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;
import wang.liangchen.matrix.framework.commons.exception.MatrixErrorException;
import wang.liangchen.matrix.framework.commons.runtime.ReturnWrapper;
import wang.liangchen.matrix.framework.commons.type.ClassUtil;
import wang.liangchen.matrix.framework.web.response.FormattedResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Liangchen.Wang 2022-09-08 6:47
 */
public class RequestResponseBodyMethodProcessorConfiguration {
    private static final Pattern classNamePattern = Pattern.compile("(className\":\\s*\")(.*?)(\"[,}])");

    public RequestResponseBodyMethodProcessorConfiguration(final RequestMappingHandlerAdapter requestMappingHandlerAdapter) {
        List<HandlerMethodReturnValueHandler> returnValueHandlers = new ArrayList<>(requestMappingHandlerAdapter.getReturnValueHandlers());
        decorateReturnValueHandlers(returnValueHandlers);
        requestMappingHandlerAdapter.setReturnValueHandlers(returnValueHandlers);

        List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<>(requestMappingHandlerAdapter.getArgumentResolvers());
        decorateArgumentResolvers(argumentResolvers);
        requestMappingHandlerAdapter.setArgumentResolvers(argumentResolvers);

        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>(requestMappingHandlerAdapter.getMessageConverters());
        decorateMessageConverters(messageConverters);
        requestMappingHandlerAdapter.setMessageConverters(messageConverters);
    }

    private void decorateReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
        for (HandlerMethodReturnValueHandler returnValueHandler : returnValueHandlers) {
            if (returnValueHandler instanceof RequestResponseBodyMethodProcessor) {
                HandlerMethodReturnValueHandler delegate = new RequestResponseBodyMethodProcessorDelegate((RequestResponseBodyMethodProcessor) returnValueHandler);
                int index = returnValueHandlers.indexOf(returnValueHandler);
                returnValueHandlers.set(index, delegate);
                break;
            }
        }
    }

    private void decorateArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        for (HandlerMethodArgumentResolver argumentResolver : argumentResolvers) {
            if (argumentResolver instanceof RequestResponseBodyMethodProcessor) {
                HandlerMethodArgumentResolver delegate = new RequestResponseBodyMethodProcessorDelegate((RequestResponseBodyMethodProcessor) argumentResolver);
                int index = argumentResolvers.indexOf(argumentResolver);
                argumentResolvers.set(index, delegate);
                break;
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void decorateMessageConverters(List<HttpMessageConverter<?>> messageConverters) {
        int index = 0;
        for (HttpMessageConverter<?> messageConverter : messageConverters) {
            HttpMessageConverter<Object> objectMessageConverter = (HttpMessageConverter<Object>) messageConverter;
            messageConverters.set(index++, new HttpMessageConverterDelegate(objectMessageConverter));
        }
    }

    class HttpMessageConverterDelegate implements HttpMessageConverter<Object> {
        private final HttpMessageConverter<Object> delegate;

        HttpMessageConverterDelegate(HttpMessageConverter<Object> delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean canRead(Class<?> clazz, MediaType mediaType) {
            return this.delegate.canRead(clazz, mediaType);
        }

        @Override
        public boolean canWrite(Class<?> clazz, MediaType mediaType) {
            return this.delegate.canWrite(clazz, mediaType);
        }

        @Override
        public List<MediaType> getSupportedMediaTypes() {
            return this.delegate.getSupportedMediaTypes();
        }

        @Override
        public Object read(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
            int modifiers = clazz.getModifiers();
            boolean isAbstract = Modifier.isAbstract(modifiers);
            boolean isInterface = Modifier.isInterface(modifiers);
            if (isAbstract || isInterface) {
                HttpInputMessageDelegate inputMessageDelegate = new HttpInputMessageDelegate(inputMessage);
                String bodyString = inputMessageDelegate.getBodyString();
                Matcher matcher = classNamePattern.matcher(bodyString);
                if (matcher.find()) {
                    String className = matcher.group(2);
                    // 替换变量
                    clazz = ClassUtil.INSTANCE.forName(className);
                    inputMessage = inputMessageDelegate;
                }
            }
            return this.delegate.read(clazz, inputMessage);
        }

        @Override
        public void write(Object o, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
            this.delegate.write(o, contentType, outputMessage);
        }
    }

    class HttpInputMessageDelegate implements HttpInputMessage {
        private final HttpInputMessage delegate;
        private final InputStream inputStream;
        private final String bodyString;

        HttpInputMessageDelegate(HttpInputMessage delegate) {
            this.delegate = delegate;
            // copy inputstream;
            FastByteArrayOutputStream outputStream = new FastByteArrayOutputStream();
            byte[] buffer = new byte[2048];
            int length;
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

        @Override
        public InputStream getBody() throws IOException {
            return this.inputStream;
        }

        @Override
        public HttpHeaders getHeaders() {
            return this.delegate.getHeaders();
        }

        public String getBodyString() {
            return bodyString;
        }
    }

    class RequestResponseBodyMethodProcessorDelegate implements HandlerMethodReturnValueHandler, HandlerMethodArgumentResolver {
        private final RequestResponseBodyMethodProcessor delegate;

        RequestResponseBodyMethodProcessorDelegate(RequestResponseBodyMethodProcessor delegate) {
            this.delegate = delegate;
        }

        @Override
        public boolean supportsReturnType(MethodParameter returnType) {
            return delegate.supportsReturnType(returnType);
        }

        @Override
        public void handleReturnValue(Object returnValue, MethodParameter methodParameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
            Object newReturnValue;
            if (returnValue instanceof FormattedResponse) {
                newReturnValue = returnValue;
            } else if (returnValue instanceof ReturnWrapper) {
                newReturnValue = FormattedResponse.of((ReturnWrapper<?>) returnValue);
            } else {
                newReturnValue = FormattedResponse.success().payload(returnValue);
            }
            delegate.handleReturnValue(newReturnValue, methodParameter, mavContainer, webRequest);
        }

        @Override
        public boolean supportsParameter(MethodParameter parameter) {
            return this.delegate.supportsParameter(parameter);
        }

        @Override
        public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
            return this.delegate.resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        }
    }
}
