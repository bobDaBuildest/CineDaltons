
package com.cinedaltons.service;

import com.cinedaltons.dto.TmdbMovieDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class HuggingFaceService {

    private static final Logger logger = LoggerFactory.getLogger(HuggingFaceService.class);

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    @Value("${huggingface.api.key}")
    private String apiKey;

    @Value("${huggingface.api.url}")
    private String apiUrl;

    public HuggingFaceService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    /**
     * Generates a trivia question about a movie using Hugging Face LLM
     */
    public String generateTriviaQuestion(TmdbMovieDto movie, String questionType) {
        String prompt = buildPrompt(movie, questionType);

        try {
            String response = callHuggingFaceAPI(prompt);
            return extractQuestionFromResponse(response);
        } catch (Exception e) {
            logger.error("Error generating question with Hugging Face: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Generates multiple choice options using AI
     */
    public List<String> generateOptions(TmdbMovieDto movie, String correctAnswer, String questionType) {
        String prompt = buildOptionsPrompt(movie, correctAnswer, questionType);

        try {
            String response = callHuggingFaceAPI(prompt);
            return extractOptionsFromResponse(response, correctAnswer);
        } catch (Exception e) {
            logger.error("Error generating options with Hugging Face: {}", e.getMessage());
            return generateFallbackOptions(correctAnswer);
        }
    }

    /**
     * Builds the prompt for question generation
     */
    private String buildPrompt(TmdbMovieDto movie, String questionType) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("[INST] You are a movie trivia expert. Generate ONE creative trivia question about the following movie.\n\n");
        prompt.append("Movie: ").append(movie.getTitle()).append("\n");

        if (movie.getReleaseDate() != null) {
            prompt.append("Release Year: ").append(movie.getReleaseDate().substring(0, 4)).append("\n");
        }

        if (movie.getOverview() != null) {
            prompt.append("Overview: ").append(movie.getOverview()).append("\n");
        }

        prompt.append("\nQuestion Type: ").append(questionType).append("\n");
        prompt.append("\nGenerate a single, clear trivia question. Do not include the answer. Keep it concise (max 100 characters). [/INST]\n");

        return prompt.toString();
    }

    /**
     * Builds the prompt for generating wrong answer options
     */
    private String buildOptionsPrompt(TmdbMovieDto movie, String correctAnswer, String questionType) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("[INST] Generate 3 plausible but WRONG answers for this movie trivia question.\n\n");
        prompt.append("Movie: ").append(movie.getTitle()).append("\n");
        prompt.append("Correct Answer: ").append(correctAnswer).append("\n");
        prompt.append("Question Type: ").append(questionType).append("\n");
        prompt.append("\nProvide exactly 3 wrong answers, one per line. Make them believable but incorrect. [/INST]\n");

        return prompt.toString();
    }

    /**
     * Calls Hugging Face Inference API
     */
    private String callHuggingFaceAPI(String prompt) {
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("inputs", prompt);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("max_new_tokens", 150);
        parameters.put("temperature", 0.7);
        parameters.put("top_p", 0.9);
        parameters.put("do_sample", true);
        requestBody.put("parameters", parameters);

        try {
            String response = webClient.post()
                    .uri(apiUrl)
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response == null) {
                throw new RuntimeException("Empty response from Hugging Face");
            }

            // Parse JSON response
            JsonNode jsonNode = objectMapper.readTree(response);

            // Handle different response formats
            if (jsonNode.isArray() && jsonNode.size() > 0) {
                return jsonNode.get(0).get("generated_text").asText();
            } else if (jsonNode.has("generated_text")) {
                return jsonNode.get("generated_text").asText();
            }

            return response;

        } catch (Exception e) {
            logger.error("Error calling Hugging Face API: {}", e.getMessage());
            throw new RuntimeException("Failed to generate content", e);
        }
    }

    /**
     * Extracts the question from the AI response
     */
    private String extractQuestionFromResponse(String response) {
        // Remove the prompt if it's echoed back
        String[] parts = response.split("\\[/INST\\]");
        String content = parts.length > 1 ? parts[1].trim() : response.trim();

        // Clean up the response
        content = content.replaceAll("\\n+", " ").trim();

        // Take the first question if multiple are present
        if (content.contains("?")) {
            int questionMarkIndex = content.indexOf("?");
            content = content.substring(0, questionMarkIndex + 1).trim();
        }

        // Limit length
        if (content.length() > 200) {
            content = content.substring(0, 197) + "...";
        }

        return content;
    }

    /**
     * Extracts options from the AI response
     */
    private List<String> extractOptionsFromResponse(String response, String correctAnswer) {
        List<String> options = new ArrayList<>();

        // Remove the prompt if echoed back
        String[] parts = response.split("\\[/INST\\]");
        String content = parts.length > 1 ? parts[1].trim() : response.trim();

        // Split by newlines and clean up
        String[] lines = content.split("\\n");
        for (String line : lines) {
            line = line.trim()
                    .replaceAll("^[0-9]+\\.", "")  // Remove numbers
                    .replaceAll("^-", "")           // Remove dashes
                    .replaceAll("^\\*", "")         // Remove asterisks
                    .trim();

            if (!line.isEmpty() && !line.equals(correctAnswer) && options.size() < 3) {
                options.add(line);
            }
        }

        // If we didn't get enough options, generate fallback
        while (options.size() < 3) {
            options.addAll(generateFallbackOptions(correctAnswer));
            options = new ArrayList<>(new HashSet<>(options)); // Remove duplicates
            if (options.size() > 3) {
                options = options.subList(0, 3);
            }
        }

        return options.subList(0, 3);
    }

    /**
     * Generates fallback options if AI fails
     */
    private List<String> generateFallbackOptions(String correctAnswer) {
        List<String> fallbacks = new ArrayList<>();
        fallbacks.add("The Godfather");
        fallbacks.add("Pulp Fiction");
        fallbacks.add("The Shawshank Redemption");
        fallbacks.add("Inception");
        fallbacks.add("The Dark Knight");
        fallbacks.add("Forrest Gump");

        // Remove the correct answer if it's in fallbacks
        fallbacks.remove(correctAnswer);

        Collections.shuffle(fallbacks);
        return fallbacks.subList(0, Math.min(3, fallbacks.size()));
    }
}