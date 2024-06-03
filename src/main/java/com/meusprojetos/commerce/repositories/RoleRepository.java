package com.meusprojetos.commerce.repositories;

import com.meusprojetos.commerce.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    Role findByAuthority(String authority);
}
