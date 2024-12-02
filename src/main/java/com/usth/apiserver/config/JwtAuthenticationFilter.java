package com.usth.apiserver.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.usth.apiserver.entity.TokenInfo;
import com.usth.apiserver.model.CustomUserDetail;
import com.usth.apiserver.service.CustomUserDetailService;
import com.usth.apiserver.service.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtAuthenticationFilter  extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailService userDetailsService;


    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    public String buildJsonResponse(String result, Object content, String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> response = new HashMap<>();
        response.put("result", result);
        response.put("content", content);
        response.put("message", message);

        try {
            return objectMapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to build JSON response", e);
        }
    }
//    @Override
//    protected boolean shouldNotFilter(HttpServletRequest request){
////        return !request.getRequestURI().contains("/auth");
//        String uri = request.getRequestURI();
//        // Danh sách endpoint không cần filter
//        return
//                uri.startsWith("/public-api/") ||
//                uri.equals("/auth") ||
//                uri.equals("/refresh-token");
//    }
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
            ) throws ServletException, IOException {
                // Lấy JWT từ header Authorization
                final String authHeader = request.getHeader("Authorization");
                final String jwt;
                final String username ;
                final TokenInfo tokenInfo;

        try {
////            SecurityUtils.storeSecurityContext();
//            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
//            response.setHeader("Access-Control-Allow-Credentials", "true");
//            response.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, PATCH, HEAD, OPTIONS, DELETE");
//            response.setHeader("Access-Control-Max-Age", "3600");
//            response.setHeader("Access-Control-Allow-Headers", "Content-Type, Accept, X-Requested-With, remember-me");

            // generate request ID
//            String requestId = UUID.randomUUID().toString();
//            request.setAttribute(ConstantValue.REQUEST_ID, requestId);
//            log.info("Current Request ID: {}", request.getAttribute(ConstantValue.REQUEST_ID).toString());

            // if header is not null and starts with word 'Bearer' -> proceed filter
            if (authHeader != null && authHeader.startsWith("Bearer")) {
                jwt = authHeader.substring(7);
                logger.info("Client jwt: " + jwt);
                username = jwtTokenProvider.extractUsername(jwt);
                boolean isJwtExpired = jwtTokenProvider.isTokenExpired(jwt);
                boolean isJwtValid = jwtTokenProvider.validateToken(jwt);
                // if jwt is expired, or user has not been authorized
                if (isJwtExpired || !isJwtValid ||
                        SecurityContextHolder.getContext() == null ||
                        SecurityContextHolder.getContext() instanceof AnonymousAuthenticationToken) {
                    logger.error("Client JWT expired");
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType("application/json");
                    response.getWriter().write(
                            buildJsonResponse("failed", null, "JWT has been expired")
                    );
                    return;
                } else {
                    // get token info from jwt
                    CustomUserDetail customUserDetail = (CustomUserDetail) this.userDetailsService.loadUserByUsername(username);
                    if (jwtTokenProvider.isTokenValid(jwt, customUserDetail)) {
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                customUserDetail,
                                null,
                                customUserDetail.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                }
            }
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("application/json");
            response.getWriter().write(
                    buildJsonResponse("failed", null, "Internal server error")
            );
        }
    }
}
