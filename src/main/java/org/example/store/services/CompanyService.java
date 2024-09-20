package org.example.store.services;

import org.example.store.dto.CompanyIncomeStatusDTO;
import org.example.store.dto.CountryDTO;
import org.example.store.dto.ProductIncomeDTO;
import org.example.store.dto.order.OrderCompanyLongDTO;
import org.example.store.dto.order.OrderCompanyShortDTO;
import org.example.store.dto.order.OrderCompanyUpdateDTO;
import org.example.store.dto.order.OrderStatusUpdateDTO;
import org.example.store.dto.product.ProductCompanyDTO;
import org.example.store.dto.product.ProductCreateDTO;
import org.example.store.dto.product.ProductDTO2;
import org.example.store.dto.product.ProductUpdateDTO;
import org.example.store.dto.stats.AmountDate;
import org.example.store.dto.stats.CompanyProductStats;
import org.example.store.dto.stats.CompanyStats;
import org.example.store.dto.stats.CompanyStatsNewCustomers;
import org.example.store.dto.user.UserCompanyDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public interface CompanyService {

    ProductDTO2 getProductById(Principal principal, Long productId);

    void updateProduct(Principal principal, ProductUpdateDTO productUpdateDto);

    Long createProduct(Principal principal, ProductCreateDTO productDto);

    void addImagesToProduct(Principal principal, Long productId, MultipartFile[] files);

    List<OrderCompanyShortDTO> getOrders(Principal principal);

    OrderCompanyLongDTO getOrderById(Principal principal, Long orderId);

    void updateOrderStatus(Principal principal, OrderStatusUpdateDTO orderStatusUpdateDTO);

    CompanyStats getCompanyStats(Principal principal, LocalDateTime endDate);

    void retireProduct(Principal principal, Long productId);

    Map<String, Integer> getSalesBySubCategories(Principal principal, LocalDateTime startDate);

    List<AmountDate> getOrdersInTime(Principal principal, LocalDateTime localDateTime);

    Map<LocalDate, BigDecimal> getIncomeInTime(Principal principal, LocalDateTime localDateTime);

    List<ProductCompanyDTO> getProducts(Principal principal);

    CompanyProductStats getProductStats(Principal principal);

    List<CountryDTO> getIncomeByCountry(Principal principal);

    List<UserCompanyDTO> getIncomeByUser(Principal principal);

    List<CompanyIncomeStatusDTO> getIncomeByStatus(Principal principal);

    Map<String, Double> getIncomeChange(Principal principal);

    List<ProductIncomeDTO> getIncomeByProducts(Principal principal);

    List<OrderCompanyUpdateDTO> getUpdates(Principal principal);

    CompanyStatsNewCustomers getCompanyNewCustomers(Principal principal, LocalDate startDate);
}
