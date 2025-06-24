package com.example.geek_denki_development.controller;

import java.time.LocalDateTime;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.geek_denki_development.entity.Administrator;
import com.example.geek_denki_development.entity.Permission;
import com.example.geek_denki_development.entity.Role;
import com.example.geek_denki_development.form.AdministratorEditForm;
import com.example.geek_denki_development.repository.AdministratorRepository;
import com.example.geek_denki_development.repository.PermissionRepository;
import com.example.geek_denki_development.repository.RoleRepository;


@Controller
public class AdministratorController {

    @Autowired
    private AdministratorRepository administratorRepository;

    @GetMapping("/administrators")
    public String listAdministrators(Model model) {
        // 全管理者情報を取得
        model.addAttribute("administrators", administratorRepository.findAll());
        
        try {
            // 現在のユーザーの権限情報を取得して追加
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName();
            
            Administrator currentAdmin = administratorRepository.findByEmail(currentUserEmail)
                    .orElse(null);
            
            // 管理者が見つかった場合のみ権限情報を追加
            if (currentAdmin != null) {
                model.addAttribute("isAdmin", currentAdmin.getPermissionId() == 1L);
            } else {
                model.addAttribute("isAdmin", false);
            }
        } catch (Exception e) {
            // エラーが発生した場合は管理者権限なしとする
            model.addAttribute("isAdmin", false);
        }
        
        return "administrators/list";
    }
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @GetMapping("/administrators/{id}")
    public String viewAdministrator(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // 管理者情報を取得
            Administrator administrator = administratorRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("管理者が見つかりません。ID: " + id));
            
            // 関連情報を取得
            Role role = roleRepository.findById(administrator.getRoleId())
                    .orElseThrow(() -> new RuntimeException("役職情報が見つかりません"));
            
            Permission permission = permissionRepository.findById(administrator.getPermissionId())
                    .orElseThrow(() -> new RuntimeException("権限情報が見つかりません"));
            
            // モデルに情報を追加
            model.addAttribute("administrator", administrator);
            model.addAttribute("roleName", role.getName());
            model.addAttribute("permissionName", permission.getName());
            
            return "administrators/detail";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "指定された管理者情報が見つかりませんでした");
            return "redirect:/administrators";
        }
    }
 // 編集画面の表示
    @GetMapping("/administrators/{id}/edit")
    public String editAdministratorForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            // 現在ログインしている管理者情報を取得
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String currentUserEmail = authentication.getName();
            Administrator currentAdmin = administratorRepository.findByEmail(currentUserEmail)
                    .orElseThrow(() -> new RuntimeException("ログイン情報が見つかりません"));
            
            // 編集対象の管理者情報を取得
            Administrator administrator = administratorRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("管理者が見つかりません。ID: " + id));
            
            // 自分自身の編集の場合はプロフィール編集画面にリダイレクト
            if (administrator.getId().equals(currentAdmin.getId())) {
                return "redirect:/profile/edit";
            }
            
            // フォームに値をセット
            AdministratorEditForm form = new AdministratorEditForm();
            form.setId(administrator.getId());
            form.setLastName(administrator.getLastName());
            form.setFirstName(administrator.getFirstName());
            form.setPhoneNumber(administrator.getPhoneNumber());
            form.setRoleId(administrator.getRoleId());
            form.setPermissionId(administrator.getPermissionId());
            
            // モデルに情報を追加
            model.addAttribute("administratorEditForm", form);
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("permissions", permissionRepository.findAll());
            
            return "administrators/edit";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "指定された管理者情報が見つかりませんでした");
            return "redirect:/administrators";
        }
    }

    // 管理者情報の更新処理
    @PostMapping("/administrators/{id}/edit")
    public String updateAdministrator(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("administratorEditForm") AdministratorEditForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        // バリデーションエラーがある場合は編集画面に戻る
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", roleRepository.findAll());
            model.addAttribute("permissions", permissionRepository.findAll());
            return "administrators/edit";
        }
        
        try {
            // 対象の管理者を取得
            Administrator administrator = administratorRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("管理者が見つかりません。ID: " + id));
            
            // 権限が変更されたかチェック
            boolean permissionChanged = !administrator.getPermissionId().equals(form.getPermissionId());
            
            // 管理者情報を更新
            administrator.setLastName(form.getLastName());
            administrator.setFirstName(form.getFirstName());
            administrator.setPhoneNumber(form.getPhoneNumber());
            administrator.setRoleId(form.getRoleId());
            administrator.setPermissionId(form.getPermissionId());
            administrator.setUpdatedAt(LocalDateTime.now());
            
            // 保存
            administratorRepository.save(administrator);
            
            // 権限変更時は再ログインが必要
            if (permissionChanged) {
                redirectAttributes.addFlashAttribute("successMessage", 
                    "管理者情報を更新しました。権限が変更されたため、再ログインが必要です。");
                return "redirect:/login";
            }
            
            // 成功メッセージをセット
            redirectAttributes.addFlashAttribute("successMessage", "管理者情報を更新しました");
            
            // 詳細画面にリダイレクト
            return "redirect:/administrators/" + id;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "管理者情報の更新に失敗しました");
            return "redirect:/administrators";
        }
    }




}
