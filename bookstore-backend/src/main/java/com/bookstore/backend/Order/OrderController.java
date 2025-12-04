package com.bookstore.backend.Order;

import com.bookstore.backend.Book.Book;
import com.bookstore.backend.Book.BookRepository;
import com.bookstore.backend.Cart.Cart;
import com.bookstore.backend.Cart.CartItem;
import com.bookstore.backend.Cart.CartItemRepository;
import com.bookstore.backend.Cart.CartRepository;
import com.bookstore.backend.User.User;
import com.bookstore.backend.User.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:5173"})
public class OrderController {

    private final OrderRepository orders;
    private final OrderItemRepository orderItems;
    private final CartRepository carts;
    private final CartItemRepository cartItems;
    private final UserRepository users;
    private final BookRepository books;

    public OrderController(
            OrderRepository orders,
            OrderItemRepository orderItems,
            CartRepository carts,
            CartItemRepository cartItems,
            UserRepository users,
            BookRepository books
    ) {
        this.orders = orders;
        this.orderItems = orderItems;
        this.carts = carts;
        this.cartItems = cartItems;
        this.users = users;
        this.books = books;
    }



    @PostMapping("/checkout")
    @Transactional
    public ResponseEntity<?> checkout(@RequestBody CheckoutRequest req) {

        // Validate user
        Optional<User> userOpt = users.findById(req.getUserId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        // Get user's cart
        Optional<Cart> cartOpt = carts.findByUserId(req.getUserId());
        if (cartOpt.isEmpty() || cartOpt.get().getItems().isEmpty()) {
            return ResponseEntity.badRequest().body("Cart is empty");
        }

        User user = userOpt.get();
        Cart cart = cartOpt.get();

        for (CartItem ci : cart.getItems()) {
            Book book = ci.getBook();
            int requested = ci.getQuantity();
            int available = book.getStockQty();

            if (available < requested) {
                return ResponseEntity.status(409)
                        .body("Not enough stock for \"" + book.getTitle()
                                + "\". In stock: " + available
                                + ", requested: " + requested);
            }
        }

        BigDecimal subtotal = cart.getItems().stream()
                .map(item -> item.getBook().getPrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal tax = subtotal.multiply(new BigDecimal("0.08"));
        BigDecimal shippingFee = subtotal.compareTo(new BigDecimal("50")) >= 0
                ? BigDecimal.ZERO
                : new BigDecimal("5.99");
        BigDecimal total = subtotal.add(tax).add(shippingFee);


        Order order = new Order();
        order.setUser(user);
        order.setOrderNumber(generateOrderNumber());
        order.setSubtotal(subtotal);
        order.setTax(tax);
        order.setShippingFee(shippingFee);
        order.setTotal(total);
        order.setStatus("PENDING");
        order.setPlacedAt(LocalDateTime.now());

        order.setShipName(req.getShipName());
        order.setShipAddress1(req.getShipAddress1());
        order.setShipAddress2(req.getShipAddress2());
        order.setShipCity(req.getShipCity());
        order.setShipRegion(req.getShipRegion());
        order.setShipPostal(req.getShipPostal());
        order.setShipCountry(
                (req.getShipCountry() == null || req.getShipCountry().isBlank())
                        ? "US"
                        : req.getShipCountry()
        );

        orders.save(order);

        for (CartItem ci : cart.getItems()) {
            Book book = ci.getBook();
            int newStock = book.getStockQty() - ci.getQuantity();
            book.setStockQty(newStock);
            books.save(book);   // update stock

            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setBook(book);
            oi.setQuantity(ci.getQuantity());
            oi.setUnitPrice(book.getPrice());
            oi.setGrandTotal(
                    book.getPrice().multiply(BigDecimal.valueOf(ci.getQuantity()))
            );

            orderItems.save(oi);
        }

        cartItems.deleteAll(cart.getItems());
        cart.getItems().clear();
        carts.save(cart);

        CheckoutResponse resp = new CheckoutResponse(
                order.getId(),
                order.getOrderNumber(),
                order.getTotal(),
                "Order placed successfully!"
        );

        return ResponseEntity.ok(resp);
    }

    private String generateOrderNumber() {
        String random = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "ORD-" + random;
    }
    @GetMapping("/history/{userId}")
    public ResponseEntity<?> getOrderHistory(@PathVariable int userId) {

        List<Order> userOrders = orders.findByUserIdOrderByPlacedAtDesc(userId);

        List<OrderHistoryResponse> response = userOrders.stream().map(order -> {
            List<OrderItemResponse> items = order.getItems().stream()
                    .map(oi -> new OrderItemResponse(
                            oi.getId(),
                            oi.getBook().getTitle(),
                            oi.getQuantity(),
                            oi.getUnitPrice(),
                            oi.getGrandTotal()
                    ))
                    .toList();

            return new OrderHistoryResponse(
                    order.getId(),
                    order.getOrderNumber(),
                    order.getTotal(),
                    order.getPlacedAt(),
                    items
            );
        }).toList();

        return ResponseEntity.ok(response);
    }

}
