package com.cinedaltons.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*; // Για JPA annotations
import lombok.Data;          // Για getters/setters (προαιρετικό, αλλά συχνό)
import java.time.LocalDateTime;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "users") // Ορίζει το όνομα του πίνακα στη βάση δεδομένων
@Data // Από το Lombok: Αυτόματα getters, setters, toString, equals, hashCode
public class User {

    @Id // Ορίζει το πρωτεύον κλειδί
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Αυτόματη αύξηση (για PostgreSQL)
    private Long id;

    @Column(unique = true, nullable = false) // Πρέπει να είναι μοναδικό και όχι null
    private String username;
    @Column(nullable = false)
    private String firstName;
    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password; // Προσοχή: Αποθηκεύεται πάντα κρυπτογραφημένο (hashed)

    // Πεδίο απαραίτητο για τη λειτουργία Quiz/Trivia [cite: 38]
    private int totalQuizScore = 0;

    // Πρόσθετα πεδία για την αξιολόγηση και παρακολούθηση
    private LocalDateTime registrationDate = LocalDateTime.now();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<QuizScore> quizHistory = new ArrayList<>();

// ...
    // *******************************************************************
    // Σχέσεις (Relationships) με άλλες οντότητες
    // *******************************************************************

    /*
     * Αν ο χρήστης έχει ανεβάσει περιεχόμενο (π.χ., βίντεο/φωτογραφίες)[cite: 40],
     * τότε ορίζετε μια σχέση One-to-Many με την οντότητα Content:
     * * @OneToMany(mappedBy = "uploader", cascade = CascadeType.ALL)
     * private List<Content> uploadedContent;
     */

    /*
     * Αν ο χρήστης έχει σχολιάσει ή βαθμολογήσει συμμετοχές[cite: 41],
     * τότε ορίζετε σχέσεις με τις οντότητες Comment και Rating.
     * * @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
     * private List<Comment> comments;
     */

}