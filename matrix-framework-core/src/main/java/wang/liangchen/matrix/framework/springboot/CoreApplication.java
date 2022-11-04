package wang.liangchen.matrix.framework.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author Liangchen.Wang 2022-11-04 12:43
 */
@SpringBootApplication
public class CoreApplication {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(CoreApplication.class);
        springApplication.run(args);
    }
}
