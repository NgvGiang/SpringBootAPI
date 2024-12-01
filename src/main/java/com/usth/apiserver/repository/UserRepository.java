package com.usth.apiserver.repository;


import com.usth.apiserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    // tự có các hàm cơ bản như findByID, findAll, save, delete, count, exists,...
    // ngoài ra có thể khai báo thêm các hàm tùy chỉnh
    Optional<User> findByUsername(String name);

    Optional<User> findByEmail(String email);
}
