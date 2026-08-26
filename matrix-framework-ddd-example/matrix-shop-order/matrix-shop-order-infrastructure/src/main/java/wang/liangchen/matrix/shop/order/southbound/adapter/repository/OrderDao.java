package wang.liangchen.matrix.shop.order.southbound.adapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * 订单数据访问对象：Spring Data JPA仓储，仅在南向适配层内部使用。
 */
public interface OrderDao extends JpaRepository<OrderPo, String> {

    List<OrderPo> findByBuyerId(String buyerId);
}
