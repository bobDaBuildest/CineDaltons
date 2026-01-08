
package com.cinedaltons.service;

import com.cinedaltons.dto.TmdbMovieDto;
import com.cinedaltons.model.QuizQuestion;
import com.cinedaltons.model.QuizScore;
import com.cinedaltons.model.User;
import com.cinedaltons.repository.QuizScoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class QuizService {

    private final TmdbService tmdbService;
    private final QuizScoreRepository quizScoreRepository;
    private final HuggingFaceService huggingFaceService;

    // Αποθήκευση ενεργών sessions
    private final Map<String, QuizSession> activeSessions = new ConcurrentHashMap<>();

    @Autowired
    public QuizService(TmdbService tmdbService,
                       QuizScoreRepository quizScoreRepository,
                       HuggingFaceService huggingFaceService) {
        this.tmdbService = tmdbService;
        this.quizScoreRepository = quizScoreRepository;
        this.huggingFaceService = huggingFaceService;
    }

    /**
     * Δημιουργεί μια νέα quiz session με AI-generated ερωτήσεις
     */
    public String createQuizSession(int numberOfQuestions) {
        String sessionId = UUID.randomUUID().toString();

        // Παίρνουμε δημοφιλείς ταινίες
        List<TmdbMovieDto> movies = tmdbService.getPopularMovies();

        // Δημιουργούμε ερωτήσεις με AI
        List<QuizQuestion> questions = generateQuestionsWithAI(movies, numberOfQuestions);

        // Αποθηκεύουμε το session
        QuizSession session = new QuizSession();
        session.setQuestions(questions);
        session.setStartTime(System.currentTimeMillis());
        activeSessions.put(sessionId, session);

        return sessionId;
    }

    /**
     * Παίρνει τις ερωτήσεις για ένα session (χωρίς τις σωστές απαντήσεις)
     */
    public List<QuizQuestion> getQuizQuestions(String sessionId) {
        QuizSession session = activeSessions.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Invalid session ID");
        }

        // Επιστρέφουμε τις ερωτήσεις χωρίς το correctAnswerIndex
        List<QuizQuestion> questions = new ArrayList<>();
        for (QuizQuestion q : session.getQuestions()) {
            QuizQuestion sanitized = new QuizQuestion();
            sanitized.setQuestion(q.getQuestion());
            sanitized.setOptions(q.getOptions());
            sanitized.setCategory(q.getCategory());
            sanitized.setMovieTitle(q.getMovieTitle());
            sanitized.setCorrectAnswerIndex(-1); // Κρύβουμε τη σωστή απάντηση
            questions.add(sanitized);
        }
        return questions;
    }

    /**
     * Υποβάλλει απάντηση και επιστρέφει αν είναι σωστή
     */
    public boolean submitAnswer(String sessionId, int questionIndex, int selectedAnswer) {
        QuizSession session = activeSessions.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Invalid session ID");
        }

        QuizQuestion question = session.getQuestions().get(questionIndex);
        boolean isCorrect = question.getCorrectAnswerIndex() == selectedAnswer;

        if (isCorrect) {
            session.setCorrectAnswers(session.getCorrectAnswers() + 1);
        }

        return isCorrect;
    }

    /**
     * Ολοκληρώνει το quiz και αποθηκεύει το σκορ
     */
    public QuizScore completeQuiz(String sessionId, User user) {
        QuizSession session = activeSessions.get(sessionId);
        if (session == null) {
            throw new IllegalArgumentException("Invalid session ID");
        }

        long timeTaken = (System.currentTimeMillis() - session.getStartTime()) / 1000;
        int score = calculateScore(session.getCorrectAnswers(), session.getQuestions().size());

        // Δημιουργία QuizScore
        QuizScore quizScore = new QuizScore();
        quizScore.setUser(user);
        quizScore.setScoreAchieved(score);
        quizScore.setTimeTakenSeconds(timeTaken);

        // Αποθήκευση στη βάση
        quizScoreRepository.save(quizScore);

        // Ενημέρωση total score χρήστη
        user.setTotalQuizScore(user.getTotalQuizScore() + score);

        // Καθαρισμός session
        activeSessions.remove(sessionId);

        return quizScore;
    }

    /**
     * Παίρνει το leaderboard
     */
    public List<QuizScore> getLeaderboard() {
        return quizScoreRepository.findTop10ByOrderByScoreAchievedDescTimeTakenSecondsAsc();
    }

    /**
     * Παίρνει το ιστορικό ενός χρήστη
     */
    public List<QuizScore> getUserHistory(User user) {
        return quizScoreRepository.findByUserOrderBySessionDateDesc(user);
    }

    /**
     * Παίρνει τον συνολικό αριθμό ερωτήσεων από ένα session
     */
    public int getTotalQuestionsFromSession(String sessionId) {
        QuizSession session = activeSessions.get(sessionId);
        if (session == null) {
            return 0;
        }
        return session.getQuestions().size();
    }

    // ============= PRIVATE HELPER METHODS =============

    /**
     * Δημιουργεί AI-generated ερωτήσεις από ταινίες χρησιμοποιώντας Hugging Face
     */
    private List<QuizQuestion> generateQuestionsWithAI(List<TmdbMovieDto> movies, int count) {
        List<QuizQuestion> questions = new ArrayList<>();
        Random random = new Random();

        String[] questionTypes = {"plot", "characters", "trivia", "production", "awards"};

        for (int i = 0; i < Math.min(count, movies.size()); i++) {
            TmdbMovieDto movie = movies.get(i);
            String questionType = questionTypes[random.nextInt(questionTypes.length)];

            try {
                // Generate question using AI
                String questionText = huggingFaceService.generateTriviaQuestion(movie, questionType);

                if (questionText == null || questionText.isEmpty()) {
                    // Fallback to template-based questions
                    QuizQuestion fallbackQuestion = generateTemplateQuestion(movie, movies, random);
                    if (fallbackQuestion != null) {
                        questions.add(fallbackQuestion);
                    }
                    continue;
                }

                // Determine correct answer based on question type
                String correctAnswer;
                switch (questionType) {
                    case "plot":
                    case "trivia":
                    case "characters":
                        correctAnswer = movie.getTitle();
                        break;
                    case "production":
                        correctAnswer = movie.getReleaseDate() != null ?
                                movie.getReleaseDate().substring(0, 4) : "Unknown";
                        break;
                    case "awards":
                        correctAnswer = String.format("%.1f", movie.getVoteAverage());
                        break;
                    default:
                        correctAnswer = movie.getTitle();
                }

                // Generate wrong options using AI
                List<String> wrongOptions = huggingFaceService.generateOptions(movie, correctAnswer, questionType);

                // Build the final question
                QuizQuestion question = new QuizQuestion();
                question.setQuestion(questionText);
                question.setCategory(questionType);
                question.setMovieTitle(movie.getTitle());

                // Combine correct and wrong answers
                List<String> allOptions = new ArrayList<>();
                allOptions.add(correctAnswer);
                allOptions.addAll(wrongOptions);
                Collections.shuffle(allOptions);

                question.setOptions(allOptions);
                question.setCorrectAnswerIndex(allOptions.indexOf(correctAnswer));

                questions.add(question);

            } catch (Exception e) {
                // Fallback to template questions if AI fails
                QuizQuestion fallbackQuestion = generateTemplateQuestion(movie, movies, random);
                if (fallbackQuestion != null) {
                    questions.add(fallbackQuestion);
                }
            }
        }

        return questions;
    }

    /**
     * Fallback method: Template-based question generation
     */
    private QuizQuestion generateTemplateQuestion(TmdbMovieDto movie, List<TmdbMovieDto> allMovies, Random random) {
        String[] categories = {"title", "year", "rating", "overview"};
        String category = categories[random.nextInt(categories.length)];

        switch (category) {
            case "title":
                return generateTitleQuestion(movie, allMovies, random);
            case "year":
                return generateYearQuestion(movie, allMovies, random);
            case "rating":
                return generateRatingQuestion(movie, allMovies, random);
            case "overview":
                return generateOverviewQuestion(movie, allMovies, random);
            default:
                return null;
        }
    }

    private QuizQuestion generateTitleQuestion(TmdbMovieDto correctMovie, List<TmdbMovieDto> allMovies, Random random) {
        QuizQuestion q = new QuizQuestion();
        q.setQuestion("Ποια είναι αυτή η ταινία; \"" +
                (correctMovie.getOverview() != null && correctMovie.getOverview().length() > 100
                        ? correctMovie.getOverview().substring(0, 100) + "..."
                        : correctMovie.getOverview()) + "\"");
        q.setCategory("title");

        List<String> options = new ArrayList<>();
        options.add(correctMovie.getTitle());

        List<TmdbMovieDto> others = new ArrayList<>(allMovies);
        others.remove(correctMovie);
        Collections.shuffle(others);

        for (int i = 0; i < 3 && i < others.size(); i++) {
            options.add(others.get(i).getTitle());
        }

        Collections.shuffle(options);
        q.setOptions(options);
        q.setCorrectAnswerIndex(options.indexOf(correctMovie.getTitle()));

        return q;
    }

    private QuizQuestion generateYearQuestion(TmdbMovieDto correctMovie, List<TmdbMovieDto> allMovies, Random random) {
        if (correctMovie.getReleaseDate() == null || correctMovie.getReleaseDate().length() < 4) {
            return null;
        }

        QuizQuestion q = new QuizQuestion();
        q.setQuestion("Πότε κυκλοφόρησε η ταινία \"" + correctMovie.getTitle() + "\";");
        q.setCategory("year");
        q.setMovieTitle(correctMovie.getTitle());

        String correctYear = correctMovie.getReleaseDate().substring(0, 4);
        List<String> options = new ArrayList<>();
        options.add(correctYear);

        int year = Integer.parseInt(correctYear);
        options.add(String.valueOf(year - 1));
        options.add(String.valueOf(year + 1));
        options.add(String.valueOf(year - 2));

        Collections.shuffle(options);
        q.setOptions(options);
        q.setCorrectAnswerIndex(options.indexOf(correctYear));

        return q;
    }

    private QuizQuestion generateRatingQuestion(TmdbMovieDto correctMovie, List<TmdbMovieDto> allMovies, Random random) {
        QuizQuestion q = new QuizQuestion();
        q.setQuestion("Ποια είναι η βαθμολογία της \"" + correctMovie.getTitle() + "\" στο TMDB;");
        q.setCategory("rating");
        q.setMovieTitle(correctMovie.getTitle());

        List<String> options = new ArrayList<>();
        double correctRating = correctMovie.getVoteAverage();
        options.add(String.format("%.1f", correctRating));
        options.add(String.format("%.1f", correctRating + 0.5));
        options.add(String.format("%.1f", correctRating - 0.5));
        options.add(String.format("%.1f", correctRating + 1.0));

        Collections.shuffle(options);
        q.setOptions(options);
        q.setCorrectAnswerIndex(options.indexOf(String.format("%.1f", correctRating)));

        return q;
    }

    private QuizQuestion generateOverviewQuestion(TmdbMovieDto correctMovie, List<TmdbMovieDto> allMovies, Random random) {
        if (correctMovie.getOverview() == null || correctMovie.getOverview().isEmpty()) {
            return null;
        }

        QuizQuestion q = new QuizQuestion();
        q.setQuestion("Για ποια ταινία είναι αυτή η περιγραφή; \"" +
                correctMovie.getOverview().substring(0, Math.min(150, correctMovie.getOverview().length())) + "...\"");
        q.setCategory("overview");

        return generateTitleQuestion(correctMovie, allMovies, random);
    }

    private int calculateScore(int correctAnswers, int totalQuestions) {
        return (int) ((correctAnswers / (double) totalQuestions) * 100);
    }

    // Inner class για session management
    private static class QuizSession {
        private List<QuizQuestion> questions;
        private int correctAnswers = 0;
        private long startTime;

        public List<QuizQuestion> getQuestions() {
            return questions;
        }

        public void setQuestions(List<QuizQuestion> questions) {
            this.questions = questions;
        }

        public int getCorrectAnswers() {
            return correctAnswers;
        }

        public void setCorrectAnswers(int correctAnswers) {
            this.correctAnswers = correctAnswers;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }
    }
}