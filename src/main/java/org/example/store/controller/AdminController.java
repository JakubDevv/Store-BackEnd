package org.example.store.controller;

import jakarta.validation.Valid;
import org.example.store.dto.category.MainCategoryAdminDTO;
import org.example.store.dto.category.SubCategoryCreateDTO;
import org.example.store.dto.filters.FilterCreateDTO;
import org.example.store.dto.filters.FilterDTO;
import org.example.store.dto.filters.FilterValueCreateDTO;
import org.example.store.dto.filters.FilterValueDTO;
import org.example.store.dto.order.OrderAdminLongDTO;
import org.example.store.dto.order.OrderAdminShortDTO;
import org.example.store.dto.product.ProductAdminDTO;
import org.example.store.dto.product.ProductAdminStatsDTO;
import org.example.store.dto.stats.AdminStats;
import org.example.store.dto.stats.AmountDate;
import org.example.store.dto.stats.CategoryProductsQuantity;
import org.example.store.dto.user.CompanyLongDTO;
import org.example.store.dto.user.UserAdminDTO;
import org.example.store.dto.user.UserAdminStatsDTO;
import org.example.store.services.AdminService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/categories")
    public ResponseEntity<List<MainCategoryAdminDTO>> getCategories() {
        return new ResponseEntity<>(adminService.getCategories(), HttpStatus.OK);
    }

    @PostMapping("/category")
    public ResponseEntity<Void> createCategory(@RequestParam String categoryName) {
        adminService.createCategory(categoryName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/subcategory")
    public ResponseEntity<Void> createSubCategory(@Valid @RequestBody SubCategoryCreateDTO subCategoryCreateDTO) {
        adminService.createSubCategory(subCategoryCreateDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/filter/value/{id}")
    public ResponseEntity<Void> deleteFilterValue(@PathVariable Long id) {
        adminService.deleteFilterValue(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/filter/{id}")
    public ResponseEntity<Void> deleteFilter(@PathVariable Long id) {
        adminService.deleteFilter(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/filter")
    public ResponseEntity<FilterDTO> createFilter(@Valid @RequestBody FilterCreateDTO filterCreateDTO) {
        return new ResponseEntity<>(adminService.createFilter(filterCreateDTO), HttpStatus.CREATED);
    }

    @PostMapping("/filterValue")
    public ResponseEntity<FilterValueDTO> createFilterValue(@Valid @RequestBody FilterValueCreateDTO filterValueCreateDTO) {
        return new ResponseEntity<>(adminService.createFilterValue(filterValueCreateDTO), HttpStatus.CREATED);
    }

    @DeleteMapping("/category/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        adminService.deleteCategoryById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/subcategory/{id}")
    public ResponseEntity<Void> deleteSubCategory(@PathVariable Long id) {
        adminService.deleteSubCategoryById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductAdminDTO>> getProducts() {
        return new ResponseEntity<>(adminService.getProducts(), HttpStatus.OK);
    }

    @GetMapping("/category/products")
    public ResponseEntity<List<CategoryProductsQuantity>> getAmountOfProductsByCategory() {
        return new ResponseEntity<>(adminService.getAmountOfProducts(), HttpStatus.OK);
    }

    @GetMapping("/subcategory/products")
    public ResponseEntity<List<CategoryProductsQuantity>> getAmountOfProductsBySubCategory() {
        return new ResponseEntity<>(adminService.getAmountOfProductsByCategory(), HttpStatus.OK);
    }

    @GetMapping("/category/sales")
    public ResponseEntity<List<CategoryProductsQuantity>> getSalesBySubCategory() {
        return new ResponseEntity<>(adminService.getSalesBySubCategory(), HttpStatus.OK);
    }

    @GetMapping("/products/time")
    public ResponseEntity<List<AmountDate>> getProductsInTime() {
        return new ResponseEntity<>(adminService.getProductsInTime(), HttpStatus.OK);
    }

    @GetMapping("/orders/time")
    public ResponseEntity<List<AmountDate>> getOrdersInTime() {
        return new ResponseEntity<>(adminService.getOrdersInTime(), HttpStatus.OK);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderAdminShortDTO>> getOrders() {
        return new ResponseEntity<>(adminService.getOrders(), HttpStatus.OK);
    }

    @GetMapping("/order/{id}")
    public ResponseEntity<OrderAdminLongDTO> getOrderById(@PathVariable Long id) {
        return new ResponseEntity<>(adminService.getOrderById(id), HttpStatus.OK);
    }

    @GetMapping("/stats")
    public ResponseEntity<AdminStats> getStats(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return new ResponseEntity<>(adminService.getStats(startDate, endDate), HttpStatus.OK);
    }

    @GetMapping("/product/{id}")
    public ResponseEntity<ProductAdminStatsDTO> getProductStats(@PathVariable Long id) {
        return new ResponseEntity<>(adminService.getProductStats(id), HttpStatus.OK);
    }

    @GetMapping("/companies")
    public ResponseEntity<List<CompanyLongDTO>> getCompanies() {
        return new ResponseEntity<>(adminService.getCompanies(), HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserAdminStatsDTO> getUser(@PathVariable Long id) {
        return new ResponseEntity<>(adminService.getUser(id), HttpStatus.OK);
    }

    @GetMapping("/users")
    public ResponseEntity<List<UserAdminDTO>> getUsers() {
        return new ResponseEntity<>(adminService.getUsers(), HttpStatus.OK);
    }

    @GetMapping("/sales/time")
    public ResponseEntity<List<AmountDate>> getSalesInTime() {
        return new ResponseEntity<>(adminService.getSalesInTime(), HttpStatus.OK);
    }

    @PutMapping("/products/retire")
    public ResponseEntity<Void> retireProductsByUserId(@RequestParam Long userId) {
        adminService.retireProductsByUserId(userId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/product/{id}/retire")
    public ResponseEntity<Void> retireProductById(@PathVariable Long id) {
        adminService.retireProductById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/users/time")
    public ResponseEntity<Map<LocalDate, Integer>> getUsersInTime() {
        return new ResponseEntity<>(adminService.getUsersInTime(), HttpStatus.OK);
    }

    @DeleteMapping("/user/{id}")
    public ResponseEntity<Void> banUserById(@PathVariable Long id) {
        adminService.banUserById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping("/company/{id}")
    public ResponseEntity<Void> banCompanyById(@PathVariable Long id) {
        adminService.banCompanyById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
