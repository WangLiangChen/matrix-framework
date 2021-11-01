package wang.liangchen.matrix.framework.data;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author LiangChen.Wang
 */
@SpringBootApplication
public class DataInitializer {
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(DataInitializer.class);
        springApplication.run(args);
    }
}
