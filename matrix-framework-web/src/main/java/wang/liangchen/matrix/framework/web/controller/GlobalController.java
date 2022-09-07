package wang.liangchen.matrix.framework.web.controller;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/")
public class GlobalController {
    @GetMapping(value = "server")
    public Mono<Environment> server(ServerHttpRequest request) {
        Environment environment = new Environment();
        LocalDateTime now = LocalDateTime.now();
        environment.setDatetime(now);
        environment.setDate(now.toLocalDate());
        environment.setTime(now.toLocalTime());
        environment.setTimestamp(now.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli());
        String contextPath = request.getPath().contextPath().toString();
        environment.setContextPath(contextPath);
        URI uri = request.getURI();
        uri = uri.resolve(contextPath);
        environment.setBasePath(uri.toString());
        return Mono.just(environment);
    }
}
