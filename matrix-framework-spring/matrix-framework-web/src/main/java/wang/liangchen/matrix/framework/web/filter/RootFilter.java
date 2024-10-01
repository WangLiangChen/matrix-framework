package wang.liangchen.matrix.framework.web.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import wang.liangchen.matrix.framework.commons.StringUtil;
import wang.liangchen.matrix.framework.commons.runtime.LocaleTimeZoneContext;
import wang.liangchen.matrix.framework.web.context.WebContext;

import java.io.IOException;

@Component
public class RootFilter extends OncePerRequestFilter implements OrderedFilter {

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String requestId = request.getParameter(WebContext.REQUEST_ID);
        if (StringUtil.INSTANCE.isBlank(requestId)) {
            requestId = request.getHeader(WebContext.REQUEST_ID);
        }
        // Set request id to thread local
        WebContext.INSTANCE.setRequestId(requestId);
        // Set request id to response header
        response.setHeader(WebContext.REQUEST_ID, requestId);

        try {
            filterChain.doFilter(request, response);
        } finally {
            WebContext.INSTANCE.remove();
            LocaleTimeZoneContext.INSTANCE.remove();
            LocaleContextHolder.resetLocaleContext();
        }
    }
}
