package org.example.store.repository;

import org.example.store.model.OrderItem;
import org.example.store.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findOrderItemsByProduct(Product product);

    @Query("select oi from OrderItem oi LEFT JOIN oi.product p where p.id = ?1")
    List<OrderItem> findOrderItemsByProductId(Long productId);

    @Query("SELECT SUM(oi.quantity * oi.price) FROM Company c JOIN c.products p JOIN p.orderItems oi JOIN oi.statuses st WHERE c.id = ?1 AND st.status = 'IN_PROGRESS' AND st.time < ?2")
    BigDecimal getOrdersByCompanyIdAndEndDate(Long companyId, LocalDateTime endDate);

    @Query("SELECT SUM(oi.quantity * oi.price) FROM Company c JOIN c.products p JOIN p.orderItems oi JOIN oi.statuses st WHERE c.id = ?1 AND st.status = 'IN_PROGRESS' AND st.time BETWEEN ?3 AND ?2")
    BigDecimal getOrdersByCompanyIdAndStartDateAndEndDate(Long companyId, LocalDateTime startDate, LocalDateTime endDate);
}
