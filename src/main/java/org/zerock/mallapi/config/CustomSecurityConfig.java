package org.zerock.mallapi.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.zerock.mallapi.security.filter.JWTCheckFilter;
import org.zerock.mallapi.security.handler.ApiLoginFailHandler;
import org.zerock.mallapi.security.handler.ApiLoginSuccessHandler;
import org.zerock.mallapi.security.handler.CustomAccessDeniedHandler;

import java.util.Arrays;
import java.util.List;

@Configuration
@Log4j2
@RequiredArgsConstructor
@EnableMethodSecurity // 이전에는 globalSecurity 어쩌고..
public class CustomSecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http.cors(httpSecurityCorsConfigurer -> {
            httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
        });

        // csrf 설정 - request 위조방지지만..이를 이용하여 구현하려면 자질구레한일이많아져서 일반적으로 disabled
        http.csrf(AbstractHttpConfigurer::disable);

        // 로그인화면 지정
        http.formLogin(config -> {
            config.loginPage("/api/member/login");
            // 성공시에 뭐가 동작하도록할것인지
            config.successHandler(new ApiLoginSuccessHandler());
            // 실패시에 뭐가 동작하도록할것인지
            config.failureHandler(new ApiLoginFailHandler());
        });

        // 세션만들지않도록 설정
        http.sessionManagement(httpSecuritySessionManagementConfigurer -> {
            httpSecuritySessionManagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.NEVER);
        });

        // 일반적으로 UserNameAuthenticationFilter 사용자 아이디와 패스워드로 검증하는필터가 있는데
        // 그전에 보통 동작을 시킴
        http.addFilterBefore(new JWTCheckFilter(), UsernamePasswordAuthenticationFilter.class);

        http.exceptionHandling(config -> {
            config.accessDeniedHandler(new CustomAccessDeniedHandler());
        });

//        http
//                .authorizeHttpRequests(
//                        authorize -> authorize
//                                .requestMatchers("/member/join").permitAll()
//                                .requestMatchers("/auth/login").permitAll()
//                                .anyRequest().authenticated()
//                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
