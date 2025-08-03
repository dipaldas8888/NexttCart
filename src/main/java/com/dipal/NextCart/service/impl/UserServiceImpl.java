package com.dipal.NextCart.service.impl;

import com.dipal.NextCart.dto.Response;
import com.dipal.NextCart.dto.UserDTO;
import com.dipal.NextCart.entity.User;
import com.dipal.NextCart.enums.UserRole;
import com.dipal.NextCart.exception.NotFoundException;
import com.dipal.NextCart.mapper.EntityDtoMapper;
import com.dipal.NextCart.repository.UserRepo;
import com.dipal.NextCart.service.interfce.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final EntityDtoMapper entityDtoMapper;


    @Override
    public Response getAllUsers() {
        List<User> users = userRepo.findAll();
        List<UserDTO> userDtos = users.stream()
                .map(entityDtoMapper::mapUserToDtoBasic)
                .toList();

        return Response.builder()
                .status(200)
                .userList(userDtos)
                .build();
    }

    @Override
    public User getLoginUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new UsernameNotFoundException("No authenticated user");
        }
        String uid = authentication.getName();  // Now UID from Firebase token (update CustomUserDetails to set username as UID)
        log.info("User Firebase UID is: {}", uid);
        return userRepo.findByFirebaseUid(uid)  // Assumes you added findByFirebaseUid to UserRepo
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    @Override
    public Response getUserInfoAndOrderHistory() {
        User user = getLoginUser();
        UserDTO userDto = entityDtoMapper.mapUserToDtoPlusAddressAndOrderHistory(user);

        return Response.builder()
                .status(200)
                .user(userDto)
                .build();
    }

    // New: Backend registration with Firebase (optional; use if client-side isn't sufficient)
    @Override
    public Response registerUserWithFirebase(UserDTO registrationRequest) {
        try {
            // Create user in Firebase
            UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                    .setEmail(registrationRequest.getEmail())
                    .setDisplayName(registrationRequest.getName())
                    .setPhoneNumber(registrationRequest.getPhoneNumber())
                    .setPassword(registrationRequest.getPassword())  // Only if allowing backend password set; otherwise, skip and let client handle
                    .setEmailVerified(false);
            UserRecord firebaseUser = FirebaseAuth.getInstance().createUser(request);

            UserRole role = registrationRequest.getRole() != null && registrationRequest.getRole().equalsIgnoreCase("ADMIN") ? UserRole.ADMIN : UserRole.USER;
            User user = User.builder()
                    .name(registrationRequest.getName())
                    .email(registrationRequest.getEmail())
                    .phoneNumber(registrationRequest.getPhoneNumber())
                    .firebaseUid(firebaseUser.getUid())
                    .role(role)
                    .build();
            User savedUser = userRepo.save(user);

            Map<String, Object> claims = new HashMap<>();
            claims.put("role", role.name());
            FirebaseAuth.getInstance().setCustomUserClaims(firebaseUser.getUid(), claims);

            UserDTO userDto = entityDtoMapper.mapUserToDtoBasic(savedUser);
            return Response.builder()
                    .status(200)
                    .message("User successfully added with Firebase")
                    .user(userDto)
                    .build();
        } catch (FirebaseAuthException e) {
            log.error("Firebase error during registration: {}", e.getMessage());
            throw new RuntimeException("Failed to register user with Firebase");
        }
    }

    @Override
    public Response setUserRole(String email, String role) {
        try {
            UserRecord firebaseUser = FirebaseAuth.getInstance().getUserByEmail(email);
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", role.toUpperCase());  // e.g., "ADMIN"
            FirebaseAuth.getInstance().setCustomUserClaims(firebaseUser.getUid(), claims);

            User dbUser = userRepo.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("User not found"));
            dbUser.setRole(UserRole.valueOf(role.toUpperCase()));
            userRepo.save(dbUser);

            return Response.builder()
                    .status(200)
                    .message("User role updated successfully")
                    .build();
        } catch (FirebaseAuthException e) {
            log.error("Firebase error setting role: {}", e.getMessage());
            throw new RuntimeException("Failed to set user role");
        }
    }


}