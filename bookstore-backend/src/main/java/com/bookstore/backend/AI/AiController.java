package com.bookstore.backend.AI;

import com.bookstore.backend.Book.Book;
import com.bookstore.backend.Book.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AiController {

    private static final int CANDIDATE_LIMIT = 30;   // how many books you show the model
    private static final int FALLBACK_LIMIT  = 3;    // if model fails show first 3
    private static final String OLLAMA_MODEL  = "llama3.1";
    private static final String OLLAMA_URL    = "http://localhost:11434/api/generate";

    private final BookRepository bookRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public AiController(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @PostMapping("/recommend")
    public ResponseEntity<?> recommend(@RequestBody AiRequest request) {

        String query = request.getQuery();
        if (query == null || query.isBlank()) {
            return ResponseEntity.badRequest().body("Query required");
        }

        List<Book> allBooks = bookRepository.findAll();
        if (allBooks.isEmpty()) {
            return ResponseEntity.ok(new AiResponse("No books found in catalog.", List.of()));
        }

        // 1) Create candidate set (NOT final picks)
        List<Book> candidates = allBooks.stream()
                .map(b -> Map.entry(b, score(b, query)))
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(Math.min(CANDIDATE_LIMIT, allBooks.size()))
                .map(Map.Entry::getKey)
                .toList();

        if (candidates.isEmpty()) {
            candidates = allBooks.stream().limit(Math.min(CANDIDATE_LIMIT, allBooks.size())).toList();
        }

        AiPickResponse pick = askOllamaToPick(query, candidates);


        Map<Integer, Book> byId = candidates.stream()
                .collect(Collectors.toMap(Book::getId, b -> b));

        List<AiPickResponse.Pick> safePicks = (pick.selected == null ? List.<AiPickResponse.Pick>of() : pick.selected)
                .stream()
                .filter(p -> p != null && p.id != null && byId.containsKey(p.id))
                .distinct()
                .limit(FALLBACK_LIMIT)
                .toList();


        List<Book> finalBooks;
        String explanation;

        if (safePicks.size() < FALLBACK_LIMIT) {
            finalBooks = candidates.stream().limit(Math.min(FALLBACK_LIMIT, candidates.size())).toList();
            explanation = askOllamaExplain(query, finalBooks); // your old behavior for explanation
        } else {
            finalBooks = safePicks.stream().map(p -> byId.get(p.id)).toList();
            explanation = buildNumberedExplanation(finalBooks, safePicks);
        }

        return ResponseEntity.ok(new AiResponse(explanation, finalBooks));
    }


    private int score(Book book, String query) {
        int score = 0;
        String q = query.toLowerCase();

        for (String word : q.split("\\s+")) {
            if (word.length() < 2) continue;

            if (contains(book.getTitle(), word)) score += 20;
            if (contains(book.getDescription(), word)) score += 15;
            if (contains(book.getAuthor(), word)) score += 5;
        }

        return score;
    }

    private boolean contains(String text, String word) {
        return text != null && text.toLowerCase().contains(word);
    }

    private AiPickResponse askOllamaToPick(String query, List<Book> candidates) {

        String candidateList = candidates.stream()
                .map(b -> "ID: " + b.getId()
                        + " | Title: " + b.getTitle()
                        + " | Author: " + b.getAuthor()
                        + " | Desc: " + safeTrim(b.getDescription(), 220))
                .collect(Collectors.joining("\n"));

        String prompt = """
User request:
"%s"

Candidates (you MUST choose ONLY from this list):
%s

Return VALID JSON ONLY in this exact format (no extra text, no markdown):
{
  "selected": [
    {"id": 1, "reason": "why it matches"},
    {"id": 2, "reason": "why it matches"},
    {"id": 3, "reason": "why it matches"}
  ]
}

Rules:
- Choose EXACTLY 3
- Each id must be one of the candidate IDs shown above
- Do not invent books
""".formatted(query, candidateList);

        Map<String, Object> body = new HashMap<>();
        body.put("model", OLLAMA_MODEL);
        body.put("prompt", prompt);
        body.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ResponseEntity<Map> res = restTemplate.postForEntity(
                    OLLAMA_URL,
                    new HttpEntity<>(body, headers),
                    Map.class
            );

            String raw = (res.getBody() == null) ? "" : res.getBody().getOrDefault("response", "").toString().trim();
            return objectMapper.readValue(raw, AiPickResponse.class);

        } catch (Exception e) {
            AiPickResponse empty = new AiPickResponse();
            empty.selected = List.of();
            return empty;
        }
    }

    private String buildNumberedExplanation(List<Book> books, List<AiPickResponse.Pick> picks) {
        Map<Integer, String> reasonById = picks.stream()
                .filter(p -> p.id != null)
                .collect(Collectors.toMap(p -> p.id, p -> p.reason == null ? "" : p.reason, (a, b) -> a));

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < books.size(); i++) {
            Book b = books.get(i);
            String reason = reasonById.getOrDefault(b.getId(), "");
            sb.append(i + 1).append(". ")
                    .append(b.getTitle())
                    .append(" — ")
                    .append(reason.isBlank() ? "Matches your request based on the book’s focus." : reason.trim())
                    .append("\n");
        }
        return sb.toString().trim();
    }

    // --- Your old explainer behavior, slightly renamed ---
    private String askOllamaExplain(String query, List<Book> books) {

        String bookList = books.stream()
                .map(b -> "- " + b.getTitle() + " by " + b.getAuthor())
                .collect(Collectors.joining("\n"));

        String prompt = """
User request:
"%s"

The following books are ALREADY selected from our catalog:
%s

STRICT RULES:
- You MUST ONLY talk about the books listed above
- You MUST NOT add or replace books
- You MUST NOT suggest alternatives
- You MUST NOT mention books outside the list
- Write EXACTLY 3 numbered points (1, 2, 3)
- Each point explains WHY that specific book matches the request
""".formatted(query, bookList);

        Map<String, Object> body = new HashMap<>();
        body.put("model", OLLAMA_MODEL);
        body.put("prompt", prompt);
        body.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ResponseEntity<Map> res = restTemplate.postForEntity(
                    OLLAMA_URL,
                    new HttpEntity<>(body, headers),
                    Map.class
            );

            return res.getBody() == null ? "" : res.getBody().getOrDefault("response", "").toString();

        } catch (Exception e) {
            return "Here are three books from our catalog that match your request.";
        }
    }

    private String safeTrim(String s, int max) {
        if (s == null) return "";
        s = s.replace("\n", " ").replace("\r", " ").trim();
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
