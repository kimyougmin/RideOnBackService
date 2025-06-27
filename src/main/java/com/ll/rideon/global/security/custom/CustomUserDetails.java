package com.ll.rideon.global.security.custom;

import com.ll.rideon.domain.users.entity.Users;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Users users;

    public CustomUserDetails(Users users) {
        this.users = users;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 권한은 추후 추가 가능
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return users.getPassword();
    }

    @Override
    public String getUsername() {
        return users.getEmail(); // 이메일을 로그인 ID로 사용
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 필요 시 조건 설정 가능
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 필요 시 조건 설정 가능
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 필요 시 조건 설정 가능
    }

    @Override
    public boolean isEnabled() {
        return true; // 필요 시 조건 설정 가능
    }
}
