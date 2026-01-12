package com.cinedaltons.controller;

import com.cinedaltons.dto.EraResult;
import com.cinedaltons.service.MovieEraService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class GameController {

    private final MovieEraService movieEraService;

    public GameController(MovieEraService movieEraService) {
        this.movieEraService = movieEraService;
    }

    @PostMapping(value = "/movie-era", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EraResult> movieEra(@RequestParam("photo") MultipartFile file) throws Exception {
        EraResult result = movieEraService.classify(file.getBytes());
        return ResponseEntity.ok(result);
    }

    /*@Value("${huggingface.api.key}")
    private String huggingFaceApiKey;

    @PostMapping("/movie-era")
    public ResponseEntity<?> checkLookalike(@RequestParam("photo") MultipartFile file) {
        try {
            // 1. URL for the Hugging Face model
            String apiUrl = "https://router.huggingface.co/hf-inference/models/microsoft/resnet-50";

            System.out.println("--- CHECKING URL: " + apiUrl + " ---");

            // 2. Set up headers (Authorization)
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(huggingFaceApiKey.trim());
            headers.set("Content-Type", "application/octet-stream");
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

            // 3. Send the image bytes
            HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.exchange(
                    apiUrl,
                    HttpMethod.POST,
                    requestEntity,
                    String.class
            );

            System.out.println("--- 2. SUCCESS! API RESPONDED ---");
            // 4. Return the result to your frontend
            return ResponseEntity.ok(response.getBody());

        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            // return the real error back to the browser (super helpful for debugging)
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("{\"error\":\"Internal Error\"}");
        }
    }*/
}
