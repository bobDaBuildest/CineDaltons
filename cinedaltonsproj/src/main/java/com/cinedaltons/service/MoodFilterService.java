package com.cinedaltons.service;

import com.cinedaltons.dto.TmdbMovieDto;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MoodFilterService {

    private final TmdbService tmdbService;
    private final HuggingFaceService huggingFaceService;

    public MoodFilterService(TmdbService tmdbService, HuggingFaceService huggingFaceService) {
        this.tmdbService = tmdbService;
        this.huggingFaceService = huggingFaceService;
    }

    public List<TmdbMovieDto> findMoviesByMood(String userMood) {
        System.out.println("--- ΞΕΚΙΝΑΕΙ Η ΑΝΑΖΗΤΗΣΗ ΓΙΑ MOOD: " + userMood + " ---");

        // 1. Φέρνουμε ταινίες
        List<TmdbMovieDto> movies = tmdbService.getPopularMovies();
        System.out.println("Βρέθηκαν " + movies.size() + " ταινίες από το TMDB.");

        if (movies.isEmpty()) {
            System.out.println("ΠΡΟΒΛΗΜΑ: Το TMDB επέστρεψε κενή λίστα!");
            return movies;
        }

        // 2. Φιλτράρισμα
        return movies.parallelStream()
                .filter(movie -> {
                    String plot = movie.getOverview();
                    // Ανάλυση
                    String detectedEmotion = huggingFaceService.analyzeEmotion(plot);

                    // ΕΚΤΥΠΩΣΗ ΓΙΑ DEBUGGING
                    System.out.println("Ταινία: " + movie.getTitle() + " -> Emotion: " + detectedEmotion);

                    boolean match = isMoodMatch(userMood, detectedEmotion);
                    if (match) System.out.println(">>> ΚΡΑΤΑΜΕ ΤΗΝ ΤΑΙΝΙΑ: " + movie.getTitle());

                    return match;
                })
                .limit(10)
                .collect(Collectors.toList());
    }

    // Αντιστοίχιση του τι γράφει ο χρήστης με το τι βγάζει το μοντέλο
    // Ενημερωμένη μέθοδος για να ταιριάζει με τις απαντήσεις του Mistral
    private boolean isMoodMatch(String userQuery, String aiEmotion) {
        if (userQuery == null || aiEmotion == null) return false;

        String query = userQuery.toLowerCase().trim();
        String emotion = aiEmotion.toLowerCase().trim();

        // Debugging για να βλέπεις τι γίνεται στην κονσόλα
        // System.out.println("User wants: " + query + " | AI found: " + emotion);

        switch (query) {
            case "happy":
                return emotion.contains("happy") || emotion.contains("joy") || emotion.contains("fun");
            case "sad":
                return emotion.contains("sad") || emotion.contains("drama") || emotion.contains("melancholy");
            case "scary":
                return emotion.contains("scary") || emotion.contains("horror") || emotion.contains("fear");
            case "tense":
                return emotion.contains("tense") || emotion.contains("angry") || emotion.contains("thriller");
            case "exciting":
                return emotion.contains("exciting") || emotion.contains("surprise") || emotion.contains("action");
            default:
                return false;
        }


    }
}
