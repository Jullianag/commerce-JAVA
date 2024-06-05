package com.meusprojetos.commerce.tests;

import com.meusprojetos.commerce.entities.Role;

public class RoleFactory {

    public static Role createRole() {

        return new Role(1L, "ROLE_CLIENT");
    }

    public static Role createRole(Long id, String authority) {

        return new Role(id, authority);
    }
}
