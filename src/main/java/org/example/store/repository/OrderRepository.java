package org.example.store.repository;

import org.example.store.dto.CountryDTO;
import org.example.store.dto.stats.AmountDate2;
import org.example.store.dto.user.UserCompanyDTO;
import org.example.store.model.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query(value = "SELECT DISTINCT o.* FROM orders_table o " +
           "JOIN orderitem oi ON oi.fk_orderitem_orders_table = o.id " +
           "JOIN product p ON p.id = oi.fk_product_orderitem " +
           "JOIN company c ON p.fk_product_company = c.id " +
           "WHERE c.id = ?1", nativeQuery = true)
    List<Order> getOrdersByCompany(Long companyId);

    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN o.orderItem ord " +
            "JOIN ord.product pr " +
            "JOIN ord.statuses st " +
            "WHERE st.status = 'IN_PROGRESS' AND st.time > ?2 AND pr IN ?1")
    List<Order> getOrdersByCompanyIdAndEndDate(List<Product> products, LocalDateTime endDate);

    @Query("SELECT DISTINCT o FROM Order o " +
            "JOIN o.orderItem ord " +
            "JOIN ord.product pr " +
            "JOIN ord.statuses st " +
            "WHERE st.status = 'IN_PROGRESS' AND st.time BETWEEN ?3 AND ?2 AND pr IN ?1")
    List<Order> getOrdersByCompanyIdAndStartDateAndEndDate(List<Product> products, LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "SELECT DISTINCT o.* FROM orders_table o " +
            "RIGHT JOIN orderitem ON orderitem.fk_orderitem_orders_table = o.id " +
            "RIGHT JOIN product ON product.id = orderitem.fk_product_orderitem " +
            "RIGHT JOIN order_status st ON o.id = st.fk_order_status_order " +
            "WHERE product.fk_product_company = ?1 AND CAST(st.status AS VARCHAR) = ?2", nativeQuery = true)
    List<Order> getOrdersByCompanyIdAndStatus(Long companyId, String status);

    @Query("SELECT new org.example.store.dto.CountryDTO(ot.country, SUM(oi.quantity * oi.price), COUNT(DISTINCT ot.id)) " +
            "FROM Order ot " +
            "JOIN ot.orderItem oi " +
            "JOIN oi.product p " +
            "JOIN p.company c " +
            "WHERE c.id = ?1 " +
            "GROUP BY ot.country ORDER BY SUM(oi.quantity * oi.price) DESC")
    List<CountryDTO> getCountryIncomeByCompanyId(Long companyId);

    @Query("SELECT new org.example.store.dto.user.UserCompanyDTO(u.id, u.photo, u.first_name, u.last_name, SUM(oi.quantity * oi.price)) " +
            "FROM User u " +
            "JOIN u.orders o " +
            "JOIN o.orderItem oi " +
            "JOIN oi.product p " +
            "JOIN p.company c " +
            "WHERE c.id = ?1 " +
            "GROUP BY u.id, u.photo, u.first_name, u.last_name order by SUM(oi.quantity * oi.price) DESC limit 5")
    List<UserCompanyDTO> getCustomersByCompanyId(Long companyId);

    @Query("SELECT coalesce(SUM(it.price * it.quantity), 0) " +
            "FROM Company c " +
            "LEFT JOIN c.products p " +
            "LEFT JOIN p.orderItems it " +
            "LEFT JOIN it.statuses s " +
            "WHERE s.status = ?2 AND c.id = ?1")
    BigDecimal getCompanyIncomeByStatus(Long companyId, Status status);

    @Query(value = "SELECT coalesce(SUM(oi.price * oi.quantity),0) FROM orderitem oi RIGHT JOIN product ON product.id = oi.fk_product_orderitem WHERE product.fk_product_company = ?1", nativeQuery = true)
    BigDecimal getMoneyTurnoverByCompanyId(Long companyId);

    @Query("SELECT coalesce(SUM(it.price * it.quantity),0) from Order o LEFT JOIN o.statuses s LEFT JOIN o.orderItem it WHERE s.status='IN_PROGRESS' AND s.time BETWEEN ?1 AND ?2 ")
    BigDecimal getMoneyTurnoverInTime(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT coalesce(SUM(it.price * it.quantity),0) from User u LEFT JOIN u.orders o left JOIN o.statuses s LEFT JOIN o.orderItem it WHERE s.status='IN_PROGRESS' AND u = ?1 AND s.time > ?2")
    BigDecimal calculateMoneySpentByUserId(User user, LocalDateTime date);

    @Query("SELECT ROUND(coalesce(SUM(it.price * it.quantity),0) / ?2, 2) from User u LEFT JOIN u.orders o left JOIN o.statuses s LEFT JOIN o.orderItem it WHERE s.status='IN_PROGRESS' AND u = ?1")
    BigDecimal calculateAverageMoneySpentByUserIdAndMonths(User user, int months);

    @Query("SELECT new org.example.store.dto.stats.AmountDate2(t.date, SUM(t.amount)) " +
            "FROM User u " +
            "LEFT JOIN u.transactions t " +
            "WHERE u = ?1 AND t.date > ?2 AND t.type = 'SENT' " +
            "GROUP BY t.date")
    List<AmountDate2> calculateMoneySpentByUserIdInDays(User user, LocalDateTime date);

    @Query("SELECT coalesce(count(o), 0) from Order o LEFT JOIN o.statuses s WHERE s.status='IN_PROGRESS' AND s.time BETWEEN ?1 AND ?2")
    Integer getQuantityOfUncompletedOrders(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT COALESCE(sum(oi.quantity), 0) from Order o join o.orderItem oi LEFT JOIN o.statuses s WHERE s.status='IN_PROGRESS' AND s.time BETWEEN ?1 AND ?2 ")
    Integer getAmountOfSoldProducts(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT o FROM User u LEFT JOIN u.orders o left JOIN o.statuses st WHERE u = ?1 AND CAST(st.status AS String) = ?2")
    List<Order> getOrdersByUserIdAndStatus(User user, String status);

    @Query("SELECT o FROM Order o RIGHT JOIN o.orderItem or RIGHT join or.product p where p = ?1")
    List<Order> getOrdersByProduct(Product product);

    Order findOrderByOrderItemContaining(OrderItem orderItem);

    @Query("SELECT new org.example.store.dto.stats.AmountDate2(t.date, SUM(t.amount)) " +
            "FROM User u " +
            "LEFT JOIN u.transactions t " +
            "WHERE u = ?1 AND t.date > ?2 AND t.type = 'RECEIVED' " +
            "GROUP BY t.date")
    List<AmountDate2> calculateIncomeByUserIdInDays(User user, LocalDateTime date);
}
