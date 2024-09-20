package org.example.store.repository;

import org.example.store.dto.user.UserDTO;
import org.example.store.dto.user.UserShortDTO;
import org.example.store.model.Company;
import org.example.store.model.Order;
import org.example.store.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.user_name = ?1")
    Optional<User> findUserByUser_name(String username);

    @Query("SELECT coalesce(SUM(it.price * it.quantity),0) from User u LEFT JOIN u.orders o LEFT JOIN o.orderItem it WHERE u = ?1")
    BigDecimal getMoneyTurnoverByUser(User user);

    @Query("SELECT o FROM User u INNER JOIN u.orders o WHERE u = ?1")
    List<Order> findOrdersByUser(User user);

    User findUserByCompany(Company company);

    @Query("SELECT new org.example.store.dto.user.UserDTO(u.id, u.first_name, u.last_name, u.user_name, coalesce(c.name, null), u.created, u.banned, u.balance, u.photo) FROM User u LEFT JOIN u.company c WHERE u.user_name=?1")
    Optional<UserDTO> findUserByName(String name);

    @Query("SELECT u FROM User u LEFT JOIN u.company c WHERE u.user_name=?1")
    Optional<User> findUserByName2(String name);

    @Query("SELECT o from User u LEFT JOIN u.orders o LEFT JOIN o.statuses st WHERE u = ?1 AND CAST(st.status AS String) = 'SENT'")
    List<Order> getSentOrdersByUser(User user);

    @Query("SELECT u FROM User u LEFT JOIN u.orders o WHERE :order MEMBER OF u.orders")
    User findUserByOrder(Order order);

    @Query("SELECT u FROM User u WHERE u.user_name = ?1")
    Optional<User> findUserByUsername(String username);
}

