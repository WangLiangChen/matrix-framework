package wang.liangchen.matrix.shop.order.southbound.adapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * 购物车数据访问对象：Spring Data JPA仓储，仅在南向适配层内部使用。
 */
public interface CartDao extends JpaRepository<CartPo, String> {

    Optional<CartPo> findByBuyerId(String buyerId);

    List<CartPo> findByItemsProductId(String productId);
}
