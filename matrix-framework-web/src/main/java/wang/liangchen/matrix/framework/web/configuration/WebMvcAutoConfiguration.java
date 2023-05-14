package wang.liangchen.matrix.framework.web.configuration;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import wang.liangchen.matrix.framework.commons.exception.ExceptionLevel;
import wang.liangchen.matrix.framework.commons.validation.ValidationUtil;
import wang.liangchen.matrix.framework.web.context.WebContext;
import wang.liangchen.matrix.framework.web.request.HttpServletRequestWrapper;
import wang.liangchen.matrix.framework.web.response.FormattedResponse;
import wang.liangchen.matrix.framework.web.response.HttpServletResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;


/**
 * @author LiangChen.Wang
 * 注册Filter的三种方式
 * 1、使用@WebFilter、@WebServlet、@WebListener
 * 2、使用FilterRegistrationBean、ServletRegistrationBean
 * 3、使用@Bean自动添加，添加后默认的过滤路径为 /*，使用FilterRegistrationBean来进行Filter的注册，filterRegistration.setEnabled(false);，就可以取消Filter的自动注册行为。
 */
@AutoConfigureAfter(org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.class)
public class WebMvcAutoConfiguration implements WebMvcConfigurer {
    //注册filter,@WebFilter需要在Configuration类上@ServletComponentScan
    @Bean
    public FilterRegistrationBean<Filter> rootFilter(final LocaleResolver localeResolver) {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>(createRootFilter(localeResolver));
        filterFilterRegistrationBean.setEnabled(true);
        filterFilterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        filterFilterRegistrationBean.addUrlPatterns("/*");
        return filterFilterRegistrationBean;
    }

    private Filter createRootFilter(final LocaleResolver localeResolver) {
        return new Filter() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
                HttpServletRequest request = (HttpServletRequest) servletRequest;
                String requestURI = request.getRequestURI();
                if (requestURI.endsWith("favicon.ico")) {
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                }

                // resolve and set locale
                Locale locale = localeResolver.resolveLocale(request);
                ValidationUtil.INSTANCE.resetLocale(locale);
                WebContext.INSTANCE.setLocale(locale);

                // resolve and set requestId
                String requestId = request.getHeader(WebContext.REQUEST_ID);
                if (null == requestId) {
                    requestId = request.getParameter(WebContext.REQUEST_ID);
                }
                if (null != requestId) {
                    WebContext.INSTANCE.setRequestId(requestId);
                }

                // wrap request and response
                HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request);
                HttpServletResponse response = (HttpServletResponse) servletResponse;
                HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(response);
                // set requestId for response
                responseWrapper.setHeader(WebContext.REQUEST_ID, requestId);
                response.setHeader(WebContext.REQUEST_ID, requestId);

                try {
                    // doFilter with wrapped request and response
                    filterChain.doFilter(requestWrapper, responseWrapper);
                    boolean asyncStarted = requestWrapper.isAsyncStarted();
                    if (asyncStarted) {
                        responseWrapper.setAsyncRequestStarted(true);
                        return;
                    }
                } catch (Throwable throwable) {
                    // catch exception
                    flushFormattedResponse(response, FormattedResponse.exception(throwable));
                    return;
                }
                // catch 404
                if (SC_NOT_FOUND == responseWrapper.getStatusCode()) {
                    flushFormattedResponse(response, FormattedResponse.failure()
                            .code(String.valueOf(SC_NOT_FOUND))
                            .level(ExceptionLevel.ERROR)
                            .i18n("Resource.Absent")
                            .message("The resource required for this request does not exist: {}", requestURI));
                    return;
                }

                // handle void
                if (0 == responseWrapper.getContentSize()) {
                    flushFormattedResponse(response, FormattedResponse.success());
                    return;
                }
                flushBytes(response, responseWrapper.getContentAsByteArray());
            }

            private void flushFormattedResponse(HttpServletResponse response, FormattedResponse<?> formattedResponse) throws IOException {
                response.setContentType("application/json;charset=utf-8");
                flushBytes(response, formattedResponse.toString().getBytes(StandardCharsets.UTF_8));
            }

            private void flushBytes(HttpServletResponse response, byte[] bytes) throws IOException {
                response.setContentLength(bytes.length);
                try (ServletOutputStream outputStream = response.getOutputStream()) {
                    outputStream.write(bytes);
                    outputStream.flush();
                } finally {
                    // remove ThreadLocal when flushed
                    WebContext.INSTANCE.remove();
                    ValidationUtil.INSTANCE.removeLocale();
                }
            }
        };
    }
}