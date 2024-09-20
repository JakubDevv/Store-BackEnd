package org.example.store.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String user_name;

    private String first_name;

    private String last_name;

    private String password;

    private LocalDateTime created;

    private LocalDateTime banned;

    private BigDecimal balance;

    private boolean photo;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

    @OneToOne
    @JoinColumn(referencedColumnName = "id", name = "fk_user_company")
    private Company company;

    @OneToMany
    @JoinColumn(referencedColumnName = "id", name = "fk_user_order")
    private List<Order> orders;

    @OneToMany
    @JoinColumn(referencedColumnName = "id", name = "fk_transaction_users")
    private List<Transaction> transactions;

    public User(String user_name, String first_name, String last_name, String password) {
        this.user_name = user_name;
        this.first_name = first_name;
        this.last_name = last_name;
        this.password = password;
        this.created = LocalDateTime.now();
        this.balance = BigDecimal.valueOf(1000);
        this.photo = false;
    }

    public User() {
    }

}
