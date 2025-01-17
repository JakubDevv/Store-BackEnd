package org.example.store.repository;

import org.example.store.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    @Query("SELECT r from Role r WHERE r.name = ?1")
    Role findRoleByName(String name);
}
