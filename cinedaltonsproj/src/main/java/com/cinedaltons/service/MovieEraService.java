package com.cinedaltons.service;

import com.cinedaltons.dto.EraResult;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;


@Service
public class MovieEraService {
    private static final String MODEL_URL =
            "https://router.huggingface.co/hf-inference/models/google/vit-base-patch16-224";

    @Value("${huggingface.api.key}")
    private String hfKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper om = new ObjectMapper();

    // new
    public EraResult classify(byte[] imageBytes) {
        List<LabelScore> preds = classifyImage(imageBytes);

        // Debug: see what ViT is actually returning (helps a LOT)
        System.out.println("Top ViT labels:");
        for (int i = 0; i < Math.min(5, preds.size()); i++) {
            System.out.println(" - " + preds.get(i).label() + " (" + preds.get(i).score() + ")");
        }

        // Points per era
        Map<String, Integer> eraPoints = new LinkedHashMap<>();
        eraPoints.put("Golden Age Hollywood (1930s–1950s)", 0);
        eraPoints.put("Swinging 60s Spy/Mod (1960s)", 0);
        eraPoints.put("Gritty New Hollywood (1970s)", 0);
        eraPoints.put("80s Action Blockbuster (1980s)", 0);
        eraPoints.put("90s Rom-Com Lead (1990s)", 0);
        eraPoints.put("2000s Teen/Pop Comedy (2000s)", 0);
        eraPoints.put("2010s Superhero Era (2010s)", 0);
        eraPoints.put("2020s Streaming Thriller (2020s)", 0);
        eraPoints.put("Film Noir Classic (1940s–1950s)", 0);
        eraPoints.put("Late 70s Disco Funk (1977–1979)", 0);
        eraPoints.put("80s Neon Cyberpunk (1980s)", 0);
        eraPoints.put("90s Indie / Grunge Cinema (1990s)", 0);
        eraPoints.put("2000s Y2K Pop Culture (1998–2004)", 0);
        eraPoints.put("2000s Dark Fantasy Epic (2001–2008)", 0);
        eraPoints.put("2010s Prestige Drama (2010s)", 0);
        eraPoints.put("2010s Instagram Indie (2015–2019)", 0);
        eraPoints.put("Retro Revival / Nostalgia Core", 0);


        for (int i = 0; i < Math.min(8, preds.size()); i++) {
            String label = preds.get(i).label().toLowerCase();

            if (label.contains("suit") || label.contains("tie")) add(eraPoints, "Swinging 60s Spy/Mod (1960s)", 2);
            if (label.contains("sunglasses") || label.contains("leather") || label.contains("motorcycle")) add(eraPoints, "80s Action Blockbuster (1980s)", 2);
            if (label.contains("gown") || label.contains("dress") || label.contains("spotlight")) add(eraPoints, "Golden Age Hollywood (1930s–1950s)", 2);
            if (label.contains("street") || label.contains("jean") || label.contains("jacket")) add(eraPoints, "Gritty New Hollywood (1970s)", 1);
            if (label.contains("coffee") || label.contains("book") || label.contains("restaurant")) add(eraPoints, "90s Rom-Com Lead (1990s)", 1);
            if (label.contains("laptop") || label.contains("cellular") || label.contains("hand-held")) add(eraPoints, "2000s Teen/Pop Comedy (2000s)", 1);
            if (label.contains("armor") || label.contains("mask") || label.contains("cape")) add(eraPoints, "2010s Superhero Era (2010s)", 2);
            if (label.contains("hood") || label.contains("knife")) add(eraPoints, "2020s Streaming Thriller (2020s)", 2);
            if (label.contains("shadow") || label.contains("monochrome")) add(eraPoints, "Film Noir Classic (1940s–1950s)", 2);
            if (label.contains("disco") || label.contains("stage") || label.contains("spotlight")) add(eraPoints, "Late 70s Disco Funk (1977–1979)", 2);
            if (label.contains("neon") || label.contains("city") || label.contains("night")) add(eraPoints, "80s Neon Cyberpunk (1980s)", 2);
            if (label.contains("flannel") || label.contains("casual")) add(eraPoints, "90s Indie / Grunge Cinema (1990s)", 2);
            if (label.contains("gloss") || label.contains("fashion")) add(eraPoints, "2000s Y2K Pop Culture (1998–2004)", 2);
            if (label.contains("armor") || label.contains("sword")) add(eraPoints, "2000s Dark Fantasy Epic (2001–2008)", 2);
            if (label.contains("studio") || label.contains("portrait")) add(eraPoints, "2010s Prestige Drama (2010s)", 1);
            if (label.contains("soft") || label.contains("minimal")) add(eraPoints, "2010s Instagram Indie (2015–2019)", 1);
            if (label.contains("retro") || label.contains("vintage")) add(eraPoints, "Retro Revival / Nostalgia Core", 2);
        }

        int sum = eraPoints.values().stream().mapToInt(Integer::intValue).sum();

        //  Fallback: when no era matched any rule
        if (sum == 0) {
            double sat = analyzeSaturation(imageBytes);

            String bestEra;
            if (sat < 0.12) {
                bestEra = "Golden Age Hollywood (1930s–1950s)";
            } else if (sat > 0.45) {
                bestEra = "80s Action Blockbuster (1980s)";
            } else {
                bestEra = "90s Rom-Com Lead (1990s)";
            }

            List<String> allEras = new ArrayList<>(eraPoints.keySet());
            allEras.remove(bestEra);          // avoid duplicate bestEra
            Collections.shuffle(allEras);

            // pick as many as we can (up to 4 more)
            List<EraResult.EraScore> top5 = new ArrayList<>();
            top5.add(new EraResult.EraScore(bestEra, 30));

            int[] scores = {18, 18, 17, 17};  // totals to 100 with 30
            for (int i = 0; i < Math.min(4, allEras.size()); i++) {
                top5.add(new EraResult.EraScore(allEras.get(i), scores[i]));
            }

            return new EraResult(bestEra, 30, top5);

            // Buckets based on color vibe
            /*List<String> mutedEras = List.of(
                    "Golden Age Hollywood (1930s–1950s)",
                    "Film Noir Classic (1940s–1950s)",
                    "2010s Prestige Drama (2010s)"
            );

            List<String> colorfulEras = List.of(
                    "80s Action Blockbuster (1980s)",
                    "80s Neon Cyberpunk (1980s)",
                    "2000s Y2K Pop Culture (1998–2004)",
                    "Late 70s Disco Funk (1977–1979)"
            );

            List<String> neutralEras = List.of(
                    "90s Rom-Com Lead (1990s)",
                    "90s Indie / Grunge Cinema (1990s)",
                    "2010s Instagram Indie (2015–2019)"
            );

            List<String> pool;
            if (sat < 0.12) pool = mutedEras;
            else if (sat > 0.45) pool = colorfulEras;
            else pool = neutralEras;

            String bestEra = pool.get(new Random().nextInt(pool.size()));

            // Pick 4 other distinct eras for variety
            List<String> others = new ArrayList<>(neutralEras);
            Collections.shuffle(others);

            List<EraResult.EraScore> top5 = List.of(
                    new EraResult.EraScore(bestEra, 30),
                    new EraResult.EraScore(others.get(0), 18),
                    new EraResult.EraScore(others.get(1), 18),
                    new EraResult.EraScore(others.get(2), 17),
                    new EraResult.EraScore(others.get(3), 17)
            );

            return new EraResult(bestEra, 30, top5);*/
        }


        // Normal scoring path
        List<Map.Entry<String, Integer>> ranked = eraPoints.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .toList();

        // ✅ NEW: If there’s a tie, pick randomly among the best to avoid always-first bias
        int max = ranked.get(0).getValue();
        List<String> tiedBest = ranked.stream()
                .filter(e -> e.getValue() == max)
                .map(Map.Entry::getKey)
                .toList();
        String bestEra = tiedBest.get(new Random().nextInt(tiedBest.size()));

        int best = max;
        int confidence = (int) Math.round((best * 100.0) / sum);

        List<EraResult.EraScore> top5 = ranked.stream().limit(5)
                .map(e -> new EraResult.EraScore(
                        e.getKey(),
                        (int) Math.round(e.getValue() * 100.0 / sum)
                ))
                .toList();

        return new EraResult(bestEra, confidence, top5);
    }

    private static void add(Map<String, Integer> m, String k, int v) {
        m.put(k, m.getOrDefault(k, 0) + v);
    }
    // Helper method for classify()
    private double analyzeSaturation(byte[] bytes) {
        try {
            var img = javax.imageio.ImageIO.read(new java.io.ByteArrayInputStream(bytes));
            if (img == null) return 0.3;

            long count = 0;
            double satSum = 0.0;

            int step = Math.max(1, Math.min(img.getWidth(), img.getHeight()) / 200); // sample pixels
            for (int y = 0; y < img.getHeight(); y += step) {
                for (int x = 0; x < img.getWidth(); x += step) {
                    int rgb = img.getRGB(x, y);
                    int r = (rgb >> 16) & 255, g = (rgb >> 8) & 255, b = rgb & 255;
                    float[] hsb = java.awt.Color.RGBtoHSB(r, g, b, null);
                    satSum += hsb[1];
                    count++;
                }
            }
            return satSum / Math.max(1, count);
        } catch (Exception e) {
            return 0.3;
        }
    }

    private List<LabelScore> classifyImage(byte[] imageBytes) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(hfKey.trim());
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        HttpEntity<byte[]> req = new HttpEntity<>(imageBytes, headers);

        try {
            ResponseEntity<String> resp = restTemplate.exchange(MODEL_URL, HttpMethod.POST, req, String.class);

            JsonNode arr = om.readTree(resp.getBody());
            List<LabelScore> out = new ArrayList<>();
            for (JsonNode n : arr) {
                out.add(new LabelScore(n.get("label").asText(), n.get("score").asDouble()));
            }
            return out;

        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            throw new RuntimeException("HF error " + e.getStatusCode() + ": " + e.getResponseBodyAsString(), e);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse HF response", e);
        }
    }

    private record LabelScore(String label, double score) {}

    private static void normalizeInPlace(float[] v) {
        double sum = 0;
        for (float x : v) sum += x * x;
        double norm = Math.sqrt(sum) + 1e-12;
        for (int i = 0; i < v.length; i++) v[i] /= norm;
    }
}
