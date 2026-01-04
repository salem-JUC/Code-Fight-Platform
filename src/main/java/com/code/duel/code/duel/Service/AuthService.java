package com.code.duel.code.duel.Service;

import com.code.duel.code.duel.Mappers.RequestMapper.UserRegisterRequest;
import com.code.duel.code.duel.Model.User;
import com.code.duel.code.duel.Repository.UserRepo;
import com.code.duel.code.duel.Exception.UsernameAlreadyTakenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final AtomicLong idGenerator = new AtomicLong(1004L);

    @Autowired
    public AuthService(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public User registerUser(UserRegisterRequest request) {
        logger.info("Registering attempt new user with username: {}", request.getUsername());

        if (userRepo.findByUsername(request.getUsername()).isPresent()) {
            throw new UsernameAlreadyTakenException(request.getUsername());
        }

        long userId = idGenerator.getAndIncrement();
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole("PLAYER");
        newUser.setUserID(userId);
        newUser.setScore(0);

        try {
            userRepo.save(newUser);
        } catch (DataAccessException e) {
            
            logger.info("User registration failed with username: {}", request.getUsername());
            logger.error("Database exception during registration for username '{}'. Reason: {}", request.getUsername(), e.getMessage(), e);
            throw e;
        }

        logger.info("User registered successfully with username: {}", request.getUsername());
        return newUser;
    }
}

