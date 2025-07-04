package com.example.geek_denki_development.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.geek_denki_development.entity.Category;
import com.example.geek_denki_development.entity.Product;
import com.example.geek_denki_development.repository.CategoryRepository;
import com.example.geek_denki_development.repository.ProductRepository;

@Controller
public class ProductController {

    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @GetMapping("/products")
    public String listProducts(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long midCategoryId,
            @RequestParam(required = false) Long smallCategoryId,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<Product> productPage = null;
            
            if (smallCategoryId != null) {
                if (keyword != null && !keyword.isEmpty()) {
                    productPage = productRepository.findByKeywordAndCategoryId(keyword, smallCategoryId, pageable);
                } else {
                    productPage = productRepository.findByCategoryId(smallCategoryId, pageable);
                }
                model.addAttribute("selectedSmallCategoryId", smallCategoryId);
                // 親カテゴリ設定
                Category smallCategory = categoryRepository.findById(smallCategoryId).orElse(null);
                if (smallCategory != null && smallCategory.getParent() != null) {
                    model.addAttribute("selectedMidCategoryId", smallCategory.getParent().getId());
                    if (smallCategory.getParent().getParent() != null) {
                        model.addAttribute("selectedCategoryId", smallCategory.getParent().getParent().getId());
                    }
                }
            } else if (midCategoryId != null) {
                // 中カテゴリ配下の小カテゴリIDリストを取得してIN句で検索
                List<Category> smallCategories = categoryRepository.findSmallCategoriesByParentId(midCategoryId);
                if (!smallCategories.isEmpty()) {
                    List<Long> smallCategoryIds = smallCategories.stream().map(Category::getId).collect(Collectors.toList());
                    if (keyword != null && !keyword.isEmpty()) {
                        productPage = productRepository.findByKeywordAndCategoryIdIn(keyword, smallCategoryIds, pageable);
                    } else {
                        productPage = productRepository.findByCategoryIdIn(smallCategoryIds, pageable);
                    }
                } else {
                    productPage = Page.empty();
                }
                model.addAttribute("selectedMidCategoryId", midCategoryId);
                // 親カテゴリ設定
                Category midCategory = categoryRepository.findById(midCategoryId).orElse(null);
                if (midCategory != null && midCategory.getParent() != null) {
                    model.addAttribute("selectedCategoryId", midCategory.getParent().getId());
                }
            } else if (categoryId != null) {
                // 大カテゴリ配下の中カテゴリ取得
                List<Category> midCategories = categoryRepository.findMidCategoriesByParentId(categoryId);
                List<Long> smallCategoryIds = new ArrayList<>();
                for (Category mid : midCategories) {
                    smallCategoryIds.addAll(
                        categoryRepository.findSmallCategoriesByParentId(mid.getId())
                            .stream().map(Category::getId).collect(Collectors.toList())
                    );
                }
                if (!smallCategoryIds.isEmpty()) {
                    if (keyword != null && !keyword.isEmpty()) {
                        productPage = productRepository.findByKeywordAndCategoryIdIn(keyword, smallCategoryIds, pageable);
                    } else {
                        productPage = productRepository.findByCategoryIdIn(smallCategoryIds, pageable);
                    }
                } else {
                    productPage = Page.empty();
                }
                model.addAttribute("selectedCategoryId", categoryId);
            } else if (keyword != null && !keyword.isEmpty()) {
                productPage = productRepository.findByKeyword(keyword, pageable);
            } else {
                productPage = productRepository.findAll(pageable);
            }
            
            // 結果をモデルに追加
            model.addAttribute("products", productPage.getContent());
            model.addAttribute("currentPage", productPage.getNumber());
            model.addAttribute("totalItems", productPage.getTotalElements());
            model.addAttribute("totalPages", productPage.getTotalPages());
            model.addAttribute("pageSize", size);
            model.addAttribute("keyword", keyword);
            model.addAttribute("categories", categoryRepository.findByLevel(1));
            
            if (model.getAttribute("selectedCategoryId") != null) {
                Long selectedCategoryId = (Long) model.getAttribute("selectedCategoryId");
                model.addAttribute("midCategories", categoryRepository.findMidCategoriesByParentId(selectedCategoryId));
            }
            
            if (model.getAttribute("selectedMidCategoryId") != null) {
                Long selectedMidCategoryId = (Long) model.getAttribute("selectedMidCategoryId");
                model.addAttribute("smallCategories", categoryRepository.findSmallCategoriesByParentId(selectedMidCategoryId));
            }
            
            return "products/list";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("errorMessage", "商品の取得中にエラーが発生しました: " + e.getMessage());
            model.addAttribute("products", Collections.emptyList());
            model.addAttribute("categories", categoryRepository.findByLevel(1));
            return "products/list";
        }
    }
    
 // ProductController.java (追加部分)
    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        Product product = productRepository.findById(id).orElse(null);
        if (product == null) {
            model.addAttribute("errorMessage", "該当する商品が見つかりません");
            return "redirect:/products";
        }
        model.addAttribute("product", product);
        return "products/detail";
    }

}

