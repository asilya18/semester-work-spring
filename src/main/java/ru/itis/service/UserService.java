package ru.itis.service;

import ru.itis.dto.RegisterForm;
import ru.itis.model.User;

public interface UserService {
    User register(RegisterForm form);
    User findById(Long id);
    User findByEmail(String email);
    User findByUsername(String username);
}