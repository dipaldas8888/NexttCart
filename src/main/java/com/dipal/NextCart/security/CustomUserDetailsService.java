package com.dipal.NextCart.security;

import com.dipal.NextCart.entity.User;
import com.dipal.NextCart.enums.UserRole;
import com.dipal.NextCart.repository.UserRepo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // First try to find by email
        Optional<User> userByEmail = userRepo.findByEmail(username);
        if (userByEmail.isPresent()) {
            return CustomUserDetails.builder().user(userByEmail.get()).build();
        }

        // If not found by email, try to find by Firebase UID
        Optional<User> userByFirebaseUid = userRepo.findByFirebaseUid(username);
        if (userByFirebaseUid.isPresent()) {
            return CustomUserDetails.builder().user(userByFirebaseUid.get()).build();
        }

        // If user doesn't exist, try to get from Firebase and create new user
        try {
            UserRecord firebaseUser = FirebaseAuth.getInstance().getUser(username);
            // Check if user exists with this email before creating
            Optional<User> existingUser = userRepo.findByEmail(firebaseUser.getEmail());
            if (existingUser.isPresent()) {
                return CustomUserDetails.builder().user(existingUser.get()).build();
            }

            // Create new user only if doesn't exist
            User newUser = new User();
            newUser.setEmail(firebaseUser.getEmail());
            newUser.setFirebaseUid(firebaseUser.getUid());
            newUser.setName(firebaseUser.getDisplayName());
            newUser.setRole(UserRole.USER); // Set default role
            User savedUser = userRepo.save(newUser);
            return CustomUserDetails.builder().user(savedUser).build();
        } catch (FirebaseAuthException e) {
            throw new UsernameNotFoundException("User not found in Firebase: " + e.getMessage());
        }
    }
}
