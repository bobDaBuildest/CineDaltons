package com.cinedaltons.controller;

import com.movieapp.dto.UserRegisterDTO;
import com.movieapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // Inject το Interface, όχι το Implementation!
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody UserRegisterDTO userDto) {
        try {
            userService.registerUser(userDto);
            return ResponseEntity.ok("Ο χρήστης δημιουργήθηκε επιτυχώς!");
        } catch (Exception e) {
            // Επιστρέφει λάθος (400 Bad Request) και το μήνυμα "Το username υπάρχει ήδη"
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}