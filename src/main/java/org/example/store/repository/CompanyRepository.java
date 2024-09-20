package org.example.store.repository;

import org.example.store.model.Company;
import org.example.store.model.OrderItem;
import org.example.store.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query("SELECT c FROM Company c JOIN c.products p WHERE ?1 = p")
    Company findCompanyByProduct(Product product);

    @Query("SELECT c FROM Company c RIGHT JOIN c.products p RIGHT JOIN p.orderItems o WHERE o = ?1")
    Company findCompanyByOrderItem(OrderItem orderItem);

    @Query("SELECT p FROM Company c RIGHT JOIN c.products p WHERE c = ?1")
    List<Product> findProductsByCompany(Company company);

    @Query("SELECT o FROM Company c RIGHT JOIN c.products p RIGHT JOIN p.orderItems o WHERE c = ?1")
    List<OrderItem> findOrderItemsByCompany(Company company);

    @Query("""
            SELECT COUNT(DISTINCT u.id) 
            FROM User u 
            JOIN u.orders o 
            JOIN o.statuses s 
            JOIN o.orderItem oi 
            JOIN oi.product p 
            JOIN p.company c 
            WHERE c = :company 
            AND s.status = 'IN_PROGRESS' 
            AND s.time = (
                SELECT MIN(s2.time) 
                FROM User u2 
                JOIN u2.orders o2 
                JOIN o2.statuses s2 
                JOIN o2.orderItem oi2 
                JOIN oi2.product p2 
                JOIN p2.company c2 
                WHERE c2 = :company 
                AND s2.status = 'IN_PROGRESS' 
                AND u2 = u
            ) 
            AND s.time >= :startDate""")
    int getCompanyNewCustomers(@Param("company") Company company, @Param("startDate") LocalDateTime startDate);

    @Query("""
            SELECT COUNT(DISTINCT o.country) 
            FROM Order o 
            JOIN o.statuses s 
            JOIN o.orderItem oi 
            JOIN oi.product p 
            JOIN p.company c 
            WHERE c = :company 
            AND s.status = 'IN_PROGRESS' 
            AND s.time = (
                SELECT MIN(s2.time) 
                FROM Order o2 
                JOIN o2.statuses s2 
                JOIN o2.orderItem oi2 
                JOIN oi2.product p2 
                JOIN p2.company c2 
                WHERE c2 = :company 
                AND s2.status = 'IN_PROGRESS' 
                AND o2.country = o.country
            ) 
            AND s.time >= :startDate""")
    int getCompanyNewCountries(@Param("company") Company company, @Param("startDate") LocalDateTime startDate);
}
