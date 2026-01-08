package com.cinedaltons.repository;

import com.cinedaltons.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Επιστρέφει true αν υπάρχει ήδη το username
    boolean existsByUsername(String username);

    //Βρίσκει χρήση με βάση το username
    Optional<User> findByUsername(String username);
}