package org.example.store.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
import org.example.store.services.CompanyService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "/company")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @GetMapping("/products")
    public ResponseEntity<List<ProductCompanyDTO>> getProducts(Principal principal) {
        return new ResponseEntity<>(companyService.getProducts(principal), HttpStatus.OK);
    }

    @GetMapping("/product")
    public ResponseEntity<ProductDTO2> getProductById(Principal principal, @Min(1) @RequestParam Long productId) {
        return new ResponseEntity<>(companyService.getProductById(principal, productId), HttpStatus.OK);
    }

    @PutMapping("/product")
    public ResponseEntity<Void> updateProduct(Principal principal, @Valid @RequestBody ProductUpdateDTO productUpdateDto) {
        companyService.updateProduct(principal, productUpdateDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/product")
    public ResponseEntity<Long> createProduct(Principal principal, @Valid @RequestBody ProductCreateDTO productDto) {
        return new ResponseEntity<>(companyService.createProduct(principal, productDto), HttpStatus.OK);
    }

    @PostMapping("/product/images")
    public ResponseEntity<Void> addImagesToProduct(Principal principal, @Min(1) @RequestParam("productId") Long productId, @RequestParam("file") MultipartFile[] files) {
        companyService.addImagesToProduct(principal, productId, files);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderCompanyShortDTO>> getOrders(Principal principal) {
        return new ResponseEntity<>(companyService.getOrders(principal), HttpStatus.OK);
    }

    @GetMapping("/order")
    public ResponseEntity<OrderCompanyLongDTO> getOrderById(Principal principal, @Min(1) @RequestParam Long orderId) {
        return new ResponseEntity<>(companyService.getOrderById(principal, orderId), HttpStatus.OK);
    }

    @PutMapping("/order/status")
    public ResponseEntity<Void> updateOrderStatus(Principal principal, @Valid @RequestBody OrderStatusUpdateDTO orderStatusUpdateDTO) {
        companyService.updateOrderStatus(principal, orderStatusUpdateDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/stats")
    public ResponseEntity<CompanyStats> getCompanyStats(Principal principal, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        return new ResponseEntity<>(companyService.getCompanyStats(principal, startDate.atStartOfDay()), HttpStatus.OK);
    }

    @GetMapping("/product/stats")
    public ResponseEntity<CompanyProductStats> getProductStats(Principal principal) {
        return new ResponseEntity<>(companyService.getProductStats(principal), HttpStatus.OK);
    }

    @PutMapping("/product/retire")
    public ResponseEntity<Void> retireProduct(Principal principal, @Min(1) @RequestParam Long productId) {
        companyService.retireProduct(principal, productId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/sales/category")
    public ResponseEntity<Map<String, Integer>> getSalesBySubCategories(Principal principal, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        return new ResponseEntity<>(companyService.getSalesBySubCategories(principal, startDate.atStartOfDay()), HttpStatus.OK);
    }

    @GetMapping("/orders/time")
    public ResponseEntity<List<AmountDate>> getOrdersInTime(Principal principal, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        return new ResponseEntity<>(companyService.getOrdersInTime(principal, startDate.atStartOfDay()), HttpStatus.OK);
    }

    @GetMapping("/income/country")
    public ResponseEntity<List<CountryDTO>> getIncomeByCountry(Principal principal) {
        return new ResponseEntity<>(companyService.getIncomeByCountry(principal), HttpStatus.OK);
    }

    @GetMapping("/income/user")
    public ResponseEntity<List<UserCompanyDTO>> getIncomeByUser(Principal principal) {
        return new ResponseEntity<>(companyService.getIncomeByUser(principal), HttpStatus.OK);
    }

    @GetMapping("/income/status")
    public ResponseEntity<List<CompanyIncomeStatusDTO>> getIncomeByStatus(Principal principal) {
        return new ResponseEntity<>(companyService.getIncomeByStatus(principal), HttpStatus.OK);
    }

    @GetMapping("/income/time")
    public ResponseEntity<Map<LocalDate, BigDecimal>> getIncomeInTime(Principal principal, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        return new ResponseEntity<>(companyService.getIncomeInTime(principal, startDate.atStartOfDay()), HttpStatus.OK);
    }

    @GetMapping("/income/change")
    public ResponseEntity<Map<String, Double>> getIncomeChange(Principal principal) {
        return new ResponseEntity<>(companyService.getIncomeChange(principal), HttpStatus.OK);
    }

    @GetMapping("/income/products")
    public ResponseEntity<List<ProductIncomeDTO>> getIncomeByProducts(Principal principal) {
        return new ResponseEntity<>(companyService.getIncomeByProducts(principal), HttpStatus.OK);
    }

    @GetMapping("/updates")
    public ResponseEntity<List<OrderCompanyUpdateDTO>> getUpdates(Principal principal) {
        return new ResponseEntity<>(companyService.getUpdates(principal), HttpStatus.OK);
    }

    @GetMapping("/new-customers")
    public ResponseEntity<CompanyStatsNewCustomers> getCompanyNewCustomers(Principal principal, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate) {
        return new ResponseEntity<>(companyService.getCompanyNewCustomers(principal, startDate), HttpStatus.OK);
    }
}