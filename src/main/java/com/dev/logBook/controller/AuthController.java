package com.dev.logBook.controller;


import com.dev.logBook.controller.dto.LoginDTO;
import com.dev.logBook.controller.dto.RegisterDTO;
import com.dev.logBook.services.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/auth")
public class AuthController {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping(value = "/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDTO register) {
        String token = authenticationService.register(register);
        return ResponseEntity.ok().body(token);
    }

    @PostMapping(value = "/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginDTO login) {
        String token = authenticationService.login(login);
        return ResponseEntity.ok().body(token);
    }

}
