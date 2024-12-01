package com.usth.apiserver.controller;

import com.usth.apiserver.DTO.UserDTO;
import com.usth.apiserver.entity.User;
import com.usth.apiserver.repository.RefreshTokenRepository;
import com.usth.apiserver.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/")
public class UserController {


    private final UserService userService ;
    private final RefreshTokenRepository refreshTokenRepository;
    public UserController(UserService userService, RefreshTokenRepository refreshTokenRepository) {
        this.userService = userService;
        this.refreshTokenRepository = refreshTokenRepository;
    }


    // API để lấy danh sách tất cả user
    @GetMapping("/")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }
    // API để lấy một user theo ID
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable int id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        String token;
        token = authHeader.substring(7); // Bỏ "Bearer " để lấy JWT
        UserDTO user = userService.getUserInfo(token);
        return ResponseEntity.ok(user);
    }
    @GetMapping("/test")
    public String test() {
        return "test";
    }
    @DeleteMapping("/admin/delete")
    public ResponseEntity<String> deleteUser(@RequestHeader("Authorization") String authHeader, @RequestParam(name = "id") int id) {
        refreshTokenRepository.deleteToken(id);
        userService.deleteUser(id);
        return ResponseEntity.ok("User deleted");
    }

}
