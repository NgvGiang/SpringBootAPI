package com.usth.apiserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.usth.apiserver.entity.TokenInfo;
import com.usth.apiserver.entity.User;
import com.usth.apiserver.model.CustomUserDetail;
import com.usth.apiserver.repository.RefreshTokenRepository;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;

@Service
public class JwtTokenProvider {
    private static final Logger log = LoggerFactory.getLogger(JwtTokenProvider.class);
    final RefreshTokenRepository refreshTokenRepository;



    public enum TokenType {
        REFRESH_TOKEN,
        ACCESS_TOKEN
    }
    // Đoạn JWT_SECRET này là bí mật, chỉ có phía server biết
    private final String JWT_SECRET = "R2lhbmduZ3ZUaGlzaXNNeVNlY3JldEtleWFzZHp4Y3Y=";

    //Thời gian có hiệu lực của chuỗi jwt
    private final long MILLISECONDS_IN_A_HOUR  = 3600000L;
    private final long ACCESS_TOKEN_EXPIRATION = MILLISECONDS_IN_A_HOUR; // 6 giờ
    //Thời gian có hiệu lực của chuỗi refresh jwt
    private final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * MILLISECONDS_IN_A_HOUR; //  7 ngày

    public JwtTokenProvider(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }
    //tạo mới refresh token
    private String generateToken(User user) {
        try {
            Map<String, Object> claims = new HashMap<>();
            claims.put("id", user.getUser_id());
            claims.put("userName", user.getUsername());
            claims.put("roles", user.getRoles());
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION);
            return Jwts
                    .builder()
                    .claims(claims)
                    .subject(claims.get("userName").toString())
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(expiryDate)
                    .signWith(getSigningKey())
                    .compact();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    // tạo mới refresh token, dùng trong 2 trường hợp: một là refresh chưa có, 2 là nó hết hạn
    //xử lý: nếu null = đăng nhập lại
    public String getRefreshToken(User user) { // gọi khi đăng nhập hoặc đăng ký, với thông tin user
        // và khi cần dùng để tạo mới accesstoken
        // ở phía server
        Optional<TokenInfo> refreshTokenOptional = refreshTokenRepository.findByUser(user);
        //truy vấn db xem có tokenInfo của user tương ứng xem có tồn tại không
        TokenInfo tokenInfo;

        if (refreshTokenOptional.isPresent()) { // not đăng ký, đăng nhập.
            tokenInfo = refreshTokenOptional.get();
            if (!isTokenExpired(tokenInfo.getToken())) {
                return tokenInfo.getToken(); // chưa hết hạn thì ko tạo mới
            }else{
                refreshTokenRepository.delete(tokenInfo);
                //Yêu cầu đăng nhập lại để tạo mới
                return null;
            }
        } else { // chưa có , đăng ký tạo mới
            tokenInfo = new TokenInfo();
            tokenInfo.setUserName(user.getUsername());
            tokenInfo.setId(UUID.randomUUID().toString());
            tokenInfo.setRoles(user.getRoles());
            tokenInfo.setUser(user);
            String newRefreshToken = generateToken(user);
            tokenInfo.setToken(newRefreshToken);
            refreshTokenRepository.save(tokenInfo);// lưu vào db
            return newRefreshToken;
        }


    }
    public String createAccessTokenFromRefreshToken(String refreshToken) {
        if (isTokenExpired(refreshToken)) {
//            throw new JwtException("Refresh Token has expired. Please log in again.");
            return null;
        }
        try {
            Claims claims = getAllClaimsFromToken(refreshToken);
            Date now = new Date();
            Date expiryDate = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION);
            return Jwts
                    .builder()
                    .claims(claims)
                    .subject(claims.get("userName").toString())
                    .issuedAt(new Date(System.currentTimeMillis()))
                    .expiration(expiryDate)
                    .signWith(getSigningKey())
                    .compact();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
//            throw new RuntimeException("Error while generating access token from refresh token.", e);
            // bắtở phía controller để truyền về cho client
        }
    }
    public void deleteRefreshToken(User user) {
        refreshTokenRepository.deleteByUser(user);
    }
    public boolean isTokenValid(String jwt, CustomUserDetail customUserDetail) {
        final String username = extractUsername(jwt);
        return (username.equals(customUserDetail.getUsername())) && !isTokenExpired(jwt);
    }
    public boolean validateToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken)
                    .getPayload();
            return true;
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }
    public String extractUsername(String authToken) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(authToken)
                .getPayload();
        return claims.getSubject();
    }
    public Claims getAllClaimsFromToken(String authToken) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(authToken)
                .getPayload();
    }

//    private Claims getAllClaimsFromToken(String token) {
//            return Jwts.parser()
//                    .setSigningKey(JWT_SECRET)
//                    .build()
//                    .parseSignedClaims(token)
//                    .getPayload();
//        }

    public boolean isTokenExpired(String jwt) {
        try {
            return getAllClaimsFromToken(jwt).getExpiration().before(new Date());
        } catch (ExpiredJwtException expiredJwtException) {
            return true;
        } catch (Exception e) {
            e.printStackTrace();

            return false;
        }
    }
    public TokenInfo getTokenInfoFromJwt(String jwt) {
        try {
            Claims claims = getAllClaimsFromToken(jwt);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> claimMap = mapper.convertValue(claims, Map.class);
            claimMap.remove("iat");
            claimMap.remove("exp");

            return mapper.convertValue(claimMap, TokenInfo.class);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public SecretKey getSigningKey() {
    //    String secretSigningKey = credentialsUtils.getCredentials(SystemConfigKeyName.SECRET_SIGNING_KEY);
        byte[] keyBytes = Decoders.BASE64.decode(JWT_SECRET);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}