package com.cinedaltons.model;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Entity που αντιπροσωπεύει έναν Ηθοποιό στην πλατφόρμα CineMatch.
 * Τα δεδομένα προέρχονται κυρίως από το TMDb.
 */
@Entity
@Table(name = "actors")
@Data // Lombok: Αυτόματα Getters, Setters, toString(), κλπ.
@NoArgsConstructor // Lombok: Constructor χωρίς ορίσματα (απαραίτητο για την JPA)
public class Actor {

    /**
     * Πρωτεύον κλειδί (Primary Key) του πίνακα actors.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Όνομα του Ηθοποιού (από TMDb).
     */
    @Column(nullable = false)
    private String name;

    /**
     * Βιογραφία ή σύντομη περιγραφή του Ηθοποιού (από TMDb).
     */
    @Column(columnDefinition = "TEXT") // Χρησιμοποιήστε TEXT για μεγάλες βιογραφίες
    private String biography;

    /**
     * Η διαδρομή (path) προς τη φωτογραφία/αφίσα του Ηθοποιού (απαιτείται για προβολή πληροφοριών [cite: 31]).
     */
    private String profilePath;

    /**
     * Η δημοτικότητα (popularity score) του Ηθοποιού (απαιτείται για λίστες trending [cite: 32]).
     */
    private Double popularity;

    // ---------------------------------------------------------------------
    // ΣΧΕΣΕΙΣ (Relationships)
    // ---------------------------------------------------------------------

    /**
     * Σχέση Many-to-Many με τις ταινίες στις οποίες έχει συμμετάσχει.
     * Χρειάζεται ενδιάμεσος πίνακας (join table) στη βάση δεδομένων.
     */
    /*
    @ManyToMany(mappedBy = "actors")
    private List<Movie> movies;
    */
}