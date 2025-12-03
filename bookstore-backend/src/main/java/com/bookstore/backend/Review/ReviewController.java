package com.bookstore.backend.Review;

import com.bookstore.backend.Book.Book;
import com.bookstore.backend.Book.BookRepository;
import com.bookstore.backend.User.User;
import com.bookstore.backend.User.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class ReviewController {

    private final ReviewRepository reviews;
    private final BookRepository books;
    private final UserRepository users;

    public ReviewController(ReviewRepository reviews,
                            BookRepository books,
                            UserRepository users) {
        this.reviews = reviews;
        this.books = books;
        this.users = users;
    }

    // GET /api/books/{bookId}/reviews
    @GetMapping("/{bookId}/reviews")
    public ResponseEntity<?> getReviews(@PathVariable int bookId) {
        if (!books.existsById(bookId)) {
            return ResponseEntity.badRequest().body("Book not found");
        }

        List<ReviewResponse> response = reviews
                .findByBookIdOrderByIdDesc(bookId)
                .stream()
                .map(r -> new ReviewResponse(
                        r.getId(),
                        // show firstName if available, otherwise email
                        (r.getUser().getFirstName() != null && !r.getUser().getFirstName().isBlank())
                                ? r.getUser().getFirstName()
                                : r.getUser().getEmail(),
                        r.getRating(),
                        r.getBody()
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    // POST /api/books/{bookId}/reviews
    @PostMapping("/{bookId}/reviews")
    public ResponseEntity<?> createReview(@PathVariable int bookId,
                                          @RequestBody ReviewRequest req) {

        if (req.getRating() < 1 || req.getRating() > 5
                || req.getComment() == null || req.getComment().isBlank()) {
            return ResponseEntity.badRequest().body("Rating 1-5 and comment are required");
        }

        Book book = books.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        User user = users.findById(req.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Review review = new Review();
        review.setBook(book);
        review.setUser(user);
        review.setRating(req.getRating());
        review.setBody(req.getComment().trim());

        reviews.save(review);

        // return updated list like FinalTake style
        return getReviews(bookId);
    }
}
