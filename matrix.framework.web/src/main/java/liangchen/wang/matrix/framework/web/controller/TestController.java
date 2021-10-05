package liangchen.wang.matrix.framework.web.controller;

import liangchen.wang.matrix.framework.web.annotation.EnableWeb;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@EnableWeb(webType = EnableWeb.WebType.WEBFLUX)
public class TestController {
    @GetMapping("/test")
    public Mono<String> test() {
        int i = 0;
        return Mono.error(new Exception("abc"));
    }
}
