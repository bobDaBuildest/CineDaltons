package com.cinedaltons.repository;

import com.cinedaltons.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Επιστρέφει true αν υπάρχει ήδη το username
    boolean existsByUsername(String username);
}