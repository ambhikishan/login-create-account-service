package com.example.Login.dto;


public class LoginDetails {
    private String username;
    private String password;
    private Long id;
    private String email;
    private boolean loggedIn = false;

    @Override
    public String toString() {
        return "LoginDetails{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", loggedIn=" + loggedIn +
                ", username='" + username + '\'' +
                '}';
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
}
