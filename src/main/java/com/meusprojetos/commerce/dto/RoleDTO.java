package com.meusprojetos.commerce.dto;

import com.meusprojetos.commerce.entities.Role;

public class RoleDTO {

    private Long id;
    private String authority;

    public RoleDTO() {
    }

    public RoleDTO(String authority, Long id) {
        this.authority = authority;
        this.id = id;
    }

    public RoleDTO(Role role) {
        id = role.getId();
        authority = role.getAuthority();
    }

    public Long getId() {
        return id;
    }

    public String getAuthority() {
        return authority;
    }
}
