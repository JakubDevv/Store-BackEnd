package org.example.store.services;

import org.example.store.dto.order.OrderCreateDTO;
import org.example.store.dto.order.OrderUserDTO;
import org.example.store.dto.order.OrderUserDTO2;
import org.example.store.dto.product.ProductReviewCreateDTO;
import org.example.store.dto.stats.UserSpendingStats;
import org.example.store.dto.transaction.TransactionDTO;
import org.example.store.dto.user.UserDTO;
import org.example.store.dto.user.UserTransactionStats;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Service
public interface UserService {

    UserDTO getUser(Principal principal);

    void createOrder(Principal principal, OrderCreateDTO order);

    void createProductReview(Principal principal, ProductReviewCreateDTO productReviewCreateDto);

    boolean checkIfUserBoughtProduct(Principal principal, Long productId);

    ResponseEntity<?> getProfilePhoto(Principal principal);

    void updateProfilePhoto(Principal principal, MultipartFile file);

    List<OrderUserDTO2> getOrders(Principal principal);

    void setOrderStatusCompleted(Principal principal, Long orderId);

    UserSpendingStats getStatsFromOrders(Principal principal);

    ResponseEntity<?> getProfilePhotoByUserId(Long userId2);

    UserTransactionStats getTransactionStats(Principal principal, LocalDate date);

}
