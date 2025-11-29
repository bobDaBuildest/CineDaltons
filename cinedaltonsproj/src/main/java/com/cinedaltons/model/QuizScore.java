package com.cinedaltons.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entity που αποθηκεύει το αποτέλεσμα μίας μόνο συνεδρίας Quiz/Trivia για έναν χρήστη.
 * Επιτρέπει τη δημιουργία λεπτομερούς πίνακα κατάταξης και ιστορικού.
 */
@Entity
@Table(name = "quiz_scores")
@Data
@NoArgsConstructor
public class QuizScore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Σύνδεση με τον χρήστη που έπαιξε το Quiz.
     * Σχέση Many-to-One: Πολλά σκορ ανήκουν σε έναν χρήστη.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Το σκορ που επιτεύχθηκε στη συγκεκριμένη συνεδρία.
     */
    @Column(nullable = false)
    private int scoreAchieved;

    /**
     * Ο χρόνος που χρειάστηκε ο χρήστης για να ολοκληρώσει το Quiz (προαιρετικό).
     */
    private Long timeTakenSeconds;

    /**
     * Ημερομηνία και ώρα ολοκλήρωσης της συνεδρίας.
     */
    private LocalDateTime sessionDate = LocalDateTime.now();

    // ---------------------------------------------------------------------
    // Constructor για ευκολότερη δημιουργία score
    // ---------------------------------------------------------------------
    public QuizScore(User user, int scoreAchieved) {
        this.user = user;
        this.scoreAchieved = scoreAchieved;
    }
}