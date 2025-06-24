package com.example.geek_denki_development.form;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class AdministratorEditForm {
    
    private Long id;
    
    @NotBlank(message = "姓を入力してください")
    @Size(min = 1, max = 50, message = "姓は1～50文字で入力してください")
    private String lastName;
    
    @NotBlank(message = "名を入力してください")
    @Size(min = 1, max = 50, message = "名は1～50文字で入力してください")
    private String firstName;
    
    @NotBlank(message = "電話番号を入力してください")
    @Pattern(regexp = "^\\d{10,11}$", message = "電話番号は10～11文字の数字で入力してください")
    private String phoneNumber;
    
    @NotNull(message = "役職を選択してください")
    private Long roleId;
    
    @NotNull(message = "権限を選択してください")
    private Long permissionId;
}
