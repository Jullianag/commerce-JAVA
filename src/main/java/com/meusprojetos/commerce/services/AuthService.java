package com.meusprojetos.commerce.services;

import com.meusprojetos.commerce.dto.EmailDTO;
import com.meusprojetos.commerce.dto.NewPasswordDTO;
import com.meusprojetos.commerce.entities.PasswordRecover;
import com.meusprojetos.commerce.entities.User;
import com.meusprojetos.commerce.repositories.PasswordRecoverRepository;
import com.meusprojetos.commerce.repositories.UserRepository;
import com.meusprojetos.commerce.services.exceptions.ForbiddenException;
import com.meusprojetos.commerce.services.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AuthService {

    @Value("${email.password-recover.token.minutes}")
    private Long tokenMinutes;

    @Value("${email.password-recover.uri}")
    private String recoverUri;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordRecoverRepository passwordRecoverRepository;

    public void validateForAdmin(Long userId) {
        User me = userService.authenticated();

        if (me.hasRole("ROLE_ADMIN")) {
            return;

        }
        if (!me.getId().equals(userId)) {
            throw new ForbiddenException("Acesso negado!");
        }
    }

    @Transactional
    public void saveNewPassword(NewPasswordDTO body) {

        List<PasswordRecover> result = passwordRecoverRepository.searchValidToken(body.getToken(), Instant.now());

        if (result.isEmpty()) {
            throw new ResourceNotFoundException("Token inválido");
        }

        User user = userRepository.findByEmail(result.getFirst().getEmail());
        user.setPassword(passwordEncoder.encode(body.getPassword()));
        user = userRepository.save(user);
    }

    @Transactional
    public void createRecoverToken(EmailDTO body) {

        User user = userRepository.findByEmail(body.getEmail());

        if (user == null) {
            throw new ResourceNotFoundException("Email não encontrado");
        }

        String token = UUID.randomUUID().toString();

        PasswordRecover entity = new PasswordRecover();
        entity.setEmail(body.getEmail());
        entity.setToken(token);
        entity.setExpiration(Instant.now().plusSeconds(tokenMinutes * 60));
        entity = passwordRecoverRepository.save(entity);

        // corpo do email
        String text = "Acesse o link para definir uma nova senha\n\n" +
                recoverUri + token + ". Validade de " + tokenMinutes + " minutos.";

        emailService.sendEmail(body.getEmail(), "Recuperação de senha", text);
    }
}
