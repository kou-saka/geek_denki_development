// LoginController.java
package com.example.geek_denki_development.controller;

import java.time.LocalDateTime;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.geek_denki_development.entity.Administrator;
import com.example.geek_denki_development.form.AdministratorForm;
import com.example.geek_denki_development.repository.AdministratorRepository;
import com.example.geek_denki_development.repository.PermissionRepository;
import com.example.geek_denki_development.repository.RoleRepository;
import com.example.geek_denki_development.repository.StoreRepository;

@Controller
public class LoginController {

    @Autowired
    private AdministratorRepository administratorRepository;
    
    @Autowired
    private StoreRepository storeRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PermissionRepository permissionRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("administratorForm", new AdministratorForm());
        model.addAttribute("stores", storeRepository.findAll());
        model.addAttribute("roles", roleRepository.findAll());
        model.addAttribute("permissions", permissionRepository.findAll());
        return "register";
    }
    
    @PostMapping("/register")
    public String registerSubmit(@Valid @ModelAttribute("administratorForm") AdministratorForm form, 
                                BindingResult bindingResult,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        
        // バリデーションエラーがある場合は登録フォームに戻る
        if (bindingResult.hasErrors()) {
            model.addAttribute("stores", storeRepository.findAll());
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("permissions", permissionRepository.findAll());
            return "register";
        }
        
        // メールアドレスの重複チェック
        if (administratorRepository.findByEmail(form.getEmail()).isPresent()) {
            bindingResult.rejectValue("email", "error.email", "このメールアドレスは既に登録されています");
            model.addAttribute("stores", storeRepository.findAll());
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("permissions", permissionRepository.findAll());
            return "register";
        }
        
        // 管理者オブジェクトの作成と保存
        Administrator administrator = new Administrator();
        administrator.setStoreId(form.getStoreId());
        administrator.setLastName(form.getLastName());
        administrator.setFirstName(form.getFirstName());
        administrator.setEmail(form.getEmail());
        administrator.setPasswordHash(passwordEncoder.encode(form.getPassword()));
        administrator.setPhoneNumber(form.getPhoneNumber());
        administrator.setRoleId(form.getRoleId());
        administrator.setPermissionId(form.getPermissionId());
        
        LocalDateTime now = LocalDateTime.now();
        administrator.setCreatedAt(now);
        administrator.setUpdatedAt(now);
        
        administratorRepository.save(administrator);
        
        // 成功メッセージをセット
        redirectAttributes.addFlashAttribute("successMessage", "管理者登録が完了しました。ログイン画面からログインしてください。");
        
        return "redirect:/login";
    }
    
    @GetMapping("/dashboard")
    public String dashboard() {
        return "dashboard";
    }
}
