// ProductRepository.java
package com.example.geek_denki_development.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.geek_denki_development.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    
    Page<Product> findAll(Pageable pageable);
    
    
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    Page<Product> findByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.category.parent.id = :categoryId")
    Page<Product> findByParentCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE p.category.parent.parent.id = :categoryId")
    Page<Product> findByGrandParentCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Product> findByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.category.id = :categoryId")
    Page<Product> findByKeywordAndCategoryId(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.category.parent.id = :categoryId")
    Page<Product> findByKeywordAndParentCategoryId(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, Pageable pageable);
    
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.category.parent.parent.id = :categoryId")
    Page<Product> findByKeywordAndGrandParentCategoryId(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, Pageable pageable);
 // ProductRepository.java (追加分)
    @Query("SELECT p FROM Product p WHERE p.category.id IN :categoryIds")
    Page<Product> findByCategoryIdIn(@Param("categoryIds") List<Long> categoryIds, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND p.category.id IN :categoryIds")
    Page<Product> findByKeywordAndCategoryIdIn(@Param("keyword") String keyword, @Param("categoryIds") List<Long> categoryIds, Pageable pageable);

}
