package com.fiveam.userservice.login.handler;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import com.fiveam.userservice.login.details.PrincipalDetails;
import com.fiveam.userservice.login.jwt.JwtToken;
import com.fiveam.userservice.user.entity.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;


/*
로그인 성공 시 마지막에 오는 클래스
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserAuthSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JwtToken jwtToken;

    @Value("${front.scheme}")
    private String scheme;

    @Value("${front.host}")
    private String host;

    @Value("${front.port}")
    private String port;

    @Override
    public void onAuthenticationSuccess( HttpServletRequest request, HttpServletResponse response, Authentication authentication ) throws IOException, ServletException{

        PrincipalDetails principalDetails = getPrincipalDetails(authentication);
        log.warn("닉네임 = {}", principalDetails.getUser().getDisplayName());

        if(principalDetails.getUser().getDisplayName() == null){
            log.warn("추가 정보 기입");
            redirectInfo(request, response, authentication);
            return;
        }
        redirect(response, principalDetails);
    }

    private void redirect( HttpServletResponse response, PrincipalDetails principalDetails ) throws IOException{
        List<String> tokens = delegateToken(principalDetails.getUser(), jwtToken);
        String uri = createURI(tokens.get(0), tokens.get(1), principalDetails).toString();
        response.sendRedirect(uri);
    }
    private void redirectInfo( HttpServletRequest request, HttpServletResponse response, Authentication authentication ) throws IOException{
        PrincipalDetails principalDetails = getPrincipalDetails(authentication);
        log.error("principal = {}", principalDetails);
        String uri = createInfoURI(principalDetails).toString();
        getRedirectStrategy().sendRedirect(request, response, uri);
    }

    private List<String> delegateToken( User user, JwtToken jwtToken ){
        List<String> tokens = new ArrayList<>();

        tokens.add(jwtToken.delegateAccessToken(user));
        tokens.add(jwtToken.delegateRefreshToken(user));

        return tokens;
    }

    private PrincipalDetails getPrincipalDetails( Authentication authentication ){
        return (PrincipalDetails) authentication.getPrincipal();
    }

    private URI createURI( String accessToken, String refreshToken, PrincipalDetails principalDetails ){
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("access_token", "Bearer " + accessToken);
        queryParams.add("refresh_token", refreshToken);
        queryParams.add("userId", String.valueOf(principalDetails.getUser().getUserId()));
        log.info("query = {}", queryParams);
        return UriComponentsBuilder.newInstance().scheme(scheme).host(host).port(port)
                .queryParams(queryParams).build().toUri();
    }

    private URI createInfoURI( PrincipalDetails principalDetails ){
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();

        queryParams.add("email", principalDetails.getUsername());
        queryParams.add("userId", String.valueOf(principalDetails.getUser().getUserId()));
        log.error("{}", queryParams);
        return UriComponentsBuilder.newInstance().scheme(scheme).host(host).port(port)
                .path("/signup").queryParams(queryParams).build().toUri();
    }

}
