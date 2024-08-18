package ru.dima.solovev.authservice.controllers;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import ru.dima.solovev.authservice.models.dto.JwtRequest;
import ru.dima.solovev.authservice.models.dto.JwtResponse;
import ru.dima.solovev.authservice.models.dto.UserDTO;
import ru.dima.solovev.authservice.services.AuthenticationService;

@RestController
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/authentication")
    public ResponseEntity<JwtResponse> authenticateUser(@RequestBody JwtRequest request) {
        return ResponseEntity.ok(new JwtResponse(authenticationService.authenticateUser(request)));
    }

    @PostMapping("/registration")
    public ResponseEntity<JwtResponse> registerNewUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(new JwtResponse(authenticationService.registerNewUser(userDTO)));
    }
}
