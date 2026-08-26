package wang.liangchen.matrix.shop.order.southbound.adapter.repository;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 购物车数据访问对象：Spring Data JPA仓储，仅在南向适配层内部使用。
 */
public interface CartDao extends JpaRepository<CartPo, String> {
}
