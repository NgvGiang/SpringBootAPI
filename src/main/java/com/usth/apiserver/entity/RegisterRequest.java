package com.usth.apiserver.entity;
import jakarta.validation.constraints.*;
public class RegisterRequest {
    @NotBlank(message = "Username is required")
    private String username;
    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Email is required")
//    @Pattern(regexp = ".+@.+\\..+", message = "Invalid email format")
    @Email(message = "Invalid email format")
    private String email;
    @NotBlank(message = "Role is required")
    private String roles;

    public RegisterRequest(String username, String password, String email, String roles) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roles = roles;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }
}
