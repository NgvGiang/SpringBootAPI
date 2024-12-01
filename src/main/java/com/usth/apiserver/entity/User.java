package com.usth.apiserver.entity;

import jakarta.persistence.*;

@Table (name = "users")
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int user_id;

    private String username;
    private String email;
    private String password;
    private String roles;

    @OneToOne(mappedBy = "user")
    private TokenInfo tokenInfo;

    public TokenInfo getTokenInfo() {
        return tokenInfo;
    }

    public void setTokenInfo(TokenInfo tokenInfo) {
        this.tokenInfo = tokenInfo;
    }

    public User(String username, String email, String password, String roles) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public User() {
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUser_id(int id) {
        this.user_id = id;
    }

    public int getUser_id() {
        return this.user_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
    public String getRoles() {
        return roles;
    }
}
