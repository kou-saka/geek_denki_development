package com.example.geek_denki_development.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.geek_denki_development.entity.Administrator;
import com.example.geek_denki_development.repository.AdministratorRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private AdministratorRepository administratorRepository;
    
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Administrator administrator = administratorRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("メールアドレスが見つかりません: " + email));
        
        String role = administrator.getPermissionId() == 1L ? "ADMIN" : "USER";
        
        return User.builder()
            .username(administrator.getEmail())
            .password(administrator.getPasswordHash())
            .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role)))
            .build();
    }
}
