package com.bookstore.backend.AI;

import com.bookstore.backend.Book.Book;
import java.util.List;

public class AiResponse {

    private String message;
    private List<Book> books;

    public AiResponse(String message, List<Book> books) {
        this.message = message;
        this.books = books;
    }

    public String getMessage() {
        return message;
    }

    public List<Book> getBooks() {
        return books;
    }
}
