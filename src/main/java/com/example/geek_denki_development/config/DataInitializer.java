// DataInitializer.java
package com.example.geek_denki_development.config;

import java.time.LocalDateTime;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.geek_denki_development.entity.Administrator;
import com.example.geek_denki_development.entity.Permission;
import com.example.geek_denki_development.entity.Role;
import com.example.geek_denki_development.entity.Store;
import com.example.geek_denki_development.repository.AdministratorRepository;
import com.example.geek_denki_development.repository.PermissionRepository;
import com.example.geek_denki_development.repository.RoleRepository;
import com.example.geek_denki_development.repository.StoreRepository;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            AdministratorRepository administratorRepository,
            StoreRepository storeRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            PasswordEncoder passwordEncoder) {
        return args -> {
            // 店舗データの初期化
            Store store1 = new Store();
            store1.setName("Geek電機 渋谷店");
            store1.setAddress("東京都渋谷区Geek坂 1-1-1");
            storeRepository.save(store1);
            
            Store store2 = new Store();
            store2.setName("Geek電機 新宿店");
            store2.setAddress("東京都新宿区西Geek 1-1-1");
            storeRepository.save(store2);
            
            Store store3 = new Store();
            store3.setName("Geek電機 池袋店");
            store3.setAddress("東京都豊島区GeekShine通り 1-1-1");
            storeRepository.save(store3);
            
            // 役職データの初期化
            String[] roleNames = {"店長", "副店長", "マネージャー", "一般従業員", "パート・アルバイト"};
            for (String roleName : roleNames) {
                Role role = new Role();
                role.setName(roleName);
                roleRepository.save(role);
            }
            
            // 権限データの初期化
            String[] permissionNames = {"管理者", "一般"};
            for (String permissionName : permissionNames) {
                Permission permission = new Permission();
                permission.setName(permissionName);
                permissionRepository.save(permission);
            }
            
            // 管理者アカウントの作成
            Administrator admin = new Administrator();
            admin.setStoreId(1L);
            admin.setFirstName("管理者");
            admin.setLastName("テスト");
            admin.setEmail("admin@example.com");
            admin.setPasswordHash(passwordEncoder.encode("password123"));
            admin.setRoleId(1L);
            admin.setPermissionId(1L);
            admin.setPhoneNumber("0312345678");
            admin.setCreatedAt(LocalDateTime.now());
            admin.setUpdatedAt(LocalDateTime.now());
            
            administratorRepository.save(admin);
            
            System.out.println("初期データを登録しました");
        };
    }
}
