package com.bookstore.backend.User;

public class LoginResponse {
    private int id;
    private String email;
    private String firstName;
    private String lastName;
    private String role;

    public LoginResponse(int id, String email,
                         String firstName, String lastName, String role) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
    }

    public int getId() { return id; }
    public String getEmail() { return email; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getRole() { return role; }
}