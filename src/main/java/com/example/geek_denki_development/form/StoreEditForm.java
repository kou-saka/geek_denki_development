package com.example.geek_denki_development.form;

import jakarta.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class StoreEditForm {
    
    private Long id;
    private String name;
    
    @NotBlank(message = "住所を入力してください")
    private String address;
}
