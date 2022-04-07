package wang.liangchen.matrix.framework.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Liangchen.Wang
 */
@SpringBootApplication
public class WebInitializer {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(WebInitializer.class);
        springApplication.run(args);
    }
}
