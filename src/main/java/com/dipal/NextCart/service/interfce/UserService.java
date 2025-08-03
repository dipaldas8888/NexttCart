package com.dipal.NextCart.service.interfce;

import com.dipal.NextCart.dto.Response;
import com.dipal.NextCart.dto.UserDTO;
import com.dipal.NextCart.entity.User;

import java.util.Optional;

public interface UserService {


    Response getAllUsers();
    User getLoginUser();
    Response getUserInfoAndOrderHistory();

    Response registerUserWithFirebase(UserDTO registrationRequest);

    Response setUserRole(String email, String role);

}