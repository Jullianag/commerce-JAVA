package com.meusprojetos.commerce.controllers;

import com.meusprojetos.commerce.dto.UserDTO;
import com.meusprojetos.commerce.dto.UserInsertDTO;
import com.meusprojetos.commerce.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping(value = "/users")
public class UserController {

    @Autowired
    private UserService service;

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
    @GetMapping(value = "/me")
    public ResponseEntity<UserDTO> getMe() {
        UserDTO dto = service.getMe();
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<Page<UserDTO>> findAll(Pageable pageable) {
        Page<UserDTO> result = service.findAllPaged(pageable);
        return ResponseEntity.ok().body(result);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
        UserDTO dto = service.findById(id);
        return ResponseEntity.ok().body(dto);
    }

    @PostMapping
    public ResponseEntity<UserDTO> insert(@Valid @RequestBody UserInsertDTO dto) {
        UserDTO newDTO = service.insert(dto);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newDTO.getId()).toUri();
        return ResponseEntity.created(uri).body(newDTO);
    }
}
