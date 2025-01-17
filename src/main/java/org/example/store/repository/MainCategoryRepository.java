package org.example.store.repository;

import org.example.store.model.MainCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MainCategoryRepository extends JpaRepository<MainCategory, Long> {

    List<MainCategory> findMainCategoriesByDeletedIsNull();
}
