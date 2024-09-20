package org.example.store.repository;

import org.example.store.dto.category.CategoryUserExpenseDTO;
import org.example.store.dto.category.SubCategoryDTO;
import org.example.store.model.Product;
import org.example.store.model.SubCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubCategoryRepository extends JpaRepository<SubCategory, Long>, PagingAndSortingRepository<SubCategory, Long> {

    @Query("SELECT p FROM SubCategory s JOIN s.products p LEFT JOIN p.reviews r where s.name=?1 GROUP BY p.id")
    Page<Product> getProductsBySubCategory(String category, Pageable pageable);

    @Query("SELECT new org.example.store.dto.category.SubCategoryDTO(s.id, s.name) FROM SubCategory s ORDER BY s.name asc")
    List<SubCategoryDTO> getSubCategories();

    @Query("""
            SELECT NEW org.example.store.dto.category.CategoryUserExpenseDTO(s.name, 
            SUM(COALESCE(p.discount_price * si.quantity, p.price * si.quantity)), 
            CAST(ROUND(100.0 * SUM(COALESCE(p.discount_price * si.quantity, p.price * si.quantity)) / (SELECT SUM(COALESCE(p2.discount_price * si2.quantity, p2.price * si2.quantity)) 
             FROM SubCategory s2 JOIN s2.products p2 JOIN p2.sizes si2 WHERE p2 IN :companyProducts), 2) as double)) 
            FROM SubCategory s 
            RIGHT JOIN s.products p 
            RIGHT JOIN p.sizes si 
            WHERE p IN :companyProducts 
            GROUP BY s.name 
            ORDER BY 2 DESC 
            LIMIT 3""")
    List<CategoryUserExpenseDTO> getValueOfStoredProductsByCompanyId(@Param("companyProducts") List<Product> companyProducts);

    Optional<SubCategory> findSubCategoryByProductsContaining(Product product);

    SubCategory getSubCategoryByProductsContaining(Product product);

    SubCategory getSubCategoryByName(String name);
}
