package com.team01.wholeSaleSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.team01.wholeSaleSystem.entity.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c")
    List<Category> findAllCategoriesAdmin();

    @Query("SELECT c FROM Category c where c.active = true")
    List<Category> findAllCategories();

    Optional<Category> findByCategoryName(String name);
}
