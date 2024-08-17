package ru.dima.solovev.authservice.services;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.dima.solovev.authservice.models.entities.User;
import ru.dima.solovev.authservice.repositories.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
@Transactional(readOnly = true)
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;

    public Optional<User> findByUsername(String username) { return userRepository.findByUsername(username); }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = findByUsername(username).orElseThrow(() -> new UsernameNotFoundException(String.format("User '%s' not found!", username)));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).toList()
        ) {
        };
    }

    @Transactional
    public void saveUser(User user) { userRepository.save(user); }
}
