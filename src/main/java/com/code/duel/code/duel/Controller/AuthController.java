package com.code.duel.code.duel.Controller;

import com.code.duel.code.duel.Mappers.RequestMapper.UserRegisterRequest;
import com.code.duel.code.duel.Model.User;
import com.code.duel.code.duel.Service.AuthService;
import com.code.duel.code.duel.Exception.EmailAlreadyRegisteredException;
import com.code.duel.code.duel.Exception.UsernameAlreadyTakenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);


    @GetMapping("/me")
    public ResponseEntity<User> getCurrentUser(Authentication authentication) {

        User user;
        try {
            user = (User) authentication.getPrincipal();
        }catch (NullPointerException e){
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(user);
    }
    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody UserRegisterRequest request) {
        try {
            User newUser = authService.registerUser(request);
            return ResponseEntity.created(null).body(newUser);
        } catch (UsernameAlreadyTakenException e) {
            logger.info("Username already taken: {}", request.getUsername());
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (EmailAlreadyRegisteredException e) {
            logger.info("Email already registered: {}", request.getEmail());
            return ResponseEntity.status(409).body(e.getMessage());
        } catch (DataAccessException e) {
            logger.info("User registration failed with username: {}", request.getUsername());
            return ResponseEntity.status(409).build();
        }
    }


}
