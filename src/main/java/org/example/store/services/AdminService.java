package org.example.store.services;

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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface AdminService {

    List<MainCategoryAdminDTO> getCategories();

    void createCategory(String category);

    void createSubCategory(SubCategoryCreateDTO subCategoryCreateDTO);

    void deleteFilter(Long id);

    void deleteFilterValue(Long id);

    FilterValueDTO createFilterValue(FilterValueCreateDTO filterValueCreateDTO);

    FilterDTO createFilter(FilterCreateDTO filterCreateDTO);

    void deleteCategoryById(Long id);

    void deleteSubCategoryById(Long id);

    List<ProductAdminDTO> getProducts();

    List<CategoryProductsQuantity> getAmountOfProductsByCategory();

    List<CategoryProductsQuantity> getAmountOfProducts();

    List<CategoryProductsQuantity> getSalesBySubCategory();

    List<AmountDate> getProductsInTime();

    List<AmountDate> getOrdersInTime();

    List<OrderAdminShortDTO> getOrders();

    OrderAdminLongDTO getOrderById(Long id);

    AdminStats getStats(LocalDate startDate, LocalDate endDate);

    ProductAdminStatsDTO getProductStats(Long productId);

    List<CompanyLongDTO> getCompanies();

    UserAdminStatsDTO getUser(Long userId);

    List<UserAdminDTO> getUsers();

    List<AmountDate> getSalesInTime();

    void retireProductsByUserId(Long userId);

    void retireProductById(Long productId);

    void banCompanyById(Long companyId);

    void banUserById(Long userId);

    Map<LocalDate, Integer> getUsersInTime();

}
