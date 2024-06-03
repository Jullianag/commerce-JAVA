package com.meusprojetos.commerce.dto;

import com.meusprojetos.commerce.entities.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class UserDTO {

    private Long id;
    @NotBlank(message = "Campo requerido")
    private String name;

    @Email(message = "Favor entrar com um email válido")
    private String email;
    private String phone;
    private LocalDate birthDate;

    private Set<RoleDTO> roles = new HashSet<>();

    public UserDTO() {
    }

    public UserDTO(User entity) {
        id = entity.getId();
        name = entity.getName();
        phone = entity.getPhone();
        email = entity.getEmail();
        birthDate = entity.getBirthDate();
        // como já foi usado o fetch na entidade User
        // pegamos a lista de role de dentro do User e adicionamos em RoleDTO
        entity.getRoles().forEach(role -> this.roles.add(new RoleDTO(role)));
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public Set<RoleDTO> getRoles() {
        return roles;
    }
}
