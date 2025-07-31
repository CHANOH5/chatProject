package com.whispeer.chat.config;

import com.whispeer.chat.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    } // passwordEncoder

//    @Bean
//    public static BCryptPasswordEncoder bCryptPasswordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

    // /css/**, /js/**, /images/** 등 정적 자원에 대해 보안 필터 무시
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    } // bCryptPasswordEncoder

    // 보안 로직(인증, 인가, 필터 등록, 로그인 방식 등)의 출입문
    // JWT 적용시에는 매 요청마다 JWT를 적용시켜야함
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//
//        http
//                .csrf(AbstractHttpConfigurer::disable)                                          // CSRF(Cross Site Request Forgery) 공격을 끔, JWT 기반 REST API에서는 불필요함
//                .authorizeHttpRequests(requests -> requests        // HTTP 요청 URL에 따라 접근 권한을 설정함
//                        .requestMatchers("/", "/login", "/join").permitAll()              // requestMatchers(url).permitAll() 포함된 url은 인증 없이 접근 허용함
//                        .anyRequest().authenticated()
//                )
//                .formLogin(form -> form                          // Spring Security 기본 로그인 처리 방식인 로그인 폼
//                        .loginPage("/login")                                                    // /login 경로로 로그인 페이지 띄움
//                        .defaultSuccessUrl("/", true)                  // /경로로 리다이렉트
//                        .permitAll()
//                )
//                .logout(logout -> logout
//                        .permitAll());                                                          // 로그아웃 URL(/logout) 접근을 누구나 가능하게 허용
//                                                                                                // 하지만 우리는 세션이 아닌 JWT 기반 인증으로 할거니까 추후에 logout 처리도 커스터마이징하거나 제거할 수 있음
//        return http.build();
//    } // securityFilterChain

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        auth  -> auth
                                .requestMatchers(
                                        "/api/v1/login",
                                        "/",
                                        "/api/v1/**"
                                )
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();

    } // securityFilterChain

} // end class
