package com.dipal.NextCart.security;

import com.dipal.NextCart.entity.User;
import com.dipal.NextCart.exception.NotFoundException;
import com.dipal.NextCart.repository.UserRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {  // username can be UID or email
        return userRepo.findByEmail(username)  // Or add findByFirebaseUid
                .map(user -> CustomUserDetails.builder().user(user).build())
                .orElseGet(() -> {
                    try {
                        UserRecord firebaseUser = FirebaseAuth.getInstance().getUser(username);  // Or getUserByEmail
                        // Sync: Create new User in DB
                        User newUser = new User();
                        newUser.setEmail(firebaseUser.getEmail());
                        newUser.setFirebaseUid(firebaseUser.getUid());
                        // Set role from claims if needed
                        userRepo.save(newUser);
                        return CustomUserDetails.builder().user(newUser).build();
                    } catch (FirebaseAuthException e) {
                        throw new NotFoundException("User not found");
                    }
                });
    }
}