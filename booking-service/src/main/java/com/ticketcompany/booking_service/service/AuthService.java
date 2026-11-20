package com.ticketcompany.booking_service.service;

import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ticketcompany.booking_service.domain.User;
import com.ticketcompany.booking_service.exception.EmailAlreadyRegisteredException;
import com.ticketcompany.booking_service.exception.InvalidCredentialsException;
import com.ticketcompany.booking_service.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public String login(String rawEmail, String rawPassword) {
        String email = normalizeEmail(rawEmail);

        User user = userRepository.findByEmail(email)
                .orElseThrow(InvalidCredentialsException::new);

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        return tokenService.createToken(user.getEmail());
    }

    public void register(String rawEmail, String rawPassword) {
        String email = normalizeEmail(rawEmail);

        String passwordHash = passwordEncoder.encode(rawPassword);

        try {
            userRepository.save(new User(email, passwordHash));
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyRegisteredException(email);
        }
    }

    static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}