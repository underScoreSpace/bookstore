package com.bookstore.backend.Cart;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Integer> {

    Optional<CartItem> findByCartIdAndBookId(int cartId, int bookId);
    void deleteByCartId(int cartId);
}
