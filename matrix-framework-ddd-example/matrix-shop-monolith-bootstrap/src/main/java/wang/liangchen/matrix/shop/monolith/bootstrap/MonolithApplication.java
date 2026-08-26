package wang.liangchen.matrix.shop.monolith.bootstrap;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 单体应用引导类：将商品与订单两个限界上下文打包为单体应用，
 * 两个上下文仍保持独立的包结构与依赖方向，跨上下文集成仍通过消息契约完成。
 */
@SpringBootApplication(scanBasePackages = "wang.liangchen.matrix.shop")
public class MonolithApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonolithApplication.class, args);
    }
}
