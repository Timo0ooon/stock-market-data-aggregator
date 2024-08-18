package ru.dima.solovev.authservice.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.dima.solovev.authservice.converters.UserConverter;
import ru.dima.solovev.authservice.models.dto.JwtRequest;
import ru.dima.solovev.authservice.models.dto.UserDTO;
import ru.dima.solovev.authservice.models.entities.User;
import ru.dima.solovev.authservice.util.JwtTokenUtils;

import java.util.List;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Transactional(readOnly = true)
@Slf4j
public class AuthenticationService {
    private final UserService userService;
    private final RoleService roleService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserConverter userConverter;
    private final JwtTokenUtils jwtTokenUtils;

    @Transactional
    public String registerNewUser(UserDTO userDTO) {
        User user = userConverter.convertToUser(userDTO);

        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        user.setRoles(List.of(roleService.findByName("ROLE_USER").orElseThrow(RuntimeException::new)));

        userService.saveUser(user);

        return jwtTokenUtils.generateToken(userDTO.getUsername());
    }


    public String authenticateUser(JwtRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        }
        catch (BadCredentialsException e) {
            log.error(e.getMessage(), e);
        }

        return jwtTokenUtils.generateToken(request.getUsername());
    }
}
