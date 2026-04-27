package com.group1.quiz.models;

public class Users {

    private String uid;
    private String name;
    private String email;
    private String role;

    // Empty constructor (required for Firestore)
    public Users() {}

    // Full constructor
    public Users(String uid, String name, String email, String role) {

        this.uid = uid;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    // Getters and Setters
    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }


    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }


    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}