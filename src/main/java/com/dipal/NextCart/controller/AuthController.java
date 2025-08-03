package com.dipal.NextCart.controller;



import com.dipal.NextCart.dto.LoginDTO;
import com.dipal.NextCart.dto.Response;
import com.dipal.NextCart.dto.UserDTO;
import com.dipal.NextCart.service.interfce.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Response> registerUser(@RequestBody UserDTO registrationRequest){
        return ResponseEntity.ok(userService.registerUserWithFirebase(registrationRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<Response> loginUser(@RequestBody LoginDTO loginRequest){
        throw new UnsupportedOperationException("Use client-side Firebase login");
    }
}