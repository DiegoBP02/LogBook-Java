package com.dev.logBook.controller;


import com.dev.logBook.controller.dto.LoginDTO;
import com.dev.logBook.controller.dto.RegisterDTO;
import com.dev.logBook.entities.User;
import com.dev.logBook.services.AuthenticationService;
import com.dev.logBook.services.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    public ResponseEntity<String> register(@Valid RegisterDTO register) {
        String token = authenticationService.register(register);
        return ResponseEntity.ok().body(token);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<String>  login(@Valid LoginDTO login) {

        String token = authenticationService.login(login);
        return ResponseEntity.ok().body(token);
    }

}
