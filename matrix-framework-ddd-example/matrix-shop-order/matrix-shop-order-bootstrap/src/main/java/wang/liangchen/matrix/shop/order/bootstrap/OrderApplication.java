package wang.liangchen.matrix.shop.order.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 订单微服务引导类：以订单限界上下文为边界的独立微服务入口。
 */
@SpringBootApplication(scanBasePackages = "wang.liangchen.matrix.shop.order")
@EnableScheduling
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
    }
}
