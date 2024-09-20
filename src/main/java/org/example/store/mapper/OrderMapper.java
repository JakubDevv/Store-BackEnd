package org.example.store.mapper;

import lombok.AllArgsConstructor;
import org.example.store.dto.category.CategoryExpenseDTO;
import org.example.store.dto.order.*;
import org.example.store.dto.product.ProductExpenseDTO;
import org.example.store.dto.user.CompanyItemsDTO;
import org.example.store.dto.user.CompanyLongDTO;
import org.example.store.dto.user.UserAdminDTO;
import org.example.store.exception.user.UserNotFoundException;
import org.example.store.model.*;
import org.example.store.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OrderMapper {

    private final OrderRepository orderRepository;
    private final OrderStatusRepository orderStatusRepository;
    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final SubCategoryRepository subCategoryRepository;

    public OrderUserDTO2 mapOrderToOrderUserDTO2(Order order) {
        Optional<OrderStatus> max = order.getStatuses().stream()
                .max(Comparator.comparing(OrderStatus::getTime));

        Optional<OrderStatus> second = order.getStatuses()
                .stream()
                .filter(status -> status.getStatus().equals(Status.COMPLETED)).findFirst();

        LocalDateTime completeDate = second.map(OrderStatus::getTime).orElse(null);

        Optional<OrderStatus> first = order.getStatuses()
                .stream()
                .filter(status -> status.getStatus().equals(Status.SENT)).findFirst();

        LocalDateTime sentDate = first.map(OrderStatus::getTime).orElse(null);

        return new OrderUserDTO2(
                order.getId(),
                order.getStatuses()
                        .stream()
                        .filter(status -> status.getStatus().equals(Status.IN_PROGRESS)).findFirst().orElseThrow().getTime(),
                order.getOrderItem().stream().map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add),
                order.getOrderItem().stream().map(item -> item.getProduct().getCompany().getName()).collect(Collectors.toSet()),
                order.getOrderItem().stream().map(item -> item.getProduct().getCompany().getId()).collect(Collectors.toSet()),
                max.get().getStatus().name(),
                order.getCountry(),
                order.getCity(),
                order.getStreet(),
                order.getHouse_number(),
                order.getZipcode(),
                sentDate,
                order.getOrderItem().stream().map(item -> item.getProduct().getId()).collect(Collectors.toSet()),
                order.getOrderItem().stream().map(this::mapOrderItemToOrderItemUserDTO).toList(),
                completeDate,
                order.getPhone()
        );
    }

    public OrderCompanyShortDTO mapOrderToOrderCompanyShortDTO(Order order, Principal principal) {
        User user = userRepository.findUserByOrder(order);
        User user2 = userRepository.findUserByUser_name(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));

        List<OrderItem> items = order.getOrderItem().stream()
                .filter(item -> Objects.equals(
                        userRepository.findUserByCompany(companyRepository.findCompanyByProduct(item.getProduct())).getUser_name(),
                        user2.getUser_name()))
                .toList();

        Map<Long, List<ProductExpenseDTO>> categoryToProductExpenses = new HashMap<>();
        Map<Long, String> categoryNames = new HashMap<>();

        for (OrderItem item : items) {
            Product product = item.getProduct();
            BigDecimal amount = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()));

            SubCategory subCategory = subCategoryRepository.getSubCategoryByProductsContaining(product);
            if (subCategory != null) {
                categoryNames.put(subCategory.getId(), subCategory.getName());

                List<ProductExpenseDTO> productExpenses = categoryToProductExpenses
                        .computeIfAbsent(subCategory.getId(), k -> new ArrayList<>());
                productExpenses.add(new ProductExpenseDTO(product.getId(), product.getName(), amount.intValue()));
            }
        }

        List<CategoryExpenseDTO> categoryExpenses = categoryToProductExpenses.entrySet().stream()
                .map(entry -> new CategoryExpenseDTO(
                        entry.getKey(),
                        categoryNames.get(entry.getKey()),
                        entry.getValue().stream()
                                .map(ProductExpenseDTO::amount)
                                .map(BigDecimal::valueOf)
                                .reduce(BigDecimal.ZERO, BigDecimal::add),
                        entry.getValue()))
                .collect(Collectors.toList());

        return new OrderCompanyShortDTO(
                order.getId(),
                user.isPhoto(),
                items.stream().mapToInt(OrderItem::getQuantity).sum(),
                order.getStatuses().stream()
                        .filter(status -> status.getStatus().equals(Status.IN_PROGRESS)).findFirst().orElseThrow().getTime(),
                items.stream().map(orderItem -> orderItem.getPrice().multiply(new BigDecimal(orderItem.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add),
                items.get(0).getStatuses().stream().max(Comparator.comparing(OrderStatus::getTime)).orElseThrow(() -> new NoSuchElementException("No status found")).getStatus(),
                user.getFirst_name(),
                user.getLast_name(),
                order.getCountry(),
                order.getCity(),
                order.getStreet(),
                order.getHouse_number(),
                items.stream().mapToInt(OrderItem::getQuantity).sum(),
                user.getId(),
                categoryExpenses
        );
    }

    public OrderItemDTO mapOrderItemToOrderItemDto(OrderItem orderItem) {
        return new OrderItemDTO(
                orderItem.getProduct().getId(),
                orderItem.getProduct().getName(),
                orderItem.getSize(),
                orderItem.getPrice(),
                orderItem.getQuantity(),
                orderItem.getProduct().getId(),
                orderItem.getProduct().getParameters().stream().map(Parameter::getValue).toList()
        );
    }


    public OrderAdminShortDTO mapOrderToOrderAdminShortDTO(Order order) {
        User user = userRepository.findUserByOrder(order);

        return new OrderAdminShortDTO(
                order.getId(),
                user.getUser_name(),
                order.getOrderItem().stream().map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add),
                order.getStatuses()
                        .stream()
                        .filter(status -> status.getStatus().equals(Status.IN_PROGRESS)).findFirst().orElseThrow().getTime(),
                order.getStatuses()
                        .stream()
                        .max(Comparator.comparing(OrderStatus::getStatus)).orElseThrow(() -> new NoSuchElementException("No status found")).getStatus(),
                order.getOrderItem().stream().map(item -> companyRepository.findCompanyByProduct(item.getProduct()).getName()).distinct().toList().stream().toList(),
                order.getCountry(),
                order.getCity(),
                order.getStreet(),
                order.getHouse_number(),
                order.getOrderItem().stream().mapToInt(OrderItem::getQuantity).sum()
        );
    }

    public OrderStatusDTO mapOrderStatusToOrderStatusDTO(OrderStatus orderStatus) {
        return new OrderStatusDTO(orderStatus.getStatus(), orderStatus.getTime());
    }

    public OrderAdminLongDTO mapOrderToOrderAdminLongDTO(Order order) {
        User user = userRepository.findUserByOrder(order);

        return new OrderAdminLongDTO(
                order.getId(),
                order.getStatuses().stream().map(this::mapOrderStatusToOrderStatusDTO).sorted(Comparator.comparing(OrderStatusDTO::time)).toList(),
                order.getOrderItem().stream().map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add),
                user.getId(),
                user.getFirst_name(),
                user.getLast_name(),
                user.getUser_name(),
                order.getStatuses()
                        .stream()
                        .filter(status -> status.getStatus().equals(Status.IN_PROGRESS)).findFirst().orElseThrow().getTime(),
                order.getCity(),
                order.getStreet(),
                order.getHouse_number(),
                order.getZipcode(),
                order.getPhone(),
                mapOrderItemsToCompanyItemsDTO(order.getOrderItem()),
                order.getOrderItem().stream().mapToInt(OrderItem::getQuantity).sum()
        );
    }

    public List<CompanyItemsDTO> mapOrderItemsToCompanyItemsDTO(List<OrderItem> orderItems) {
        return orderItems.stream()
                .collect(Collectors.groupingBy(orderItem -> companyRepository.findCompanyByProduct(orderItem.getProduct()).getName()))
                .entrySet().stream()
                .map(entry -> {
                    String companyName = entry.getKey();
                    List<OrderItem> items = entry.getValue();

                    return new CompanyItemsDTO(
                            items.stream()
                            .flatMap(orderItem -> orderItem.getStatuses().stream())
                            .map(this::mapOrderStatusToOrderStatusDTO)
                            .sorted(Comparator.comparing(OrderStatusDTO::time))
                            .toList(),
                            companyName,
                            items.stream()
                                    .map(this::mapOrderItemToOrderItemAdminDTO2)
                                    .toList());
                })
                .toList();
    }

    public OrderItemAdminDTO2 mapOrderItemToOrderItemAdminDTO2(OrderItem orderItem) {

        return new OrderItemAdminDTO2(
                orderItem.getProduct().getName(),
                orderItem.getSize(),
                orderItem.getPrice(),
                orderItem.getQuantity(),
                orderItem.getProduct().getId(),
                orderItem.getId(),
                orderItem.getProduct().getParameters().stream().map(Parameter::getValue).toList()
        );

    }

    public OrderItemAdminDTO mapOrderItemToOrderItemAdminDTO(OrderItem orderItem) {
        Order order = orderRepository.findOrderByOrderItemContaining(orderItem);

        return new OrderItemAdminDTO(
                order.getId(),
                orderItem.getSize(),
                orderItem.getPrice(),
                orderItem.getQuantity(),
                order.getStatuses()
                        .stream()
                        .filter(status -> status.getStatus().equals(Status.IN_PROGRESS)).findFirst().orElseThrow().getTime(),
                orderItem.getStatuses()
                        .stream()
                        .max(Comparator.comparing(OrderStatus::getStatus))
                        .orElseThrow(() -> new NoSuchElementException("No status found")).getStatus()
        );
    }

    public OrderCompanyLongDTO mapOrderToOrderCompanyLongDTO(Order order, Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));

        List<OrderItem> items = order.getOrderItem().stream().filter(item -> Objects.equals(userRepository.findUserByCompany(companyRepository.findCompanyByProduct(item.getProduct())).getUser_name(), user.getUser_name())).toList();

        OrderItem orderItem = items.get(0);

        LocalDateTime sentDate = orderItem.getStatuses().stream()
                .filter(status -> status.getStatus().equals(Status.SENT))
                .map(OrderStatus::getTime)
                .findFirst()
                .orElse(null);

        LocalDateTime completionDate = order.getStatuses().stream()
                .filter(status -> status.getStatus().equals(Status.COMPLETED))
                .map(OrderStatus::getTime)
                .findFirst()
                .orElse(null);

        return new OrderCompanyLongDTO(
                order.getId(),
                completionDate,
                order.getCity(),
                order.getStreet(),
                order.getHouse_number(),
                order.getZipcode(),
                order.getPhone(),
                items.stream()
                        .map(order2 -> order2.getPrice().multiply(new BigDecimal(order2.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                sentDate,
                items.stream().map(this::mapOrderItemToOrderItemDto).toList(),
                order.getStatuses()
                        .stream()
                        .filter(status -> status.getStatus().equals(Status.IN_PROGRESS)).findFirst().orElseThrow().getTime(),
                user.getFirst_name(),
                user.getLast_name(),
                user.getUser_name()
        );
    }

    public CompanyLongDTO mapCompanyIdToCompanyLongDTO(Company company) {
        List<Product> products = company.getProducts();
        User user = userRepository.findUserByCompany(company);

        return new CompanyLongDTO(
                company.getId(),
                company.getName(),
                user.getFirst_name() + " " + user.getLast_name(),
                products.stream().mapToInt(Product::getSales).sum(),
                orderRepository.getMoneyTurnoverByCompanyId(company.getId()),
                products.stream().filter(product -> product.getRetired() == null).toList().size(),
                orderRepository.getOrdersByCompanyIdAndStatus(company.getId(), "COMPLETED").size(),
                orderRepository.getOrdersByCompanyIdAndStatus(company.getId(), "IN_PROGRESS").size() - orderRepository.getOrdersByCompanyIdAndStatus(company.getId(), "COMPLETED").size(),
                orderStatusRepository.getLastActivityByCompanyId(company.getId()),
                company.getCreated(),
                company.getBanned()
        );
    }

    public OrderUserAdminDTO mapOrderToOrderUserAdminDTO(Order order){

        return new OrderUserAdminDTO(
                order.getId(),
                order.getOrderItem().stream().map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add),
                order.getStreet(),
                order.getZipcode(),
                order.getHouse_number(),
                order.getCity(),
                order.getStatuses().stream().filter( status -> status.getStatus().equals(Status.IN_PROGRESS)).findFirst().get().getTime(),
                order.getStatuses()
                        .stream()
                        .max(Comparator.comparing(OrderStatus::getStatus))
                        .orElseThrow(() -> new NoSuchElementException("No status found")).getStatus()
        );
    };

    public UserAdminDTO mapUserDtoToUserAdminDTO(User user){
        List<Order> orders = userRepository.findOrdersByUser(user);
        String companyName = user.getCompany() != null ? user.getCompany().getName() : null;

        return new UserAdminDTO(
                user.getId(),
                user.getUser_name(),
                user.getFirst_name(),
                user.getLast_name(),
                user.getCreated(),
                user.getBanned(),
                companyName,
                orders.stream()
                        .flatMap(order -> order.getOrderItem().stream().map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity()))))
                        .reduce(BigDecimal.ZERO, BigDecimal::add),
                orders.size(),
                userRepository.getSentOrdersByUser(user).size(),
                user.getBalance(),
                user.isPhoto()
        );
    }

    public OrderItemUserDTO mapOrderItemToOrderItemUserDTO(OrderItem orderItem){
        Company company = companyRepository.findCompanyByProduct(orderItem.getProduct());

        return new OrderItemUserDTO(
                orderItem.getId(),
                orderItem.getProduct().getName(),
                orderItem.getSize(),
                orderItem.getPrice(),
                orderItem.getQuantity(),
                orderItem.getProduct().getId(),
                company.getName(),
                orderItem.getProduct().getParameters().stream().map(Parameter::getValue).toList()
        );
    }

}


