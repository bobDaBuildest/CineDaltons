import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.util.stream.Collectors;

public class MovieTasteSurvey {

    private static final List<String> AVAILABLE_GENRES = Arrays.asList(
            "Action", "Comedy", "Drama", "Sci-Fi", "Horror", "Romance",
            "Thriller", "Documentary", "Animation", "Fantasy", "Mystery", "Western"
    );

    private static final String APP_ID = "cinema-taste-survey";

    public static void main(String[] args) {

        String currentUserId = UUID.randomUUID().toString();

        System.out.println("Welcome to Movie Taste Survey");
        System.out.println("Here you can select all of your favorite movie genres");
        System.out.println("Please enter your user ID:");
        System.out.println("User ID: " + currentUserId);

        List<String> selectedGenres = askForTastes();

        if (!selectedGenres.isEmpty()) {
            System.out.println("\n Final Selected Genres:");
            String tastesString = selectedGenres.stream()
                    .map(g -> "-> " + g)
                    .collect(Collectors.joining("\n"));
            System.out.println(tastesString);

            savePreferencesToUserBase(currentUserId, selectedGenres);
        } else {
            System.out.println("\n You did not select any genre. Save was canceled.");
        }
    }

    private static List<String> askForTastes() {
        Scanner scanner = new Scanner(System.in);
        List<String> userChoices = new ArrayList<>();

        System.out.println("\n Movie Genres! Select all your favorites");
        boolean running = true;

        while (running) {
            System.out.println("\n Available Genres:");
            for (int i = 0; i < AVAILABLE_GENRES.size(); i++) {
                System.out.printf("[%d] %s\n", i + 1, AVAILABLE_GENRES.get(i));
            }

            System.out.printf("Type the numbers of the genres (1-%d) separated by spaces or commas:\n", AVAILABLE_GENRES.size());
            System.out.println("Press '0' or just hit ENTER to finish!");
            System.out.print("Your choice: ");

            String input = scanner.nextLine().trim();

            if (input.equals("0") || input.isEmpty()) {
                running = false;
                continue;
            }

            String[] parts = input.split("[,\\s]+");

            for (String part : parts) {
                try {
                    int index = Integer.parseInt(part) - 1;

                    if (index >= 0 && index < AVAILABLE_GENRES.size()) {
                        String genre = AVAILABLE_GENRES.get(index);

                        if (!userChoices.contains(genre)) {
                            userChoices.add(genre);
                            System.out.println(">> Added: " + genre);
                        } else {
                            System.out.println(">> Already selected: " + genre);
                        }
                    } else {
                        System.err.println("Warning! The number '" + part + "' is not a valid genre.");
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Warning! The value '" + part + "' is not a valid number.");
                }
            }

            System.out.println("Current selections: " + userChoices);
        }

        return userChoices;
    }

    private static void savePreferencesToUserBase(String userId, List<String> genres) {
        System.out.println("\n Saved Successfully!");
        System.out.println("For user: " + userId);
    }
}