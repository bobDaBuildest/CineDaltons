package com.cinedaltons.service.impl;

import com.cinedaltons.dto.UserRegisterDTO;
import com.cinedaltons.model.User;
import com.cinedaltons.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    // 1. Mock το Repository (για να μην χτυπάει βάση)
    @Mock
    private UserRepository userRepository;

    // 2. InjectMocks: Η υπηρεσία που τεστάρουμε
    @InjectMocks
    private UserServiceImpl userService;

    // --- ΣΕΝΑΡΙΟ 1: Επιτυχής Εγγραφή ---
    @Test
    void testRegisterUser_Success() throws Exception {
        // ARRANGE: Φτιάχνουμε τα δεδομένα
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("testUser");
        dto.setPassword("12345");
        dto.setEmail("test@email.com");

        // Λέμε στο Mock: "Όταν σε ρωτήσουν αν υπάρχει το 'testUser', πες ΟΧΙ (false)"
        when(userRepository.existsByUsername("testUser")).thenReturn(false);

        // ACT: Καλούμε τη μέθοδο
        userService.registerUser(dto);

        // ASSERT: Ελέγχουμε ότι κλήθηκε η save() 1 φορά
        verify(userRepository, times(1)).save(any(User.class));
    }

    // --- ΣΕΝΑΡΙΟ 2: Το Username υπάρχει ήδη (Exception) ---
    @Test
    void testRegisterUser_ThrowsException_WhenUserExists() {
        // ARRANGE
        UserRegisterDTO dto = new UserRegisterDTO();
        dto.setUsername("existingUser");

        // Λέμε στο Mock: "Όταν σε ρωτήσουν για το 'existingUser', πες ΝΑΙ (true)"
        when(userRepository.existsByUsername("existingUser")).thenReturn(true);

        // ACT & ASSERT: Ελέγχουμε αν πετάγεται Exception
        Exception exception = assertThrows(Exception.class, () -> {
            userService.registerUser(dto);
        });

        // Ελέγχουμε και το μήνυμα του λάθους
        assertEquals("Το username 'existingUser' υπάρχει ήδη.", exception.getMessage());

        // Βεβαιωνόμαστε ότι ΔΕΝ προσπάθησε να κάνει save
        verify(userRepository, never()).save(any());
    }
}