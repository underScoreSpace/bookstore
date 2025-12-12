package com.bookstore.backend.AI;

import com.bookstore.backend.Book.Book;
import com.bookstore.backend.Book.BookRepository;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ai")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class AiController {

    private static final int MIN_SCORE = 10;

    private final BookRepository bookRepository;
    private final RestTemplate restTemplate = new RestTemplate();

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

        //take top 3 based off score
        List<Book> topBooks = allBooks.stream()
                .map(b -> Map.entry(b, score(b, query)))
                .filter(e -> e.getValue() >= MIN_SCORE)
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();

        if (topBooks.isEmpty()) {
            topBooks = allBooks.stream().limit(3).toList();
        }

        String explanation = askOllama(query, topBooks);

        return ResponseEntity.ok(new AiResponse(explanation, topBooks));
    }

    // might need to be reworked sometimes it doesn't give relevant info... or maybe we need more books
    private int score(Book book, String query) {
        int score = 0;
        String q = query.toLowerCase();

        for (String word : q.split("\\s+")) {
            if (word.length() < 4) continue;

            if (contains(book.getTitle(), word)) {
                score += 20;
            }

            if (contains(book.getDescription(), word)) {
                score += 15;
            }

            if (contains(book.getAuthor(), word)) {
                score += 5;
            }
        }

        return score;
    }

    private boolean contains(String text, String word) {
        return text != null && text.toLowerCase().contains(word);
    }


    private String askOllama(String query, List<Book> books) {

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
- You MUST NOT say "alternatively", "another option", or "consider"
- Write EXACTLY 3 numbered points (1, 2, 3)
- Each point explains WHY that specific book matches the request
- Do NOT repeat titles
""".formatted(query, bookList);

        Map<String, Object> body = new HashMap<>();
        body.put("model", "llama3.1");
        body.put("prompt", prompt);
        body.put("stream", false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ResponseEntity<Map> res = restTemplate.postForEntity(
                    "http://localhost:11434/api/generate",
                    new HttpEntity<>(body, headers),
                    Map.class
            );

            return res.getBody().getOrDefault("response", "").toString();

        } catch (Exception e) {
            return "Here are three books from our catalog that match your request.";
        }
    }
}
