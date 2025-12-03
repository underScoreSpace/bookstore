package com.bookstore.backend.Review;

public class ReviewResponse {
    private int id;
    private String userDisplayName;
    private int rating;
    private String comment;

    public ReviewResponse(int id, String userDisplayName, int rating, String comment) {
        this.id = id;
        this.userDisplayName = userDisplayName;
        this.rating = rating;
        this.comment = comment;
    }

    public int getId() { return id; }
    public String getUserDisplayName() { return userDisplayName; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
}
