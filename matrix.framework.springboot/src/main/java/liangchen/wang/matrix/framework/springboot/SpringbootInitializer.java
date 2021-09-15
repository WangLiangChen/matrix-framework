package liangchen.wang.matrix.framework.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Liangchen.Wang
 */
@SpringBootApplication
public class SpringbootInitializer {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(SpringbootInitializer.class);
        springApplication.run(args);
    }
}
