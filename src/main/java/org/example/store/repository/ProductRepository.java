package org.example.store.repository;

import org.example.store.dto.ProductIncomeDTO;
import org.example.store.model.Company;
import org.example.store.model.Product;
import org.example.store.model.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT r from Product p JOIN p.reviews r WHERE p.id = ?1 ORDER BY r.sendtime DESC ")
    Page<ProductReview> getProductReviewsByProductId(Long id, Pageable pageable);

    @Query("SELECT coalesce(count(p),0) from Product p WHERE p.created BETWEEN ?1 AND ?2")
    Integer getAmountOfNewProducts(LocalDateTime startDate, LocalDateTime endDate);

    @Query("SELECT p from Company c JOIN c.products p WHERE p.id = ?1 AND c.name = ?2")
    Optional<Product> getProductByIdAndCompanyId(Long productId, String username);

    @Query("SELECT p FROM Product p LEFT JOIN p.reviews r where p.created >= ?1 and p.retired IS NULL GROUP BY p.id")
    Page<Product> getPremieredProducts(LocalDateTime premiereTime, Pageable pageable);

    @Query("SELECT p FROM Product p LEFT JOIN p.reviews r where p.discount_price IS NOT NULL and p.retired IS NULL GROUP BY p.id")
    Page<Product> getDiscountedProducts(Pageable page);

    @Query("SELECT p FROM Product p RIGHT JOIN p.reviews r WHERE p.retired IS NULL GROUP BY p.id ORDER BY COALESCE(ROUND(AVG(r.rating), 0), 0) DESC")
    List<Product> getMostRatedProducts(Pageable pageable);

    @Query("SELECT p FROM SubCategory s RIGHT JOIN s.products p WHERE s.id=?1 AND p.retired IS NULL GROUP BY p.id order by p.sales DESC LIMIT 6")
    List<Product> getProductsByCategoryIdAndSortBySales(Long id);

    @Query("SELECT p FROM Product p LEFT JOIN p.parameters pa LEFT JOIN p.reviews r where p.name LIKE %?1% or p.description LIKE %?1% or pa.value LIKE %?1% and p.retired IS NULL GROUP BY p.id")
    Page<Product> getProductsByQuery(String query, Pageable page);

    @Query("SELECT r FROM Company c RIGHT JOIN c.products p RIGHT JOIN p.reviews r where c = ?1 and r.sendtime > ?2")
    List<ProductReview> findProductReviewsByCompanyIdAndTime(Company company, LocalDateTime dateTime);

    @Query("SELECT r FROM Company c RIGHT JOIN c.products p RIGHT JOIN p.reviews r where c = ?1 and r.sendtime BETWEEN ?2 AND ?3")
    List<ProductReview> findProductReviewsByCompanyIdAndTime(Company company, LocalDateTime startDate, LocalDateTime endDate);

    @Modifying
    @Transactional
    @Query(value = "UPDATE product SET retired = CURRENT_TIMESTAMP WHERE fk_product_company = :companyId AND retired IS NULL", nativeQuery = true)
    void retireProductsByCompanyId(@Param("companyId") Long companyId);

    @Modifying
    @Transactional
    @Query(value = "UPDATE product SET retired = CURRENT_TIMESTAMP WHERE id = :productId AND retired IS NULL", nativeQuery = true)
    void retireProductById(@Param("productId") Long productId);

    @Query("select count(p) from Company c RIGHT JOIN c.products p where p.retired IS NULL and c = ?1")
    int getAmountOfActiveProducts(Company company);

    @Query("select count(p) from Company c RIGHT JOIN c.products p where p.retired IS NOT NULL and c = ?1")
    int getAmountOfRetiredProducts(Company company);

    @Query("select coalesce(avg(r.rating),0) from Company c RIGHT join c.products p Left join p.reviews r where c = ?1")
    double getRatingByCompanyId(Company company);

    @Query("select coalesce(avg(r.rating), 0) from Product p RIGHT join p.reviews r where p = ?1")
    Double getRatingByProduct(Product product);

    @Query("select coalesce(count(r.rating), 0) from Product p RIGHT join p.reviews r where p = ?1")
    Integer getRatingOfProduct(Product product);

    @Query("select coalesce(coalesce(p.discount_price, p.price) * sum(s.quantity), 0) from Product p RIGHT join p.sizes s where p = ?1 group by p.id")
    BigDecimal getValueOfStoredProduct(Product product);

    @Query("select coalesce(sum(s.quantity * coalesce(p.discount_price, p.price)), 0) from Company c RIGHT join c.products p inner join p.sizes s where c = ?1")
    BigDecimal getValueOfStoredProductsByCompanyId(Company company);

    @Query("select new org.example.store.dto.ProductIncomeDTO(p.name, sum(oi.quantity * oi.price)) from Company c RIGHT join c.products p right join p.orderItems oi where c.id = ?1 and p.retired IS NULL GROUP BY p.id ORDER BY sum(oi.quantity * oi.price) DESC LIMIT 3")
    List<ProductIncomeDTO> getTop3ProductsCompanyId(Long companyId);
}
