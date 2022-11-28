package wang.liangchen.matrix.framework.web.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import wang.liangchen.matrix.framework.web.push.PushUtil;
import wang.liangchen.matrix.framework.web.push.PusherType;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/")
public class GlobalController {
    @GetMapping(value = "server")
    public Environment server(HttpServletRequest request) {
        Environment environment = new Environment();
        LocalDateTime now = LocalDateTime.now();
        environment.setDatetime(now);
        environment.setDate(now.toLocalDate());
        environment.setTime(now.toLocalTime());
        environment.setTimestamp(now.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli());
        String contextPath = request.getContextPath();
        environment.setContextPath(contextPath);
        String uri = request.getRequestURI();
        environment.setBasePath(uri);
        PushUtil.INSTANCE.broadcast(PusherType.DeferredResult, environment);
        return environment;
    }
}
