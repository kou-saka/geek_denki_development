package com.example.geek_denki_development.controller;

import java.time.LocalDateTime;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.geek_denki_development.entity.Administrator;
import com.example.geek_denki_development.entity.Permission;
import com.example.geek_denki_development.entity.Role;
import com.example.geek_denki_development.entity.Store;
import com.example.geek_denki_development.form.ProfileEditForm;
import com.example.geek_denki_development.repository.AdministratorRepository;
import com.example.geek_denki_development.repository.PermissionRepository;
import com.example.geek_denki_development.repository.RoleRepository;
import com.example.geek_denki_development.repository.StoreRepository;

@Controller
public class ProfileController {

    @Autowired
    private AdministratorRepository administratorRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    @Autowired
    private StoreRepository storeRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/profile")
    public String viewProfile(Model model) {
        // 現在ログインしている管理者のメールアドレスを取得
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        
        // メールアドレスから管理者情報を取得
        Administrator administrator = administratorRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("管理者情報が見つかりません"));
        
        // 関連情報を取得
        Role role = roleRepository.findById(administrator.getRoleId())
                .orElseThrow(() -> new RuntimeException("役職情報が見つかりません"));
        
        Permission permission = permissionRepository.findById(administrator.getPermissionId())
                .orElseThrow(() -> new RuntimeException("権限情報が見つかりません"));
        
        Store store = storeRepository.findById(administrator.getStoreId())
                .orElseThrow(() -> new RuntimeException("店舗情報が見つかりません"));
        
        // モデルに情報を追加
        model.addAttribute("administrator", administrator);
        model.addAttribute("roleName", role.getName());
        model.addAttribute("permissionName", permission.getName());
        model.addAttribute("storeName", store.getName());
        
        // 成功メッセージがある場合に表示（編集成功時）
        return "profile";
    }
    
    // プロフィール編集画面表示
    @GetMapping("/profile/edit")
    public String editProfileForm(Model model) {
        // 現在ログインしている管理者情報を取得
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        
        Administrator administrator = administratorRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("管理者情報が見つかりません"));
        
        // フォームに値をセット
        ProfileEditForm form = new ProfileEditForm();
        form.setId(administrator.getId());
        form.setStoreId(administrator.getStoreId());
        form.setLastName(administrator.getLastName());
        form.setFirstName(administrator.getFirstName());
        form.setEmail(administrator.getEmail());
        form.setPhoneNumber(administrator.getPhoneNumber());
        form.setRoleId(administrator.getRoleId());
        form.setPermissionId(administrator.getPermissionId());
        
        // 店舗情報を取得（表示用）
        Store store = storeRepository.findById(administrator.getStoreId())
                .orElseThrow(() -> new RuntimeException("店舗情報が見つかりません"));
        
        // モデルに情報を追加
        model.addAttribute("profileEditForm", form);
        model.addAttribute("storeName", store.getName());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("permissions", permissionRepository.findAll());
        
        return "profile-edit";
    }
    
    // プロフィール更新処理
    @PostMapping("/profile/edit")
    public String updateProfile(
            @Valid @ModelAttribute("profileEditForm") ProfileEditForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        // 現在ログインしている管理者情報を取得
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName();
        
        Administrator currentAdmin = administratorRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new RuntimeException("管理者情報が見つかりません"));
        
        // メールアドレスの重複チェック（自分自身は除外）
        if (!form.getEmail().equals(currentUserEmail)) {
            if (administratorRepository.findByEmail(form.getEmail()).isPresent()) {
                bindingResult.rejectValue("email", "error.email", "このメールアドレスは既に登録されています");
            }
        }
        
        // バリデーションエラーがある場合は編集画面に戻る
        if (bindingResult.hasErrors()) {
            // 店舗情報を取得（表示用）
            Store store = storeRepository.findById(currentAdmin.getStoreId())
                    .orElseThrow(() -> new RuntimeException("店舗情報が見つかりません"));
            
            model.addAttribute("storeName", store.getName());
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("permissions", permissionRepository.findAll());
            return "profile-edit";
        }
        
        // メールアドレスが変更されたかどうか確認
        boolean emailChanged = !currentUserEmail.equals(form.getEmail());
        
        // 管理者情報を更新
        currentAdmin.setLastName(form.getLastName());
        currentAdmin.setFirstName(form.getFirstName());
        currentAdmin.setEmail(form.getEmail());
        currentAdmin.setPhoneNumber(form.getPhoneNumber());
        currentAdmin.setRoleId(form.getRoleId());
        currentAdmin.setPermissionId(form.getPermissionId());
        
        // パスワードが入力されている場合のみハッシュ化して更新
        if (form.isPasswordChangeRequired()) {
            currentAdmin.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        }
        
        // 更新日時を設定
        currentAdmin.setUpdatedAt(LocalDateTime.now());
        
        // 保存
        administratorRepository.save(currentAdmin);
        
        // メールアドレスが変更された場合、セッションを無効化し再ログインさせる
        if (emailChanged) {
            // ログアウト処理
            SecurityContextHolder.clearContext();
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            
            redirectAttributes.addFlashAttribute("successMessage", "プロフィールを更新しました。メールアドレスが変更されたため、再度ログインしてください。");
            return "redirect:/login";
        }
        
        // 成功メッセージをセット
        redirectAttributes.addFlashAttribute("successMessage", "プロフィールを更新しました");
        
        return "redirect:/profile";
    	}
	}
