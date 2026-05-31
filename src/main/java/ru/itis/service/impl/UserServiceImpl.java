package ru.itis.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.dto.RegisterForm;
import ru.itis.exception.AlreadyExistsException;
import ru.itis.exception.NotFoundException;
import ru.itis.model.User;
import ru.itis.model.enums.Role;
import ru.itis.repository.UserRepository;
import ru.itis.service.UserService;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public User register(RegisterForm form) {
        if (userRepository.existsByEmail(form.getEmail())) {
            log.error("registration failed: email already exists {}", form.getEmail());
            throw new AlreadyExistsException("email уже занят");
        }
        if (userRepository.existsByUsername(form.getUsername())) {
            log.error("registration failed: username already exists {}", form.getUsername());
            throw new AlreadyExistsException("имя пользователя уже занято");
        }
        User user = new User();
        user.setUsername(form.getUsername());
        user.setEmail(form.getEmail());
        user.setPassword(passwordEncoder.encode(form.getPassword()));
        user.setRole(Role.USER);
        return userRepository.save(user);
    }

    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("пользователь не найден"));
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("пользователь не найден"));
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("пользователь не найден"));
    }
}