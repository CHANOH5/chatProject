package com.whispeer.chat.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // 매 요청에서 JWT를 검사하고 인증 처리 (Security Context)

    // 1. 요청 헤더에서 Authorization: Bearer <token> 추출
    // 2. 토근이 유효한지 검사
    // 3. 토큰에서 사용자 ID 꺼냄 → DB에서 사용자 로딩

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 추출
        String authHeader = request.getHeader("Authorization");
        String jwtToken = null;
        String userId = null;

        String path = request.getServletPath();
        if (path.equals("/login") || path.equals("/join")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
            userId = jwtUtil.getUserIdFromToken(jwtToken);
        } // if

        // 2. 토큰 유효성 검사 & SecurityContext가 비어있을 경우
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserPrincipal userDetails = (UserPrincipal) userDetailsService.loadUserByUsername(userId);

            if(jwtUtil.validateToken(jwtToken)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(new WebAuthenticationDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken); // 인증 완료

            } // inner if

        } // if

        // 다음 필터로 넘어감
        filterChain.doFilter(request, response);

    } // doFilterInternal

} // end class
