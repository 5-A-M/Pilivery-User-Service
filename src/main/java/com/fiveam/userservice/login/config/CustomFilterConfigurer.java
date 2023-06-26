package com.fiveam.userservice.login.config;


import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.stereotype.Component;
import com.fiveam.userservice.login.filter.JwtLoginFilter;
import com.fiveam.userservice.login.filter.JwtVerificationFilter;
import com.fiveam.userservice.login.handler.UserAuthFailureHandler;
import com.fiveam.userservice.login.handler.UserAuthSuccessHandler;
import com.fiveam.userservice.login.jwt.JwtToken;
import com.fiveam.userservice.redis.RedisConfig;

@Component
@RequiredArgsConstructor
public class CustomFilterConfigurer extends AbstractHttpConfigurer<CustomFilterConfigurer, HttpSecurity> {
    private final JwtToken jwtToken;
    private final RedisConfig redisConfig;

    @Override
    public void configure( HttpSecurity builder ) throws Exception{
        AuthenticationManager authenticationManager = builder.getSharedObject(AuthenticationManager.class);
        JwtLoginFilter jwtLoginFilter = new JwtLoginFilter(authenticationManager,jwtToken);//필터 실행
        jwtLoginFilter.setFilterProcessesUrl("/users/login"); //로그인 디폴트 url

        jwtLoginFilter.setAuthenticationFailureHandler(new UserAuthFailureHandler());//로그인 실패시 핸들러 설정
        jwtLoginFilter.setAuthenticationSuccessHandler(new UserAuthSuccessHandler(jwtToken ));//로그인 성공시 핸들러 설정

        JwtVerificationFilter jwtVerificationFilter = new JwtVerificationFilter(redisConfig, jwtToken); //jwt인증 필터 설정

        builder.addFilter(jwtLoginFilter) //로그인 필터 추가
                .addFilterAfter(jwtVerificationFilter, JwtLoginFilter.class);//로그인 필터가 실행된 바로 다음 jwt인증 필터 실행
    }
}
