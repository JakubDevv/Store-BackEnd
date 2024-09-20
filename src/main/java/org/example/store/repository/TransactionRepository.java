package org.example.store.repository;

import org.example.store.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT coalesce(SUM(t.amount), 0) FROM User u JOIN u.transactions t WHERE u.id=?1 AND t.date > ?2 AND t.type='RECEIVED'")
    BigDecimal getIncomeByUserIdAfterDate(Long userId, LocalDateTime date);

    @Query("SELECT coalesce(SUM(t.amount), 0) FROM User u JOIN u.transactions t WHERE u.id=?1 AND t.date > ?2 AND t.type='SENT'")
    BigDecimal getSpentByUserIdAfterDate(Long userId, LocalDateTime date);

    @Query("SELECT coalesce(COUNT(t), 0) FROM User u JOIN u.transactions t WHERE u.id=?1 AND t.date > ?2")
    Long getAmountOfTransactionsByUserIdAfterDate(Long userId, LocalDateTime date);

}
