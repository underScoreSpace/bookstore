package com.bookstore.backend.Cart;

import com.bookstore.backend.Book.Book;
import com.bookstore.backend.User.User;
import jakarta.persistence.*;

@Entity
@Table

public class Cart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer CartId;

    @OneToOne
    private Book book;

    @OneToOne
    private User user;

}
