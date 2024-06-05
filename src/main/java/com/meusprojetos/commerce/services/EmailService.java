package com.meusprojetos.commerce.services;

import com.meusprojetos.commerce.config.Generated;
import com.meusprojetos.commerce.services.exceptions.EmailException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Generated
@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String emailForm;

    @Autowired
    private JavaMailSender emailSender;


    public void sendEmail(String to, String subject, String body) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(emailForm);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            emailSender.send(message);
        }
        catch (MailException e) {
            throw new EmailException("Failed to send email");
        }
    }
}
