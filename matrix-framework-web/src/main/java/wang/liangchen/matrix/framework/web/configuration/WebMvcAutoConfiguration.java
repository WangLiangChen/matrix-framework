package wang.liangchen.matrix.framework.web.configuration;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import wang.liangchen.matrix.framework.commons.enumeration.Symbol;
import wang.liangchen.matrix.framework.web.context.WebContext;
import wang.liangchen.matrix.framework.web.request.HttpServletRequestWrapper;
import wang.liangchen.matrix.framework.web.response.FormattedResponse;
import wang.liangchen.matrix.framework.web.response.HttpServletResponseWrapper;
import wang.liangchen.matrix.framework.web.response.ResponseLevel;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static javax.servlet.http.HttpServletResponse.SC_NOT_FOUND;

/**
 * @author LiangChen.Wang
 * 注册Filter的三种方式
 * 1、使用@WebFilter、@WebServlet、@WebListener
 * 2、使用FilterRegistrationBean、ServletRegistrationBean
 * 3、使用@Bean自动添加，添加后默认的过滤路径为 /*，使用FilterRegistrationBean来进行Filter的注册，filterRegistration.setEnabled(false);，就可以取消Filter的自动注册行为。
 */
@AutoConfigureAfter(org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.class)
public class WebMvcAutoConfiguration {
    //注册filter,@WebFilter需要在Configuration类上@ServletComponentScan
    @Bean
    public FilterRegistrationBean<Filter> rootFilter() {
        FilterRegistrationBean<Filter> filterFilterRegistrationBean = new FilterRegistrationBean<>(createRootFilter());
        filterFilterRegistrationBean.setEnabled(true);
        filterFilterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        filterFilterRegistrationBean.addUrlPatterns("/*");
        return filterFilterRegistrationBean;
    }

    private Filter createRootFilter() {
        return new Filter() {
            @Override
            public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
                HttpServletRequest request = (HttpServletRequest) servletRequest;
                String requestURI = request.getRequestURI();
                if (requestURI.endsWith("favicon.ico")) {
                    filterChain.doFilter(servletRequest, servletResponse);
                    return;
                }
                String requestId = request.getHeader(WebContext.REQUEST_ID);
                if (null == requestId) {
                    requestId = request.getParameter(WebContext.REQUEST_ID);
                }
                requestId = null == requestId ? Symbol.BLANK.getSymbol() : requestId;
                WebContext.INSTANCE.setRequestId(requestId);

                HttpServletRequestWrapper requestWrapper = new HttpServletRequestWrapper(request);
                HttpServletResponse response = (HttpServletResponse) servletResponse;
                HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(response);
                // set requestId for response
                responseWrapper.setHeader(WebContext.REQUEST_ID, requestId);
                try (ServletOutputStream outputStream = response.getOutputStream()) {
                    try {
                        filterChain.doFilter(requestWrapper, responseWrapper);
                    } catch (Exception e) {
                        outputStream.write(FormattedResponse.exception(e).toString().getBytes(StandardCharsets.UTF_8));
                        outputStream.flush();
                        return;
                    }
                    // handle 404
                    int statusCode = responseWrapper.getStatusCode();
                    if (SC_NOT_FOUND == statusCode) {
                        outputStream.write(FormattedResponse.failure()
                                .code(String.valueOf(SC_NOT_FOUND))
                                .level(ResponseLevel.ERROR)
                                .message("request does not exist: {}", requestURI).toString().getBytes(StandardCharsets.UTF_8));
                        outputStream.flush();
                        return;
                    }

                    // handle void
                    if (0 == responseWrapper.getContentSize()) {
                        outputStream.write(FormattedResponse.success().toString().getBytes(StandardCharsets.UTF_8));
                        outputStream.flush();
                        return;
                    }
                    outputStream.write(responseWrapper.getContentAsByteArray());
                    outputStream.flush();
                }
            }

            @Override
            public void destroy() {
                WebContext.INSTANCE.remove();
            }
        };
    }
}