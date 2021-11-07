package wang.liangchen.matrix.framework.web.controller;

import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import wang.liangchen.matrix.framework.web.domain.Server;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RestController
@RequestMapping("/")
public class GlobalController {
    @GetMapping(value = "server")
    public Mono<Server> server(ServerHttpRequest request) {
        Server server = new Server();
        LocalDateTime now = LocalDateTime.now();
        server.setDatetime(now);
        server.setDate(now.toLocalDate());
        server.setTime(now.toLocalTime());
        server.setTimestamp(now.atZone(ZoneOffset.systemDefault()).toInstant().toEpochMilli());
        String contextPath = request.getPath().contextPath().toString();
        server.setContextPath(contextPath);
        URI uri = request.getURI();
        uri = uri.resolve(contextPath);
        server.setBasePath(uri.toString());
        return Mono.just(server);
    }
}
