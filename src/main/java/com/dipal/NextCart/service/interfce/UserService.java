package com.dipal.NextCart.service.interfce;

import com.dipal.NextCart.dto.LoginDTO;
import com.dipal.NextCart.dto.Response;
import com.dipal.NextCart.dto.UserDTO;
import com.dipal.NextCart.entity.User;

public interface UserService {
    Response registerUser(UserDTO registrationRequest);
    Response loginUser(LoginDTO loginRequest);
    Response getAllUsers();
    User getLoginUser();
    Response getUserInfoAndOrderHistory();
}