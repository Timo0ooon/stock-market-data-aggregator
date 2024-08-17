package ru.dima.solovev.authservice.converters;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import ru.dima.solovev.authservice.models.dto.UserDTO;
import ru.dima.solovev.authservice.models.entities.User;
import ru.dima.solovev.authservice.services.RoleService;

import java.util.List;

@Component
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class UserConverter {
    private final ModelMapper modelMapper;
    private final RoleService roleService;

    public UserDTO convertToUserDTO(User user) {
        return modelMapper.map(user, UserDTO.class);
    }

    public User convertToUser(UserDTO userDTO) {
        User user = modelMapper.map(userDTO, User.class);
        user.setRoles(List.of(roleService.findByName("ROLE_USER").orElseThrow(RuntimeException::new)));

        return user;
    }
}
