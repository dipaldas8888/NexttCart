package com.dipal.NextCart.service.interfce;

import com.dipal.NextCart.dto.Response;
import com.dipal.NextCart.dto.UserDTO;
import com.dipal.NextCart.entity.User;

import java.util.Optional;

public interface UserService {
    // Deprecated or remove: Response registerUser(UserDTO registrationRequest);
    // Deprecated or remove: Response loginUser(LoginDTO loginRequest);

    Response getAllUsers();
    User getLoginUser();
    Response getUserInfoAndOrderHistory();

    // New: For backend user creation with Firebase (optional, if needed)
    Response registerUserWithFirebase(UserDTO registrationRequest);

    // New: To set role via Firebase custom claims
    Response setUserRole(String email, String role);

}