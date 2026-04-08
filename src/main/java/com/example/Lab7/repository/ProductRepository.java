package com.example.Lab7.repository;

import com.example.Lab7.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByCategoryId(String categoryId);
    List<Product> findByNameContainingIgnoreCase(String keyword);
    boolean existsByCategoryId(String categoryId);
    //lấy sản phẩm theo giá từ minPrice đến maxPrice
    @Query("SELECT product FROM Product product WHERE product.price BETWEEN :minPrice AND :maxPrice ORDER BY product.price ASC")
    List<Product> findByPriceRange(
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice
    );
}
