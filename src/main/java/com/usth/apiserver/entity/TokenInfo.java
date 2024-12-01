package com.usth.apiserver.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.Collections;

@Table(name = "token_info")
@Entity
public class TokenInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id; // String for UUID
    @Column (name = "token")
    private String token;
    private String userName;
    private String roles;
    @OneToOne(fetch = FetchType.LAZY) // Quan hệ với User, lazy loading
    @JoinColumn(name = "user_id",referencedColumnName = "user_id", nullable = false) // FK đến bảng user
    private User user;

    public void setId(String id) {
        this.id = id;
    }



    public void setUser(User user){
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public String getId() {
        return id;
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null || roles.isEmpty()) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
        }
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roles.toUpperCase()));
    }
}
