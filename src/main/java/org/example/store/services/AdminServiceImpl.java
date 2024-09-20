package org.example.store.services;

import lombok.AllArgsConstructor;
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
import org.example.store.exception.company.CompanyNotFoundException;
import org.example.store.exception.filter.FilterNotFoundException;
import org.example.store.exception.filtervalue.FilterValueNotFoundException;
import org.example.store.exception.maincategory.MainCategoryNotFoundException;
import org.example.store.exception.order.OrderNotFoundException;
import org.example.store.exception.product.ProductNotFoundException;
import org.example.store.exception.subcategory.SubcategoryNotFoundException;
import org.example.store.exception.user.UserNotFoundException;
import org.example.store.mapper.OrderMapper;
import org.example.store.mapper.ProductMapper;
import org.example.store.model.*;
import org.example.store.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final MainCategoryRepository mainCategoryRepository;
    private final FilterRepository filterRepository;
    private final FilterValueRepository filterValueRepository;
    private final ProductMapper productMapper;
    private final SubCategoryRepository subCategoryRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemRepository orderItemRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    @Override
    public List<MainCategoryAdminDTO> getCategories() {
        return mainCategoryRepository.findAll().stream().map(productMapper::mapMainCategoryToMainCategoryAdminDTO).toList();
    }

    @Override
    public void createCategory(String category) {
        mainCategoryRepository.save(new MainCategory(category));
    }

    @Override
    public void createSubCategory(SubCategoryCreateDTO subCategoryCreateDTO) {
        MainCategory mainCategory = mainCategoryRepository.findById(subCategoryCreateDTO.mainCategoryId()).orElseThrow(() -> new MainCategoryNotFoundException(subCategoryCreateDTO.mainCategoryId()));
        SubCategory subCategory1 = new SubCategory(subCategoryCreateDTO.name());
        subCategoryRepository.save(subCategory1);
        mainCategory.getSubCategories().add(subCategory1);
        mainCategoryRepository.save(mainCategory);
    }

    @Override
    public void deleteFilter(Long id) {
        filterRepository.deleteById(id);
    }

    @Override
    public void deleteFilterValue(Long id) {
        FilterValue filterValue = filterValueRepository.findById(id).orElseThrow(() -> new FilterValueNotFoundException(id));
        filterValueRepository.delete(filterValue);
    }

    @Override
    public FilterValueDTO createFilterValue(FilterValueCreateDTO filterValueCreateDTO) {
        Filter filter = filterRepository.findById(filterValueCreateDTO.filterId()).orElseThrow(() -> new FilterNotFoundException(filterValueCreateDTO.filterId()));
        FilterValue filterValue1 = new FilterValue(filterValueCreateDTO.name());
        filterValueRepository.save(filterValue1);

        filter.getValues().add(filterValue1);
        filterRepository.save(filter);

        return productMapper.mapFilterValueToFilterValueDTO(filterValue1);
    }

    @Override
    public FilterDTO createFilter(FilterCreateDTO filterCreateDTO) {
        SubCategory subCategory = subCategoryRepository.findById(filterCreateDTO.subCategoryId()).orElseThrow(() -> new SubcategoryNotFoundException(filterCreateDTO.subCategoryId()));
        Filter filter = new Filter(filterCreateDTO.name());
        filterRepository.save(filter);

        subCategory.getFilters().add(filter);
        subCategoryRepository.save(subCategory);

        return productMapper.mapFilterToFilterDTO(filter);
    }

    @Override
    public void deleteCategoryById(Long id) {
        MainCategory category = mainCategoryRepository.findById(id).orElseThrow(() -> new MainCategoryNotFoundException(id));
        category.setDeleted(LocalDateTime.now());
        category.getSubCategories().forEach(subCategory -> deleteSubCategoryById(subCategory.getId()));
        mainCategoryRepository.save(category);
    }

    @Override
    public void deleteSubCategoryById(Long id) {
        SubCategory subCategory = subCategoryRepository.findById(id).orElseThrow(() -> new SubcategoryNotFoundException(id));
        subCategory.setDeleted(LocalDateTime.now());
        List<Product> products = subCategory.getProducts();
        products.forEach(product -> product.setRetired(LocalDateTime.now()));
        productRepository.saveAll(products);
        subCategoryRepository.save(subCategory);
    }

    @Override
    public List<ProductAdminDTO> getProducts() {
        return productRepository.findAll().stream().map(productMapper::mapProductToProductAdminDTO).toList();
    }

    @Override
    public List<CategoryProductsQuantity> getAmountOfProductsByCategory() {
        List<SubCategory> subCategories = subCategoryRepository.findAll();
        return subCategories.stream().map(subCategory -> new CategoryProductsQuantity(subCategory.getName(), subCategory.getProducts().stream().filter(product -> product.getRetired() == null).toList().size())).toList();
    }

    @Override
    public List<CategoryProductsQuantity> getAmountOfProducts() {
        return mainCategoryRepository.findMainCategoriesByDeletedIsNull().stream()
                .map(category -> new CategoryProductsQuantity(category.getName(), category.getSubCategories().stream()
                        .filter(subCategory -> subCategory.getDeleted() == null)
                        .mapToInt(subcategory -> subcategory.getProducts().stream()
                                .filter(product -> product.getRetired() == null).toList().size()).sum())).toList();
    }

    @Override
    public List<CategoryProductsQuantity> getSalesBySubCategory() {
        List<SubCategory> subCategories = subCategoryRepository.findAll();
        return subCategories.stream().map(subCategory -> new CategoryProductsQuantity(subCategory.getName(), subCategory.getProducts().stream().mapToInt(Product::getSales).sum())).toList();
    }

    @Override
    public List<AmountDate> getProductsInTime() {
        List<Product> products = productRepository.findAll();

        return products.stream()
                .map(product -> product.getCreated().toLocalDate())
                .collect(Collectors.groupingBy(date -> date, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new AmountDate(Integer.parseInt(String.valueOf(entry.getValue())), entry.getKey()))
                .toList();
    }

    @Override
    public List<AmountDate> getOrdersInTime() {
        List<Order> orders = orderRepository.findAll();

        return orders.stream()
                .map(product -> product.getStatuses().stream().filter(status -> status.getStatus().equals(Status.IN_PROGRESS)).findFirst().orElseThrow().getTime())
                .collect(Collectors.groupingBy(date -> date, Collectors.counting()))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new AmountDate(Integer.parseInt(String.valueOf(entry.getValue())), entry.getKey().toLocalDate()))
                .toList();
    }

    @Override
    public List<OrderAdminShortDTO> getOrders() {
        return orderRepository.findAll().stream().map(orderMapper::mapOrderToOrderAdminShortDTO).toList();
    }

    @Override
    public OrderAdminLongDTO getOrderById(Long id) {
        return orderMapper.mapOrderToOrderAdminLongDTO(orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id)));
    }

    @Override
    public AdminStats getStats(LocalDate startDate, LocalDate endDate) {
        int amountOfNewProducts = productRepository.getAmountOfNewProducts(startDate.atStartOfDay(), endDate.atStartOfDay());
        int amountOfNewOrders = orderRepository.getQuantityOfUncompletedOrders(startDate.atStartOfDay(), endDate.atStartOfDay());
        BigDecimal moneyTurnoverInTime = orderRepository.getMoneyTurnoverInTime(startDate.atStartOfDay(), endDate.atStartOfDay());
        int amountOfSoldProducts = orderRepository.getAmountOfSoldProducts(startDate.atStartOfDay(), endDate.atStartOfDay());
        return new AdminStats(
                amountOfNewProducts,
                amountOfNewOrders,
                moneyTurnoverInTime,
                amountOfSoldProducts
        );
    }

    @Override
    public ProductAdminStatsDTO getProductStats(Long productId) {
        return productMapper.mapProductToProductAdminStatsDTO(productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId)));
    }

    @Override
    public List<CompanyLongDTO> getCompanies() {
        return companyRepository.findAll().stream().map(orderMapper::mapCompanyIdToCompanyLongDTO).toList();
    }

    @Override
    public UserAdminStatsDTO getUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        List<Order> orders = userRepository.findOrdersByUser(user);

        return new UserAdminStatsDTO(
                userId,
                user.getFirst_name(),
                user.getLast_name(),
                user.getUser_name(),
                user.getCreated(),
                user.getBanned(),
                orders.stream().map(orderMapper::mapOrderToOrderUserAdminDTO).toList(),
                orders.stream()
                        .map(this::getLatestInProgressStatusTime)
                        .max(LocalDateTime::compareTo)
                        .orElse(null),
                orderRepository.getOrdersByUserIdAndStatus(user, "COMPLETED").stream().flatMap(order -> order.getOrderItem().stream()).map(orderMapper::mapOrderItemToOrderItemAdminDTO2).toList(),
                user.isPhoto()
        );
    }

    @Override
    public List<UserAdminDTO> getUsers() {
        return userRepository.findAll().stream().filter(user -> user.getRoles().stream().noneMatch(role -> Objects.equals(role.getName(), "admin"))).map(orderMapper::mapUserDtoToUserAdminDTO).toList();
    }

    @Override
    public List<AmountDate> getSalesInTime() {
        List<OrderItem> orderItems = orderItemRepository.findAll();

        return orderItems.stream()
                .map(orderItem -> {
                    LocalDateTime time = orderItem.getStatuses().stream().filter(status -> status.getStatus().equals(Status.IN_PROGRESS)).findFirst().get().getTime();
                    int quantity = orderItem.getQuantity();
                    return new AmountDate(quantity, time.toLocalDate());
                })
                .collect(Collectors.groupingBy(AmountDate::date, Collectors.summingInt(AmountDate::amount)))
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> new AmountDate(Integer.parseInt(String.valueOf(entry.getValue())), entry.getKey()))
                .toList();
    }

    @Override
    public void retireProductsByUserId(Long userId) {
        productRepository.retireProductsByCompanyId(userId);
    }

    @Override
    public void retireProductById(Long productId) {
        productRepository.retireProductById(productId);
    }

    @Override
    public void banCompanyById(Long companyId) {
        Company company = companyRepository.findById(companyId).orElseThrow(() -> new CompanyNotFoundException(companyId));
        company.setBanned(LocalDateTime.now());
        companyRepository.save(company);
        productRepository.retireProductsByCompanyId(companyId);
    }

    @Override
    public void banUserById(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        if(user.getCompany() != null){
            banCompanyById(user.getCompany().getId());
        }
        user.setBanned(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public Map<LocalDate, Integer> getUsersInTime() {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> user.getCreated().toLocalDate())
                .collect(Collectors.groupingBy(date -> date, Collectors.summingInt(e -> 1)));
    }

    public LocalDateTime getLatestInProgressStatusTime(Order order) {
        Optional<LocalDateTime> latestInProgressTime = order.getStatuses().stream()
                .filter(orderStatus -> Status.IN_PROGRESS.equals(orderStatus.getStatus()))
                .map(OrderStatus::getTime)
                .max(LocalDateTime::compareTo);

        return latestInProgressTime.orElse(null);
    }


}
