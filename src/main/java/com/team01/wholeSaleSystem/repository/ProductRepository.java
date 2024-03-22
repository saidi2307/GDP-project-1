package com.team01.wholeSaleSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.team01.wholeSaleSystem.entity.Category;
import com.team01.wholeSaleSystem.entity.Product;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategory(Category category);
    List<Product> findByCategoryAndProductName(Category category, String productName);
    List<Product> findByProductName(String productName);
    Product findByProductNameAndCategory(String productName, Category category);
    Product findFirstByProductName(String productName);

    @Query("SELECT p FROM Product p")
    List<Product> findAllProducts();
}
