package com.bookstore.backend.Cart;

import com.bookstore.backend.Book.Book;
import com.bookstore.backend.Book.BookRepository;
import com.bookstore.backend.User.User;
import com.bookstore.backend.User.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class CartController {

    private final CartRepository carts;
    private final CartItemRepository cartItems;
    private final UserRepository users;
    private final BookRepository books;

    public CartController(CartRepository carts,
                          CartItemRepository cartItems,
                          UserRepository users,
                          BookRepository books) {
        this.carts = carts;
        this.cartItems = cartItems;
        this.users = users;
        this.books = books;
    }

    private Cart getOrCreateCartForUser(int userId) {
        return carts.findByUser_Id(userId).orElseGet(() -> {
            User user = users.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            Cart newCart = new Cart();
            newCart.setUser(user);
            return carts.save(newCart);
        });
    }

    private List<CartItemResponse> toResponse(Cart cart) {
        return cart.getItems().stream()
                .map(item -> new CartItemResponse(
                        item.getBook().getId(),
                        item.getBook().getTitle(),
                        item.getBook().getAuthor(),
                        item.getBook().getPrice().doubleValue(),
                        item.getQuantity(),
                        item.getBook().getIsbn()
                ))
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getCart(@PathVariable int userId) {
        if (!users.existsById(userId)) {
            return ResponseEntity.badRequest().body("User not found");
        }

        Cart cart = getOrCreateCartForUser(userId);
        return ResponseEntity.ok(toResponse(cart));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addToCart(@RequestBody CartItemRequest req) {

        Cart cart = getOrCreateCartForUser(req.getUserId());

        Book book = books.findById(req.getBookId())
                .orElseThrow(() -> new IllegalArgumentException("Book not found"));

        int qty = req.getQuantity() <= 0 ? 1 : req.getQuantity();

        CartItem item = cartItems
                .findByCartIdAndBookId(cart.getId(), req.getBookId())
                .orElseGet(() -> {
                    CartItem ci = new CartItem();
                    ci.setCart(cart);
                    ci.setBook(book);
                    ci.setQuantity(0);
                    return ci;
                });

        item.setQuantity(item.getQuantity() + qty);
        cartItems.save(item);

        Cart updated = carts.findById(cart.getId()).orElse(cart);
        return ResponseEntity.ok(toResponse(updated));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateQuantity(@RequestBody CartItemRequest req) {

        Cart cart = getOrCreateCartForUser(req.getUserId());

        Optional<CartItem> opt = cartItems
                .findByCartIdAndBookId(cart.getId(), req.getBookId());

        if (opt.isEmpty()) {
            return ResponseEntity.badRequest().body("Cart item not found");
        }

        if (req.getQuantity() <= 0) {
            cartItems.delete(opt.get());
        } else {
            CartItem item = opt.get();
            item.setQuantity(req.getQuantity());
            cartItems.save(item);
        }

        Cart updated = carts.findById(cart.getId()).orElse(cart);
        return ResponseEntity.ok(toResponse(updated));
    }

    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<?> clearCart(@PathVariable int userId) {
        Cart cart = getOrCreateCartForUser(userId);
        cartItems.deleteByCartId(cart.getId());
        return ResponseEntity.ok().build();
    }
}
