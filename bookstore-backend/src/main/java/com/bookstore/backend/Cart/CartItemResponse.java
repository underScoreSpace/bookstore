package com.bookstore.backend.Cart;

public class CartItemResponse {
    private int bookId;
    private String title;
    private String author;
    private double price;
    private int quantity;
    private String isbn;

    public CartItemResponse(int bookId, String title, String author,
                            double price, int quantity, String isbn) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.price = price;
        this.quantity = quantity;
        this.isbn = isbn;
    }

    public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public String getIsbn() { return isbn; }
}
