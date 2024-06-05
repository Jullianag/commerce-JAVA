package com.meusprojetos.commerce.services;

import com.meusprojetos.commerce.entities.User;
import com.meusprojetos.commerce.repositories.PasswordRecoverRepository;
import com.meusprojetos.commerce.repositories.UserRepository;
import com.meusprojetos.commerce.services.exceptions.ForbiddenException;
import com.meusprojetos.commerce.tests.UserFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class AuthServiceTests {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserService userService;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordRecoverRepository passwordRecoverRepository;

    private User admin, selfClient, otherClient;

    @BeforeEach
    void setUp() throws Exception {

        admin = UserFactory.createAdmintUser();
        selfClient = UserFactory.createCustomClientnUser(1L, "Bob");
        otherClient = UserFactory.createCustomClientnUser(2L, "Ana");
    }

    @Test
    public void validateForAdminShouldDoNothingWhenAdminLogged() {

        Mockito.when(userService.authenticated()).thenReturn(admin);

        Long userId = admin.getId();

        Assertions.assertDoesNotThrow(() -> {
            authService.validateForAdmin(userId);
        });
    }

    @Test
    public void validateForAdminShouldDoNothingWhenSelfClientLogged() {

        Mockito.when(userService.authenticated()).thenReturn(selfClient);

        Long userId = selfClient.getId();

        Assertions.assertDoesNotThrow(() -> {
            authService.validateForAdmin(userId);
        });
    }

    @Test
    public void validateForAdminShouldThrowsForbiddenExceptionWhenOtherClientLogged() {

        Mockito.when(userService.authenticated()).thenReturn(selfClient);

        Long userId = otherClient.getId();

        Assertions.assertThrows(ForbiddenException.class, () -> {
            authService.validateForAdmin(userId);
        });
    }

}
