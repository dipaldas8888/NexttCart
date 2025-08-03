package com.dipal.NextCart.security;


import com.dipal.NextCart.entity.User;
import lombok.Builder;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@Builder
public class CustomUserDetails implements UserDetails {

    private User user;


    

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String role = user.getRole() != null ? user.getRole().name() : "USER";
        return List.of(new SimpleGrantedAuthority(role));
    }
    @Override
    public String getUsername() {
        return user.getFirebaseUid() != null ? user.getFirebaseUid() : user.getEmail();  // Add firebaseUid to User entity if needed
    }

    @Override
    public String getPassword() {
        return "";
    }
}
