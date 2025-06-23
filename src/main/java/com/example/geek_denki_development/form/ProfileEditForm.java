package com.example.geek_denki_development.form;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import lombok.Data;

@Data
public class ProfileEditForm {
    
    // ID（更新に必要）
    private Long id;
    
    // 店舗ID（表示のみ、変更不可）
    private Long storeId;
    
    @NotBlank(message = "姓を入力してください")
    @Size(min = 1, max = 50, message = "姓は1～50文字で入力してください")
    private String lastName;
    
    @NotBlank(message = "名を入力してください")
    @Size(min = 1, max = 50, message = "名は1～50文字で入力してください")
    private String firstName;
    
    @NotBlank(message = "メールアドレスを入力してください")
    @Email(message = "メールの形式で入力してください")
    private String email;
    
    // パスワードは空白可（変更しない場合は空白）
    @Size(min = 0, max = 12, message = "パスワードは4～12文字で入力してください")
    private String password;
    
    @NotBlank(message = "電話番号を入力してください")
    @Pattern(regexp = "^\\d{10,11}$", message = "電話番号は10～11文字の数字で入力してください")
    private String phoneNumber;
    
    private Long roleId;
    
    private Long permissionId;
    
    // パスワード変更するかどうかの判定メソッド
    public boolean isPasswordChangeRequired() {
        return password != null && !password.isEmpty();
    }
    
    // パスワードのバリデーション（入力がある場合のみ検証）
    @Size(min = 4, max = 12, message = "パスワードは4～12文字で入力してください")
    public String getPasswordForValidation() {
        if (isPasswordChangeRequired()) {
            return password;
        }
        return "dummypassword"; // ダミーの有効なパスワード
    }
}
