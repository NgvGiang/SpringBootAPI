package com.usth.apiserver.repository;

import com.usth.apiserver.entity.TokenInfo;
import com.usth.apiserver.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Repository
public interface RefreshTokenRepository extends JpaRepository<TokenInfo, String> {
//    TokenInfo findByToken(String token);
//    void deleteByToken(String token);
//    Optional<TokenInfo> getTokenInfoByToken(String token);
//    Optional<TokenInfo> getTokenInfoByUserName(String userName);
//    Optional<TokenInfo> getTokenInfoByUserNameAndToken(String userName, String token);
//
//    Optional<TokenInfo> getRefreshTokenInfoByUserId(int id);
//
//    Optional<TokenInfo> getTokenInfoByUserUser_id(int id);
//    Optional<TokenInfo> findByUser_User_id(int id);
    Optional<TokenInfo> findByUser(User user);

    void deleteByUser(User user);

    @Modifying
    @Transactional
    @Query(value = "delete from token_info where token_info.user_id = ?",nativeQuery = true)
    void deleteToken( int id);
}
