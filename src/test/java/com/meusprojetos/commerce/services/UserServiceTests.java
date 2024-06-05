package com.meusprojetos.commerce.services;

import com.meusprojetos.commerce.dto.RoleDTO;
import com.meusprojetos.commerce.dto.UserDTO;
import com.meusprojetos.commerce.dto.UserInsertDTO;
import com.meusprojetos.commerce.dto.UserUpdateDTO;
import com.meusprojetos.commerce.entities.Role;
import com.meusprojetos.commerce.entities.User;
import com.meusprojetos.commerce.projections.UserDetailsProjection;
import com.meusprojetos.commerce.repositories.RoleRepository;
import com.meusprojetos.commerce.repositories.UserRepository;
import com.meusprojetos.commerce.services.exceptions.DatabaseException;
import com.meusprojetos.commerce.services.exceptions.ResourceNotFoundException;
import com.meusprojetos.commerce.tests.RoleFactory;
import com.meusprojetos.commerce.tests.UserDetailsFactory;
import com.meusprojetos.commerce.tests.UserFactory;
import com.meusprojetos.commerce.utils.CustomUserUtil;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;


@ExtendWith(SpringExtension.class)
public class UserServiceTests {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private CustomUserUtil userUtil;


    private List<UserDetailsProjection> userDetailsProjections;
    private User user;
    private UserDTO userDTO;
    private UserInsertDTO userInsertDTO;
    private PageImpl<User> page;
    private String roleClient;

    private Role role;
    private RoleDTO roleDTO;

    private Long existingUserId, nonExistingUserId, dependentUserId;
    private String existingUsername, nonExistingUsername;

    @BeforeEach
    void setUp() throws Exception {

        existingUserId = 1L;
        nonExistingUserId = 2L;
        dependentUserId = 3L;

        existingUsername = "maria@gmail.com";
        nonExistingUsername = "user@gmail.com";

        roleClient = "ROLE_CLIENT";
        role = RoleFactory.createRole();
        roleDTO = new RoleDTO(role);

        user = UserFactory.createCustomClientnUser(1L, existingUsername);
        userDetailsProjections = UserDetailsFactory.createCustomAdminUser(existingUsername);
        page = new PageImpl<>(List.of(user));
        userInsertDTO = new UserInsertDTO();

        Mockito.when(userRepository.searchUserAndRolesByEmail(existingUsername)).thenReturn(userDetailsProjections);
        Mockito.when(userRepository.searchUserAndRolesByEmail(nonExistingUsername)).thenReturn(new ArrayList<>());

        Mockito.when(userRepository.findByEmail(existingUsername)).thenReturn(user);
        Mockito.when(userRepository.findByEmail(nonExistingUsername)).thenThrow(UsernameNotFoundException.class);

        Mockito.when(userRepository.findAll((Pageable) any())).thenReturn(page);

        Mockito.when(userRepository.findById(existingUserId)).thenReturn(Optional.of(user));
        Mockito.when(userRepository.findById(nonExistingUserId)).thenReturn(Optional.empty());

        Mockito.when(userRepository.save(any())).thenReturn(user);

        Mockito.when(userRepository.getReferenceById(existingUserId)).thenReturn(user);
        Mockito.when(userRepository.getReferenceById(nonExistingUserId)).thenThrow(EntityNotFoundException.class);

        Mockito.when(userRepository.save(any())).thenReturn(user);

        Mockito.when(userRepository.existsById(existingUserId)).thenReturn(true);
        Mockito.when(userRepository.existsById(dependentUserId)).thenReturn(true);
        Mockito.when(userRepository.existsById(nonExistingUserId)).thenReturn(false);

        Mockito.doNothing().when(userRepository).deleteById(existingUserId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(userRepository).deleteById(dependentUserId);

    }

    @Test
    public void loadUserByUsernameShouldReturnUserDetailsWhenIdExists() {

        UserDetails result = userService.loadUserByUsername(existingUsername);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getUsername(), existingUsername);
    }

    @Test
    public void loadUserByUsernameShouldThrowsUserNotFoundExceptionWhenUserDoesNotExist() {

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(nonExistingUsername);
        });
    }

    @Test
    public void authenticatedShouldReturnWhenUserExists() {

        Mockito.when(userUtil.getLoggedUsername()).thenReturn(existingUsername);

        User result = userService.authenticated();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getUsername(), existingUsername);
    }

    @Test
    public void authenticatedShouldThrowUsernameNotFoundExceptionWhenUserDoesNotExist() {

        Mockito.doThrow(ClassCastException.class).when(userUtil).getLoggedUsername();

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            userService.authenticated();
        });
    }

    @Test
    public void getMeShouldReturnUserDTOWhenUserAuthenticated() {

        UserService spyUserService = Mockito.spy(userService);
        Mockito.doReturn(user).when(spyUserService).authenticated();

        UserDTO result = spyUserService.getMe();

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getEmail(), existingUsername);
        Assertions.assertEquals(result.getId(), user.getId());
    }

    @Test
    public void getMeShouldThrowsUserNotFoundExceptionWhenUserNotAuthenticated() {

        UserService spyUserService = Mockito.spy(userService);
        Mockito.doThrow(UsernameNotFoundException.class).when(spyUserService).authenticated();

        Assertions.assertThrows(UsernameNotFoundException.class, () -> {
            spyUserService.authenticated();
        });
    }

    @Test
    public void findAllShouldReturnPageUserDTO() {

        Pageable pageable = PageRequest.of(0, 12);
        Page<UserDTO> result = userService.findAllPaged(pageable);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getSize(), 1);
        Assertions.assertEquals(result.iterator().next().getEmail(), user.getEmail());
    }

    @Test
    public void findByIdShouldReturnUserDTOWhenIdExists() {

        UserDTO result = userService.findById(existingUserId);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingUserId);
        Assertions.assertEquals(result.getEmail(), user.getEmail());
    }

    @Test
    public void findByIdShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            userService.findById(nonExistingUserId);
        });

    }

    @Test
    public void insertShouldReturnUserDTO() {

        Mockito.when(roleRepository.getReferenceById(roleDTO.getId())).thenReturn(role);
        Mockito.when(roleRepository.findByAuthority(roleDTO.getAuthority())).thenReturn(role);
        Mockito.when(passwordEncoder.encode(userInsertDTO.getPassword())).thenReturn(String.valueOf(user));

        UserDTO result = userService.insert(userInsertDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingUserId);
    }

    @Test
    public void updateShouldThrowsEntityNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            userService.update(nonExistingUserId, new UserUpdateDTO());
        });
    }

    @Test
    public void updateShouldReturnUserDTOWhenIdExists() {

        UserDTO result = userService.update(existingUserId, new UserUpdateDTO());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(result.getId(), existingUserId);
    }

    @Test
    public void deleteShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            userService.delete(nonExistingUserId);
        });
    }

    @Test
    public void deleteShouldReturnDoNothingWhenIdExists() {

        Assertions.assertDoesNotThrow(() -> {
            userService.delete(existingUserId);
        });
    }

    @Test
    public void deleteShouldThrowsDatabaseExceptionWhenDependentId() {

        Assertions.assertThrows(DatabaseException.class, () -> {
            userService.delete(dependentUserId);
        });
    }

}
