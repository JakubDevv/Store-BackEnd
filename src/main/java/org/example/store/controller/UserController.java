package org.example.store.controller;

import jakarta.validation.Valid;
import org.example.store.dto.order.OrderCreateDTO;
import org.example.store.dto.order.OrderUserDTO;
import org.example.store.dto.order.OrderUserDTO2;
import org.example.store.dto.product.ProductReviewCreateDTO;
import org.example.store.dto.stats.UserSpendingStats;
import org.example.store.dto.user.UserDTO;
import org.example.store.dto.user.UserTransactionStats;
import org.example.store.services.UserService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/order")
    public ResponseEntity<Void> createOrder(Principal principal, @Valid @RequestBody OrderCreateDTO order) {
        userService.createOrder(principal, order);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<OrderUserDTO2>> getOrders(Principal principal) {
        return new ResponseEntity<>(userService.getOrders(principal), HttpStatus.OK);
    }

    @PostMapping("/product/review")
    public ResponseEntity<Boolean> createProductReview(Principal principal, @Valid @RequestBody ProductReviewCreateDTO productReviewCreateDto) {
        userService.createProductReview(principal, productReviewCreateDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/verify-purchase")
    public ResponseEntity<Boolean> checkIfUserBoughtProduct(Principal principal, @RequestParam Long productId) {
        return new ResponseEntity<>(userService.checkIfUserBoughtProduct(principal, productId), HttpStatus.OK);
    }

    @PutMapping("/photo")
    public ResponseEntity<Void> updateProfilePhoto(Principal principal, @RequestParam("file") MultipartFile file) {
        userService.updateProfilePhoto(principal, file);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/photo")
    public ResponseEntity<?> getProfilePhoto(Principal principal) {
        return userService.getProfilePhoto(principal);
    }

    @GetMapping("/userImage")
    public ResponseEntity<?> getProfileImageByUserId(@RequestParam Long userId2) {
        return userService.getProfilePhotoByUserId(userId2);
    }

    @PutMapping("/order/{id}/complete")
    public ResponseEntity<Void> setOrderStatusCompleted(Principal principal, @PathVariable Long id) {
        userService.setOrderStatusCompleted(principal, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/stats")
    public ResponseEntity<UserSpendingStats> getStatsFromOrders(Principal principal) {
        return new ResponseEntity<>(userService.getStatsFromOrders(principal), HttpStatus.OK);
    }

    @GetMapping("/")
    public UserDTO getUser(Principal principal){
        return userService.getUser(principal);
    }

    @GetMapping("/transaction/stats")
    public ResponseEntity<UserTransactionStats> getTransactionStats(Principal principal, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return new ResponseEntity<>(userService.getTransactionStats(principal, date), HttpStatus.OK);
    }
}
