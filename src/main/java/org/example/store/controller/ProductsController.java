package org.example.store.controller;

import jakarta.validation.constraints.NotBlank;
import org.example.store.dto.category.MainCategoryDTO;
import org.example.store.dto.category.SubCategoryDTO;
import org.example.store.dto.category.SubCategoryDTO2;
import org.example.store.dto.filters.FilterDTO;
import org.example.store.dto.order.OrderItemCreateDTO;
import org.example.store.dto.product.*;
import org.example.store.dto.stats.HomeStats;
import org.example.store.services.ProductsService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/products")
public class ProductsController {

    private final ProductsService productsService;

    public ProductsController(ProductsService productsService) {
        this.productsService = productsService;
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return new ResponseEntity<>(productsService.getProductById(id), HttpStatus.OK);
    }

    @GetMapping("/rating/{id}")
    public ResponseEntity<ProductRatingDTO> getProductRating(@PathVariable("id") Long id) {
        return new ResponseEntity<>(productsService.calculateProductRating(id), HttpStatus.OK);
    }

    @GetMapping("/reviews/{id}/page/{pageNum}")
    public ResponseEntity<Page<ProductReviewDTO>> getProductReviews(@PathVariable("id") Long id, @PathVariable("pageNum") int pageNum) {
        return new ResponseEntity<>(productsService.getProductReviews(id, pageNum), HttpStatus.OK);
    }

    @GetMapping("/stats")
    public ResponseEntity<HomeStats> getStats() {
        return new ResponseEntity<>(productsService.getStats(), HttpStatus.OK);
    }

    @GetMapping("/subcategory/{category}")
    public ResponseEntity<Page<ProductDTO3>> getProductsByCategory(@PathVariable("category") String category, @RequestParam(defaultValue = "0") Integer page) {
        return new ResponseEntity<>(productsService.getProductsByCategory(category, page), HttpStatus.OK);
    }

    @GetMapping("/new")
    public ResponseEntity<Page<ProductDTO3>> getNewProducts(@RequestParam(defaultValue = "0") Integer page) {
        return new ResponseEntity<>(productsService.getNewProducts(page), HttpStatus.OK);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<MainCategoryDTO>> getCategories() {
        return new ResponseEntity<>(productsService.getCategories(), HttpStatus.OK);
    }

    @GetMapping("/filters/{subCategory}")
    public ResponseEntity<List<FilterDTO>> getFiltersForSubcategory(@PathVariable("subCategory") String subCategory) {
        return new ResponseEntity<>(productsService.getFiltersForSubcategory(subCategory), HttpStatus.OK);
    }

    @GetMapping("/{subCategory}")
    public ResponseEntity<Page<ProductDTO3>> getFilteredProducts(@NotBlank @PathVariable("subCategory") String category, @RequestParam Map<String, String> allFilters, @RequestParam(name = "from", required = false) BigDecimal from, @RequestParam(name = "to", required = false) BigDecimal to) {
        return new ResponseEntity<>(productsService.getFilteredProducts(category, allFilters, from, to), HttpStatus.OK);
    }

    @GetMapping("/discounted")
    public ResponseEntity<Page<ProductDTO3>> getDiscountedProducts(@RequestParam(defaultValue = "0") Integer pageNum) {
        return new ResponseEntity<>(productsService.getDiscountedProducts(pageNum), HttpStatus.OK);
    }

    @GetMapping("/subCategories")
    public ResponseEntity<List<SubCategoryDTO>> getSubCategories() {
        return new ResponseEntity<>(productsService.getSubCategories(), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<ProductDTO3>> getProductsByQuery(@RequestParam String query, @RequestParam(defaultValue = "0") Integer page) {
        return new ResponseEntity<>(productsService.getFormattedProductsByQuery(query, page), HttpStatus.OK);
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<?> getProductPhoto(@PathVariable Long id, @RequestParam(required = false, defaultValue = "0") int index) {
        return productsService.getProductPhoto(id, index);
    }

    @GetMapping("/rating")
    public ResponseEntity<List<ProductDTO3>> getProductsSortedByRating() {
        return new ResponseEntity<>(productsService.getProductsSortedByRating(), HttpStatus.OK);
    }

    @GetMapping("/sales")
    public ResponseEntity<List<SubCategoryDTO2>> getProductsSortedBySales() {
        return new ResponseEntity<>(productsService.getProductsSortedBySales(), HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserPhoto(@PathVariable Long id) {
        return productsService.getUserPhoto(id);
    }

    @PostMapping("/validate-products")
    public List<OrderItemCreateDTO> getUser(@RequestBody List<OrderItemCreateDTO> products){
        return productsService.validateProducts(products);
    }

}
