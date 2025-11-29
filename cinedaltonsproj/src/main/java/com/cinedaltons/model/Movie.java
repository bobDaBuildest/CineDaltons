package com.cinedaltons.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

/**
 * Entity που αντιπροσωπεύει μια Ταινία στην πλατφόρμα CineMatch.
 * Τα δεδομένα προέρχονται από ανοιχτές πηγές όπως το TMDb.
 */
@Entity
@Table(name = "movies")
@Data // Lombok: Αυτόματα Getters, Setters, κλπ.
@NoArgsConstructor // Lombok: Constructor χωρίς ορίσματα (απαραίτητο για την JPA)
public class Movie {

    /**
     * Πρωτεύον κλειδί (Primary Key).
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Τίτλος της ταινίας.
     */
    @Column(nullable = false)
    private String title;

    /**
     * Περιγραφή της ταινίας.
     */
    @Column(columnDefinition = "TEXT")
    private String overview;

    /**
     * Ημερομηνία κυκλοφορίας της ταινίας.
     */
    private LocalDate releaseDate;

    /**
     * Η διαδρομή (path) προς την αφίσα της ταινίας.
     */
    private String posterPath;

    /**
     * Βαθμός δημοτικότητας (από TMDb) (απαιτείται για λίστες trending).
     */
    private Double popularity;

    /**
     * Λίστα με τα είδη (genres) της ταινίας.
     */
    @ElementCollection(fetch = FetchType.EAGER) // Αποθήκευση λίστας βασικών τύπων δεδομένων
    @CollectionTable(name = "movie_genres", joinColumns = @JoinColumn(name = "movie_id"))
    @Column(name = "genre")
    private List<String> genres;

    // ---------------------------------------------------------------------
    // ΣΧΕΣΕΙΣ (Relationships)
    // ---------------------------------------------------------------------

    /**
     * Σχέση Many-to-Many με Ηθοποιούς (Actors).
     * Η ταινία έχει πολλούς ηθοποιούς και ο ηθοποιός συμμετέχει σε πολλές ταινίες.
     * Χρειάζεται ενδιάμεσος πίνακας (join table).
     */
    /*
    @ManyToMany
    @JoinTable(
        name = "movie_cast",
        joinColumns = @JoinColumn(name = "movie_id"),
        inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    private List<Actor> actors;
    */

    // ---------------------------------------------------------------------
    // ΠΕΔΙΑ ΓΙΑ ΤΙΣ ΕΥΦΥΕΙΣ ΛΕΙΤΟΥΡΓΙΕΣ (KPIs)
    // ---------------------------------------------------------------------

    /**
     * Εσωτερικός δείκτης ΚΡΙς: Audience Engagement Score
     */
    private Double audienceEngagementScore;

    /**
     * Εσωτερικός δείκτης ΚΡΙς: Awards Potential
     */
    private Double awardsPotential;

    // Μπορείτε να προσθέσετε και άλλα πεδία ΚΡΙς εδώ, όπως το Box Office Popularity Proxy.
}