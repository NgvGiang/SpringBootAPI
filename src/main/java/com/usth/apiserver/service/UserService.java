package com.usth.apiserver.service;

import com.usth.apiserver.entity.User;
import com.usth.apiserver.DTO.UserDTO;
import com.usth.apiserver.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;

    // Inject UserRepository thông qua constructor
    public UserService(UserRepository userRepository, JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
    }

    // Trả về danh sách user dưới dạng DTO
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(user -> new UserDTO(user.getUser_id(), user.getUsername(), user.getEmail()))
                .collect(Collectors.toList());
    }

    // Trả về thông tin một user theo ID
    public UserDTO getUserDTOById(int id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserDTO(user.getUser_id(), user.getUsername(), user.getEmail());
    }
    // nếu không dùng DTO thì có thể trả về User luôn nhưng cần chú ý vấn đề bảo mật
    //  vì thông tin trả về sẽ không được chọn mà nó sẽ trả đầy đủ data được khai báo trong entity

    public User getUserById(int id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }


    public UserDTO getUserInfo(String token) {
        String username = tokenProvider.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return new UserDTO(user.getUser_id(), user.getUsername(), user.getEmail());
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }
}
