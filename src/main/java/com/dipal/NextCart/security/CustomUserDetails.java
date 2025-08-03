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
        // Assuming you add a way to get role from claims or DB
        String role = user.getRole() != null ? user.getRole().name() : "USER";
        return List.of(new SimpleGrantedAuthority(role));
    }
    @Override
    public String getUsername() {
        return user.getFirebaseUid() != null ? user.getFirebaseUid() : user.getEmail();  // Add firebaseUid to User entity if needed
    }

    // Remove getPassword() as Firebase handles it (return null or empty)
    @Override
    public String getPassword() {
        return "";
    }
}
