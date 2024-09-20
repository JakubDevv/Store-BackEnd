package org.example.store.services;

import lombok.RequiredArgsConstructor;
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
import org.example.store.exception.order.OrderNotFoundException;
import org.example.store.exception.product.ProductNotFoundException;
import org.example.store.exception.subcategory.SubcategoryNotFoundException;
import org.example.store.exception.user.UserNotFoundException;
import org.example.store.mapper.OrderMapper;
import org.example.store.mapper.ProductMapper;
import org.example.store.model.*;
import org.example.store.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final ProductRepository productRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final ParametersRepository parametersRepository;
    private final SizesRepository sizesRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ImageService imageService;
    private final ProductMapper productMapper;
    private final OrderMapper orderMapper;
    private final OrderStatusRepository orderStatusRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;

    @Override
    public ProductDTO2 getProductById(Principal principal, Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
        return productMapper.mapProductToProductDTO2(product);
    }

    @Override
    public void updateProduct(Principal principal, ProductUpdateDTO productUpdateDto) {
        Product product = productRepository.getProductByIdAndCompanyId(productUpdateDto.id(), principal.getName()).orElseThrow(() -> new ProductNotFoundException(productUpdateDto.id()));
        List<Size> newSizes = productUpdateDto.sizes().stream().map(productMapper::mapSizeDTOToSize).collect(Collectors.toList());
        List<Parameter> newParameters = productUpdateDto.parameters().stream().map(productMapper::mapParameterDTOToParameter).collect(Collectors.toList());
        parametersRepository.saveAll(newParameters);
        sizesRepository.saveAll(newSizes);
        product.setSizes(newSizes);
        product.setParameters(newParameters);
        product.setName(productUpdateDto.title());
        product.setDescription(productUpdateDto.description());
        product.setPrice(productUpdateDto.price());
        product.setDiscount_price(productUpdateDto.discountPrice());
        productRepository.save(product);
    }

    @Override
    public Long createProduct(Principal principal, ProductCreateDTO productCreateDTO) {
        SubCategory subCategory = subCategoryRepository.findById(productCreateDTO.subcategoryId()).orElseThrow(() -> new SubcategoryNotFoundException(productCreateDTO.subcategoryId()));
        List<Size> sizes = productCreateDTO.sizes().stream().map(productMapper::mapSizeDTOToSize).toList();
        List<Parameter> parameters = productCreateDTO.parameters().stream().map(productMapper::mapParameterDTOToParameter).toList();
        sizesRepository.saveAll(sizes);
        parametersRepository.saveAll(parameters);
        User user = userRepository.findUserByUser_name(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        Product product = new Product(productCreateDTO.title(), productCreateDTO.description(), productCreateDTO.price(), sizes, parameters);
        subCategory.addProduct(product);
        product.setCompany(user.getCompany());
        Product save = productRepository.save(product);
        subCategoryRepository.save(subCategory);
        return save.getId();
    }

    @Override
    public void addImagesToProduct(Principal principal, Long productId, MultipartFile[] files) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
        imageService.deleteImagesByProduct(product);
        imageService.addMultipleFiles(files, product);
    }

    @Override
    public List<OrderCompanyShortDTO> getOrders(Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        List<Order> orders = orderRepository.getOrdersByCompany(user.getCompany().getId());
        return orders.stream().map(order -> orderMapper.mapOrderToOrderCompanyShortDTO(order, principal)).toList();
    }

    @Override
    public OrderCompanyLongDTO getOrderById(Principal principal, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        return orderMapper.mapOrderToOrderCompanyLongDTO(order, principal);
    }

    @Override
    public void updateOrderStatus(Principal principal, OrderStatusUpdateDTO orderStatusUpdateDTO) {
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        Order order = orderRepository.findById(orderStatusUpdateDTO.orderId()).orElseThrow(() -> new OrderNotFoundException(orderStatusUpdateDTO.orderId()));
        order.getOrderItem().stream()
                .filter(item -> Objects.equals(userRepository.findUserByCompany(companyRepository.findCompanyByOrderItem(item)).getId(), user.getId()))
                .forEach(item -> {
                    OrderStatus orderStatus = new OrderStatus(orderStatusUpdateDTO.status());
                    orderStatusRepository.save(orderStatus);
                    item.getStatuses().add(orderStatus);
                });
        if (order.getOrderItem().stream().allMatch(item -> item.getStatuses().stream()
                .anyMatch(status -> status.getStatus().equals(Status.SENT)))) {
            OrderStatus orderStatus = new OrderStatus(Status.SENT);
            orderStatusRepository.save(orderStatus);
            order.getStatuses().add(orderStatus);
        }
        orderItemRepository.saveAll(order.getOrderItem());
        orderRepository.save(order);
    }


    @Override
    public CompanyStats getCompanyStats(Principal principal, LocalDateTime endDate) {
        User user = userRepository.findUserByName2(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        Company company = user.getCompany();
        LocalDateTime now = LocalDateTime.now();
        long betweenDays = ChronoUnit.DAYS.between(now, endDate);
        LocalDateTime secondDate = endDate.plusDays(betweenDays);

        List<OrderItem> items = orderRepository.getOrdersByCompanyIdAndEndDate(company.getProducts(), endDate).stream().flatMap(order -> order.getOrderItem().stream()).filter(item -> Objects.equals(companyRepository.findCompanyByOrderItem(item).getId(), company.getId())).toList();
        List<OrderItem> items2 = orderRepository.getOrdersByCompanyIdAndStartDateAndEndDate(company.getProducts(), endDate, secondDate).stream().flatMap(order -> order.getOrderItem().stream()).filter(item -> Objects.equals(companyRepository.findCompanyByOrderItem(item).getId(), company.getId())).toList();

        BigDecimal totalAmount = items.stream().map(orderItem -> orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAmount2 = items2.stream().map(orderItem -> orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);

        double multiply = totalAmount2.equals(BigDecimal.ZERO) ? 0 : ((totalAmount.doubleValue() - totalAmount2.doubleValue()) / totalAmount2.doubleValue()) * 100;

        int totalQuantity = items.stream().mapToInt(OrderItem::getQuantity).sum();
        int totalQuantity2 = items2.stream().mapToInt(OrderItem::getQuantity).sum();

        List<ProductReview> reviewCount = productRepository.findProductReviewsByCompanyIdAndTime(company, endDate);
        List<ProductReview> reviewCount2 = productRepository.findProductReviewsByCompanyIdAndTime(company, secondDate, endDate);

        int orderCount = orderRepository.getOrdersByCompanyIdAndEndDate(company.getProducts(), endDate).size();
        int orderCount2 = orderRepository.getOrdersByCompanyIdAndStartDateAndEndDate(company.getProducts(), endDate, secondDate).size();

        return new CompanyStats(orderCount,
                totalAmount,
                totalQuantity,
                reviewCount.size(),
                reviewCount.size() - reviewCount2.size(),
                totalQuantity - totalQuantity2,
                multiply,
                orderCount - orderCount2);
    }

    @Override
    public void retireProduct(Principal principal, Long productId) {
        User user = userRepository.findUserByName2(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException(productId));
        Company company = companyRepository.findCompanyByProduct(product);
        if (Objects.equals(company.getId(), user.getCompany().getId())) {
            productRepository.retireProductById(productId);
        }
    }

    @Override
    public Map<String, Integer> getSalesBySubCategories(Principal principal, LocalDateTime startDate) {
        User user = userRepository.findUserByName2(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        List<Order> orders = orderRepository.getOrdersByCompanyIdAndEndDate(user.getCompany().getProducts(), startDate);

        return orders.stream()
                .flatMap(order -> order.getOrderItem().stream())
                .collect(Collectors.toMap(
                        item -> subCategoryRepository.findSubCategoryByProductsContaining(item.getProduct()).orElseThrow(SubcategoryNotFoundException::new).getName(),
                        OrderItem::getQuantity,
                        Integer::sum));
    }

    @Override
    public List<AmountDate> getOrdersInTime(Principal principal, LocalDateTime localDateTime) {
        User user = userRepository.findUserByName2(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        List<Order> ordersBySellerId3 = orderRepository.getOrdersByCompanyIdAndEndDate(user.getCompany().getProducts(), localDateTime);
        return ordersBySellerId3.stream().map(order -> new AmountDate(1, order.getStatuses().stream().filter(status -> status.getStatus().equals(Status.IN_PROGRESS)).toList().get(0).getTime().toLocalDate())).toList();
    }

    @Override
    public Map<LocalDate, BigDecimal> getIncomeInTime(Principal principal, LocalDateTime localDateTime) {
        User user = userRepository.findUserByName2(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        return orderRepository.getOrdersByCompanyIdAndEndDate(user.getCompany().getProducts(), localDateTime)
                .stream()
                .flatMap(order -> order.getOrderItem().stream())
                .collect(Collectors.groupingBy(
                        item -> item.getStatuses()
                                .stream()
                                .filter(status -> status.getStatus().equals(Status.IN_PROGRESS))
                                .findFirst()
                                .orElseThrow(() -> new IllegalStateException("No IN_PROGRESS status found"))
                                .getTime()
                                .toLocalDate(),
                        Collectors.mapping(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())), Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
    }

    @Override
    public List<ProductCompanyDTO> getProducts(Principal principal) {
        User user = userRepository.findUserByName2(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        List<Product> products = companyRepository.findProductsByCompany(user.getCompany());
        return products.stream().map(product -> {
            SubCategory subCategory = subCategoryRepository.getSubCategoryByProductsContaining(product);
            List<OrderItem> items = orderItemRepository.findOrderItemsByProduct(product);
            int sales = items.stream().mapToInt(OrderItem::getQuantity).sum();
            BigDecimal income = items.stream().map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
            int sum = product.getSizes().stream().mapToInt(Size::getQuantity).sum();
            return new ProductCompanyDTO(product.getId(), product.getCreated(), product.getName(), subCategory.getName(), subCategory.getDeleted() != null, sales, sum, product.getPrice(), product.getDiscount_price(), income, product.getRetired() == null);
        }).toList();
    }

    @Override
    public CompanyProductStats getProductStats(Principal principal) {
        User user = userRepository.findUserByName2(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        List<OrderItem> orderItems = companyRepository.findOrderItemsByCompany(user.getCompany());
        BigDecimal overallSpend = orderItems.stream().map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<Order> orders = orderRepository.getOrdersByCompany(user.getCompany().getId());
        Set<Long> clients = orders.stream()
                .map(order -> userRepository.findUserByOrder(order).getId())
                .collect(Collectors.toSet());

        return new CompanyProductStats(overallSpend,
                productRepository.getAmountOfActiveProducts(user.getCompany()),
                productRepository.getAmountOfRetiredProducts(user.getCompany()),
                clients.size(),
                productRepository.getRatingByCompanyId(user.getCompany()),
                productRepository.getValueOfStoredProductsByCompanyId(user.getCompany()),
                subCategoryRepository.getValueOfStoredProductsByCompanyId(user.getCompany().getProducts()));
    }

    @Override
    public List<CountryDTO> getIncomeByCountry(Principal principal) {
        User user = userRepository.findUserByName2(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        return orderRepository.getCountryIncomeByCompanyId(user.getCompany().getId());
    }

    @Override
    public List<UserCompanyDTO> getIncomeByUser(Principal principal) {
        User user = userRepository.findUserByName2(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        return orderRepository.getCustomersByCompanyId(user.getCompany().getId());
    }

    @Override
    public List<CompanyIncomeStatusDTO> getIncomeByStatus(Principal principal) {
        User user = userRepository.findUserByName2(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        BigDecimal COMPLETED = orderRepository.getCompanyIncomeByStatus(user.getCompany().getId(), Status.COMPLETED);
        BigDecimal SENT = orderRepository.getCompanyIncomeByStatus(user.getCompany().getId(), Status.SENT);
        BigDecimal IN_PROGRESS = orderRepository.getCompanyIncomeByStatus(user.getCompany().getId(), Status.IN_PROGRESS);
        return List.of(
                new CompanyIncomeStatusDTO(Status.COMPLETED, COMPLETED),
                new CompanyIncomeStatusDTO(Status.SENT, SENT.subtract(COMPLETED)),
                new CompanyIncomeStatusDTO(Status.IN_PROGRESS, IN_PROGRESS.subtract(SENT)));
    }

    @Override
    public Map<String, Double> getIncomeChange(Principal principal) {
        User user = userRepository.findUserByName2(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));

        BigDecimal totalAmount = orderItemRepository.getOrdersByCompanyIdAndEndDate(user.getCompany().getId(), LocalDateTime.now().minusDays(7));
        BigDecimal totalAmount2 = orderItemRepository.getOrdersByCompanyIdAndStartDateAndEndDate(user.getCompany().getId(), LocalDateTime.now().minusDays(7), LocalDateTime.now().minusDays(14));

        double multiply = totalAmount2 == null ? 0 : ((totalAmount.doubleValue() - totalAmount2.doubleValue()) / totalAmount2.doubleValue()) * 100;

        BigDecimal totalAmount3 = orderItemRepository.getOrdersByCompanyIdAndEndDate(user.getCompany().getId(), LocalDateTime.now().minusMonths(1));
        BigDecimal totalAmount4 = orderItemRepository.getOrdersByCompanyIdAndStartDateAndEndDate(user.getCompany().getId(), LocalDateTime.now().minusMonths(1), LocalDateTime.now().minusMonths(2));

        double multiply2 = totalAmount4 == null ? 0 : ((totalAmount3.doubleValue() - totalAmount4.doubleValue()) / totalAmount4.doubleValue()) * 100;

        BigDecimal totalAmount5 = orderItemRepository.getOrdersByCompanyIdAndEndDate(user.getCompany().getId(), LocalDateTime.now().minusYears(1));
        BigDecimal totalAmount6 = orderItemRepository.getOrdersByCompanyIdAndStartDateAndEndDate(user.getCompany().getId(), LocalDateTime.now().minusYears(1), LocalDateTime.now().minusYears(2));

        double multiply3 = totalAmount6 == null ? 0 : ((totalAmount5.doubleValue() - totalAmount6.doubleValue()) / totalAmount6.doubleValue()) * 100;

        return Map.ofEntries(Map.entry("week", multiply), Map.entry("month",multiply2), Map.entry("year",multiply3));
    }

    @Override
    public List<ProductIncomeDTO> getIncomeByProducts(Principal principal) {
        User user = userRepository.findUserByName2(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        return productRepository.getTop3ProductsCompanyId(user.getCompany().getId());
    }

    @Override
    public List<OrderCompanyUpdateDTO> getUpdates(Principal principal) {
        User user = userRepository.findUserByName2(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        List<OrderCompanyUpdateDTO> updates = new ArrayList<>();

        orderRepository.getOrdersByCompany(user.getCompany().getId()).forEach(order ->  {
            List<OrderItem> items = order.getOrderItem().stream().filter(item -> Objects.equals(userRepository.findUserByCompany(companyRepository.findCompanyByProduct(item.getProduct())).getUser_name(), user.getUser_name())).toList();
            BigDecimal orderPrice = items.stream().map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
            User userByOrder = userRepository.findUserByOrder(order);
            order.getOrderItem().get(0).getStatuses().forEach(status -> {
                updates.add(new OrderCompanyUpdateDTO(userByOrder.getId(), order.getId(), orderPrice, status.getTime(), userByOrder.getUser_name(), status.getStatus()));
            });
        });
        return updates.stream().sorted(Comparator.comparing(OrderCompanyUpdateDTO::date).reversed()).toList();
    }

    @Override
    public CompanyStatsNewCustomers getCompanyNewCustomers(Principal principal, LocalDate startDate) {
        User user = userRepository.findUserByName2(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        Company company = user.getCompany();
        return new CompanyStatsNewCustomers(companyRepository.getCompanyNewCountries(company, startDate.atStartOfDay()), companyRepository.getCompanyNewCustomers(company, startDate.atStartOfDay()));
    }

}
