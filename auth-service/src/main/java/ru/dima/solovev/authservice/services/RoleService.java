package ru.dima.solovev.authservice.services;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ru.dima.solovev.authservice.models.entities.Role;
import ru.dima.solovev.authservice.repositories.RoleRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class RoleService {
    private final RoleRepository roleRepository;

    public Optional<Role> findByName(String name) { return roleRepository.findByName(name); }
}
