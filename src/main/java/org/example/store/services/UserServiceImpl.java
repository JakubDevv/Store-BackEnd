package org.example.store.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.store.dto.category.CategoryUserExpenseDTO;
import org.example.store.dto.order.OrderCreateDTO;
import org.example.store.dto.order.OrderItemCreateDTO;
import org.example.store.dto.order.OrderUserDTO;
import org.example.store.dto.order.OrderUserDTO2;
import org.example.store.dto.product.ProductReviewCreateDTO;
import org.example.store.dto.stats.AmountDate2;
import org.example.store.dto.stats.UserSpendingStats;
import org.example.store.dto.transaction.TransactionDTO;
import org.example.store.dto.user.UserDTO;
import org.example.store.dto.user.UserTransactionStats;
import org.example.store.exception.order.OrderNotFoundException;
import org.example.store.exception.product.ProductNotFoundException;
import org.example.store.exception.product.ProductPropertiesChanged;
import org.example.store.exception.product.SizeNotFoundException;
import org.example.store.exception.user.MoneyNotEnoughException;
import org.example.store.exception.user.UserNotFoundException;
import org.example.store.mapper.OrderMapper;
import org.example.store.model.*;
import org.example.store.repository.*;
import org.example.store.s3.S3Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    @Value("${bucket.name}")
    private String bucketName;

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final SizesRepository sizesRepository;
    private final OrderItemRepository orderItemRepository;
    private final S3Service s3Service;
    private final OrderMapper orderMapper;
    private final OrderStatusRepository orderStatusRepository;
    private final SubCategoryRepository subCategoryRepository;
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final ProductsService productsService;
    private final TransactionRepository transactionRepository;

    @Override
    public UserDTO getUser(Principal principal) {
        return userRepository.findUserByName(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
    }

    @Override
    @Transactional
    public void createOrder(Principal principal, OrderCreateDTO order) {
        User user = userRepository.findUserByName2(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        if(productsService.validateProducts(order.items()).equals(order.items())){
            if(calculateOrderPrice(order).compareTo(user.getBalance()) <= 0){
                proceedOrder(user, order);
            } else {
                throw new MoneyNotEnoughException();
            }
        } else {
            throw new ProductPropertiesChanged(productsService.validateProducts(order.items()));
        }
    }

    @Override
    public void createProductReview(Principal principal, ProductReviewCreateDTO productReviewCreateDto) {

    }

    @Override
    public boolean checkIfUserBoughtProduct(Principal principal, Long productId) {
        return true;
    }

    @Override
    public ResponseEntity<?> getProfilePhoto(Principal principal) {
        User user = userRepository.findUserByUser_name(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        try {
            byte[] image = s3Service.getObject(
                    bucketName,
                    "profilePhoto/" + user.getId()
            );
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("image/png"))
                    .body(image);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public void updateProfilePhoto(Principal principal, MultipartFile file) {
        User user = userRepository.findUserByUser_name(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        try {
            s3Service.putObject(
                    bucketName,
                    "profilePhoto/" + user.getId(),
                    file.getBytes()
            );
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public List<OrderUserDTO2> getOrders(Principal principal) {
        return userRepository.findUserByUser_name(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()))
                .getOrders()
                .stream()
                .map(orderMapper::mapOrderToOrderUserDTO2)
                .sorted(Comparator.comparing(OrderUserDTO2::date).reversed())
                .toList();
    }

    @Transactional
    public void proceedOrder(User user, OrderCreateDTO orderCreateDTO) {
            Order order = new Order(orderCreateDTO.city(), orderCreateDTO.street(), orderCreateDTO.addressNumber(), orderCreateDTO.zip_code(), orderCreateDTO.phone());
            BigDecimal fullPrice = new BigDecimal(0);
            HashMap<Long, BigDecimal> map = new HashMap<>();
            for (OrderItemCreateDTO productId : orderCreateDTO.items()) {
                Size size = sizesRepository.findById(Long.valueOf(productId.sizeId())).orElseThrow(() -> new SizeNotFoundException(Long.valueOf(productId.sizeId())));
                Product product = productRepository.findById(productId.id()).orElseThrow(() -> new ProductNotFoundException(productId.id()));
                Company company = companyRepository.findCompanyByProduct(product);
                if (map.containsKey(company.getId())) {
                    BigDecimal bigDecimal = map.get(company.getId());
                    map.replace(company.getId(), bigDecimal.add(productId.price().multiply(new BigDecimal(productId.quantity()))));
                } else {
                    map.put(company.getId(), productId.price().multiply(new BigDecimal(productId.quantity())));
                }
                fullPrice = fullPrice.add(productId.price().multiply(new BigDecimal(productId.quantity())));
                OrderItem orderItem = new OrderItem(productId.quantity());
                orderItem.setSize(size.getSizevalue());
                orderItem.setProduct(product);
                orderItem.setPrice(productId.price());
                OrderStatus orderStatus = new OrderStatus(Status.IN_PROGRESS);
                orderStatusRepository.save(orderStatus);
                orderItem.setStatuses(List.of(orderStatus));
                orderItemRepository.save(orderItem);
                order.addOrderItem(orderItem);
                size.setQuantity(size.getQuantity() - productId.quantity());
                sizesRepository.save(size);
            }
            Transaction transaction = new Transaction(fullPrice, Type.SENT);
            OrderStatus orderStatuss = new OrderStatus(Status.IN_PROGRESS);
            orderStatusRepository.save(orderStatuss);
            order.setStatuses(List.of(orderStatuss));
            order.setCountry(orderCreateDTO.country());
            orderRepository.save(order);
            transaction.setOrder(order);
            transactionRepository.save(transaction);
            user.getOrders().add(order);
            user.getTransactions().add(transaction);
            user.setBalance(user.getBalance().subtract(fullPrice));
            userRepository.save(user);
            map.forEach((a, b) -> {
                Transaction transactionn = new Transaction(b, Type.RECEIVED);
                transactionn.setOrder(order);
                transactionRepository.save(transactionn);
                User userByCompany = userRepository.findUserByCompany(companyRepository.findById(a).orElseThrow(IllegalArgumentException::new));
                userByCompany.setBalance(userByCompany.getBalance().add(b));
                userByCompany.getTransactions().add(transactionn);
                userRepository.save(userByCompany);
        });
    }

    private BigDecimal calculateOrderPrice(OrderCreateDTO orderCreateDTO) {
        return orderCreateDTO.items().stream()
                .map(item -> {
                    Product product = productRepository.findById(item.id()).orElseThrow(() -> new ProductNotFoundException(item.id()));
                    BigDecimal actualPrice = product.getDiscount_price() == null ? product.getPrice() : product.getDiscount_price();
                    return actualPrice.multiply(new BigDecimal(item.quantity()));
                }).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public void setOrderStatusCompleted(Principal principal, Long orderId) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new OrderNotFoundException(orderId));
        User user = userRepository.findUserByOrder(order);
        if (Objects.equals(principal.getName(), user.getUser_name())) {

            order.getOrderItem().forEach(item -> {
                OrderStatus orderStatus = new OrderStatus(Status.COMPLETED);
                orderStatusRepository.save(orderStatus);
                item.getStatuses().add(orderStatus);
                orderItemRepository.save(item);
            });

            OrderStatus orderStatus = new OrderStatus(Status.COMPLETED);
            orderStatusRepository.save(orderStatus);
            order.getStatuses().add(orderStatus);
            orderRepository.save(order);
        }
    }

    @Override
    public UserSpendingStats getStatsFromOrders(Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName()));
        Map<String, BigDecimal> expenseByCategory = new HashMap<>();
        long between = ChronoUnit.MONTHS.between(user.getCreated(), LocalDateTime.now());
        long minBetween = Math.max(between, 1);

        BigDecimal moneyTurnover = userRepository.getMoneyTurnoverByUser(user);
        List<OrderItem> items = userRepository.findOrdersByUser(user).stream().flatMap(order -> order.getOrderItem().stream()).toList();
        items.forEach(item -> {
            SubCategory subCategory = subCategoryRepository.getSubCategoryByProductsContaining(item.getProduct());
            expenseByCategory.compute(subCategory.getName(), (key, oldValue) ->
                    (oldValue == null ? BigDecimal.ZERO : oldValue).add(item.getPrice().multiply(new BigDecimal(item.getQuantity())))
            );
        });

        List<Map.Entry<String, BigDecimal>> sortedEntries = expenseByCategory.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .toList();

        List<Map.Entry<String, BigDecimal>> topThreeEntries = sortedEntries.subList(0, Math.min(3, sortedEntries.size()));

        List<CategoryUserExpenseDTO> list = topThreeEntries.stream().map(category -> new CategoryUserExpenseDTO(category.getKey(), category.getValue(), category.getValue().divide(moneyTurnover, 4, RoundingMode.HALF_UP).multiply(new BigDecimal("100")).intValue())).toList();

        return new UserSpendingStats(
                orderRepository.calculateMoneySpentByUserIdInDays(user, LocalDateTime.now().minusMonths(6)),
                orderRepository.calculateIncomeByUserIdInDays(user, LocalDateTime.now().minusMonths(6)),
                orderRepository.calculateAverageMoneySpentByUserIdAndMonths(user, (int) minBetween),
                orderRepository.calculateMoneySpentByUserId(user, LocalDateTime.now().minusMonths(6)),
                orderRepository.calculateMoneySpentByUserId(user, LocalDateTime.now().minusYears(1)),
                list
        );
    }

    @Override
    public ResponseEntity<?> getProfilePhotoByUserId(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        try {
            byte[] image = s3Service.getObject(
                    bucketName,
                    "profilePhoto/" + user.getId()
            );
            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.valueOf("image/png"))
                    .body(image);
        } catch (IOException e) {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public UserTransactionStats getTransactionStats(Principal principal, LocalDate date) {
        LocalDateTime now = LocalDateTime.now();
        long betweenDays = ChronoUnit.DAYS.between(date, now);
        Long userId = userRepository.findUserByUsername(principal.getName()).orElseThrow(() -> new UserNotFoundException(principal.getName())).getId();
        Long amountOfTransactionsByUserIdAfterDate = transactionRepository.getAmountOfTransactionsByUserIdAfterDate(userId, date.atStartOfDay());
        Long amountOfTransactionsByUserIdAfterDate1 = transactionRepository.getAmountOfTransactionsByUserIdAfterDate(userId, date.minusDays(betweenDays).atStartOfDay());

        BigDecimal spentByUserIdAfterDate = transactionRepository.getSpentByUserIdAfterDate(userId, date.atStartOfDay());
        BigDecimal spentByUserIdAfterDate1 = transactionRepository.getSpentByUserIdAfterDate(userId, date.minusDays(betweenDays).atStartOfDay());

        BigDecimal percentageDifference = Objects.equals(spentByUserIdAfterDate1, BigDecimal.ZERO) ? BigDecimal.ZERO : spentByUserIdAfterDate.divide(spentByUserIdAfterDate1, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        BigDecimal incomeByUserIdAfterDate = transactionRepository.getIncomeByUserIdAfterDate(userId, date.atStartOfDay());
        BigDecimal incomeByUserIdAfterDate1 = transactionRepository.getIncomeByUserIdAfterDate(userId, date.minusDays(betweenDays).atStartOfDay());

        BigDecimal percentageDifference2 = Objects.equals(incomeByUserIdAfterDate1, BigDecimal.ZERO) ? BigDecimal.ZERO : incomeByUserIdAfterDate.divide(incomeByUserIdAfterDate1, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

        return new UserTransactionStats(
                spentByUserIdAfterDate,
                percentageDifference.doubleValue(),
                incomeByUserIdAfterDate,
                percentageDifference2.doubleValue(),
                amountOfTransactionsByUserIdAfterDate,
                -(amountOfTransactionsByUserIdAfterDate1-amountOfTransactionsByUserIdAfterDate-amountOfTransactionsByUserIdAfterDate)
        );
    }

}
