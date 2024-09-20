package org.example.store.services;

import org.example.store.dto.category.MainCategoryDTO;
import org.example.store.dto.category.SubCategoryDTO;
import org.example.store.dto.category.SubCategoryDTO2;
import org.example.store.dto.filters.FilterDTO;
import org.example.store.dto.order.OrderItemCreateDTO;
import org.example.store.dto.product.*;
import org.example.store.dto.stats.HomeStats;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public interface ProductsService {

    ResponseEntity<?> getProductPhoto(Long productId, int photoNumber);

    List<SubCategoryDTO> getSubCategories();

    List<ProductDTO3> getProductsSortedByRating();

    ProductRatingDTO calculateProductRating(Long productId);

    ProductDTO getProductById(Long productId);

    Page<ProductDTO3> getFormattedProductsByQuery(String query, int page);

    List<MainCategoryDTO> getCategories();

    List<FilterDTO> getFiltersForSubcategory(String category);

    Page<ProductDTO3> getNewProducts(int page);

    Page<ProductDTO3> getProductsByCategory(String category, Integer page);

    Page<ProductDTO3> getFilteredProducts(String category, Map<String, String> allFilters, BigDecimal from, BigDecimal to);

    Page<ProductReviewDTO> getProductReviews(Long id, int pageNum);

    List<SubCategoryDTO2> getProductsSortedBySales();

    Page<ProductDTO3> getDiscountedProducts(Integer pageNum);

    HomeStats getStats();

    ResponseEntity<?> getUserPhoto(Long id);

    List<OrderItemCreateDTO> validateProducts(List<OrderItemCreateDTO> products);

}
