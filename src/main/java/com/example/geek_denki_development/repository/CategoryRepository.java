// CategoryRepository.java
package com.example.geek_denki_development.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.geek_denki_development.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByLevel(Integer level);
    List<Category> findByParentId(Long parentId);
    List<Category> findByParentIsNull();
    
    Category findByName(String name);
    
    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId AND c.level = 2 ORDER BY c.id")
    List<Category> findMidCategoriesByParentId(@Param("parentId") Long parentId);
    
    @Query("SELECT c FROM Category c WHERE c.parent.id = :parentId AND c.level = 3 ORDER BY c.id")
    List<Category> findSmallCategoriesByParentId(@Param("parentId") Long parentId);
    
    // 新規追加：名前とレベルでカテゴリーを取得するメソッド
    @Query("SELECT c FROM Category c WHERE c.name = :name AND c.level = :level")
    Category findByNameAndLevel(@Param("name") String name, @Param("level") Integer level);
}
