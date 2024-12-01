package com.usth.apiserver.service;

import com.usth.apiserver.entity.LoginRequest;
import com.usth.apiserver.entity.RegisterRequest;
import com.usth.apiserver.entity.AuthResponse;
import com.usth.apiserver.entity.User;
import com.usth.apiserver.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Service
public class AuthenticationService  {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    private final UserRepository userRepository ;
    private final PasswordEncoder passwordEncoder;

    public AuthenticationService(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

//    public ResponseEntity<?> login(User user) {
//        Authentication authentication = authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(),user.getAuthorities())
//        );
//        if (authentication.isAuthenticated()) {
//            User user = (User) authentication.getPrincipal();
//            String refreshToken = jwtTokenProvider.getRefreshToken(user);
//            String accessToken = jwtTokenProvider.createAccessTokenFromRefreshToken(refreshToken);
//            Map<String, String> tokens = new HashMap<>();
//            tokens.put("refreshToken", refreshToken);
//            tokens.put("accessToken", accessToken);
//            return  ResponseEntity.ok(tokens);
//        } else {
//            throw new RuntimeException("Invalid credentials");
//        }
//
//    }
    public AuthResponse registerUser(@RequestParam RegisterRequest registerRequest) { // ko cần @Valid vì đã kiểm tra ở controller
        User user = new User(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword()),
                "User"
        );

        userRepository.save(user);
        String refreshToken = jwtTokenProvider.getRefreshToken(user);
        String accessToken = jwtTokenProvider.createAccessTokenFromRefreshToken(refreshToken);
        return new AuthResponse(accessToken, refreshToken,"User created successfully.");
    }
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public AuthResponse login(@Valid LoginRequest loginRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),loginRequest.getPassword())
        );
        // nếu 3 dòng trên không ném ra exception thì user xác thực.
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        if (userOptional.isPresent()) {
            String refToken = jwtTokenProvider.getRefreshToken(userOptional.get());
            String accessToken = jwtTokenProvider.createAccessTokenFromRefreshToken(refToken);
            return new AuthResponse(accessToken,refToken,"Success!");
        }
        return new AuthResponse(null,null,"User not found!");
    }
}
