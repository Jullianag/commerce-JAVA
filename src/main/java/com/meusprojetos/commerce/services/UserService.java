package com.meusprojetos.commerce.services;

import com.meusprojetos.commerce.entities.Role;
import com.meusprojetos.commerce.entities.User;
import com.meusprojetos.commerce.projections.UserDetailsProjection;
import com.meusprojetos.commerce.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository repository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);

        // ao invés de size, podemos usar isEmpty
        if (result.size() == 0) {
            throw new UsernameNotFoundException("Usuário não encontrado!");
        }

        User user = new User();
        user.setEmail(username);
        // ao invés de get, podemos usar getFirst
        user.setPassword(result.get(0).getPassword());
        for (UserDetailsProjection projection : result) {
            user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
        }
        return user;
    }
}
