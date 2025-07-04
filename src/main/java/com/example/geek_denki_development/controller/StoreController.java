package com.example.geek_denki_development.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.geek_denki_development.entity.Administrator;
import com.example.geek_denki_development.entity.Store;
import com.example.geek_denki_development.form.StoreEditForm;
import com.example.geek_denki_development.repository.AdministratorRepository;
import com.example.geek_denki_development.repository.StoreRepository;

@Controller
public class StoreController {

    @Autowired
    private StoreRepository storeRepository;
    
    @Autowired
    private AdministratorRepository administratorRepository;
    
    @GetMapping("/stores")
    public String viewStore(Model model) {
        // 現在ログインしている管理者の情報を取得
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        
        Administrator administrator = administratorRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("管理者情報が見つかりません"));
        
        // 管理者権限を持っているかチェック
        boolean isAdmin = administrator.getPermissionId() == 1L;
        model.addAttribute("isAdmin", isAdmin);
        
        // 現在のユーザーの店舗情報を取得
        Store store = storeRepository.findById(administrator.getStoreId())
                .orElseThrow(() -> new RuntimeException("店舗情報が見つかりません"));
        
        model.addAttribute("store", store);
        
        return "stores/detail"; 
    }
    
    // 店舗編集画面表示
    @GetMapping("/stores/{id}/edit")
    public String editStoreForm(@PathVariable("id") Long id, Model model) {
        // 管理者権限チェック
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        
        Administrator administrator = administratorRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("管理者情報が見つかりません"));
        
        if (administrator.getPermissionId() != 1L) {
            // 管理者権限がない場合はダッシュボードにリダイレクト
            return "redirect:/dashboard";
        }
        
        // 店舗情報取得
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("店舗情報が見つかりません"));
        
        // フォームに値をセット
        StoreEditForm form = new StoreEditForm();
        form.setId(store.getId());
        form.setName(store.getName());
        form.setAddress(store.getAddress());
        
        model.addAttribute("storeEditForm", form);
        
        return "stores/edit";
    }
    
    // 店舗更新処理
    @PostMapping("/stores/{id}/edit")
    public String updateStore(
            @PathVariable("id") Long id,
            @Valid StoreEditForm storeEditForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // 管理者権限チェック
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        
        Administrator administrator = administratorRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("管理者情報が見つかりません"));
        
        if (administrator.getPermissionId() != 1L) {
            // 管理者権限がない場合はダッシュボードにリダイレクト
            return "redirect:/dashboard";
        }
        
        // バリデーションエラーがある場合は編集画面に戻る
        if (bindingResult.hasErrors()) {
            return "stores/edit";
        }
        
        // 店舗情報を取得
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("店舗情報が見つかりません"));
        
        // 店舗情報を更新
        store.setAddress(storeEditForm.getAddress());
        
        // 更新日時を設定する場合は以下を追加
        // store.setUpdatedAt(LocalDateTime.now());
        
        // 保存
        storeRepository.save(store);
        
        // 成功メッセージをセット
        redirectAttributes.addFlashAttribute("successMessage", "店舗情報を変更しました");
        
        return "redirect:/stores";
    }
}
