package com.fiveam.userservice.user.service;

import com.fiveam.userservice.client.ItemServiceClient;
import com.fiveam.userservice.exception.bussiness.BusinessLogicException;
import com.fiveam.userservice.exception.bussiness.ExceptionCode;
import com.fiveam.userservice.login.jwt.JwtToken;
import com.fiveam.userservice.user.dto.UserDto;
import com.fiveam.userservice.user.entity.AuthUtils;
import com.fiveam.userservice.user.entity.User;
import com.fiveam.userservice.user.entity.UserStatus;
import com.fiveam.userservice.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtils authUtils;
    private final JwtToken jwtToken;
    private final UserInfoFilter userInfoFilter;
    private final ItemServiceClient itemServiceClient;

    public User joinUser( User user ){
        log.warn(user.getName() + " " + user.getEmail());
        userInfoFilter.filterUserInfo(user);
        encodePassword(user);
        createRole(user);
        user = userRepository.save(user);
        System.out.println(user);
        makeCart(user);
        return user;
    }


    public User deleteUser(){
        User loginUser = getLoginUser();
        loginUser.setUserStatus(UserStatus.USER_WITHDRAWAL);
        return loginUser;
    }

    public User updateUser( UserDto.Post userDto ){

        userInfoFilter.filterUpdateUser(userDto);

        User loginUser = getLoginUser();
        loginUser.setAddress(userDto.getAddress());
        loginUser.setPhone(userDto.getPhone());
        loginUser.setRealName(userDto.getRealName());
        loginUser.setDisplayName(userDto.getDisplayName());
        loginUser.setDetailAddress(userDto.getDetailAddress());

        String encodedPwd = passwordEncoder.encode(userDto.getPassword());
        loginUser.setPassword(encodedPwd);

        return loginUser;
    }

    public User updateOAuthInfo( UserDto.PostMoreInfo userDto ){

        Optional<User> loginUser = userRepository.findByEmail(userDto.getEmail());

        if(loginUser.isPresent()){
            makeCart(loginUser.get());
            userInfoFilter.filterMoreInfo(userDto);
            loginUser.get().setUserStatus(UserStatus.USER_ACTIVE);
            loginUser.get().setAddress(userDto.getAddress());
            loginUser.get().setDetailAddress(userDto.getDetailAddress());
            loginUser.get().setRealName(userDto.getRealName());
            loginUser.get().setPhone(userDto.getPhone());
            loginUser.get().setDisplayName(userDto.getDisplayName());
            loginUser.get().setCreatedAt(ZonedDateTime.now(ZoneId.of("Asia/Seoul")));

            return loginUser.get();
        }

        throw new BusinessLogicException(ExceptionCode.USER_NOT_FOUND);
    }

    public void giveToken( User user, HttpServletResponse response ) throws IOException{
        String s = jwtToken.delegateAccessToken(user);
        String r = jwtToken.delegateRefreshToken(user);
        String accessToken = "Bearer " + s;
        String refreshToken = "Bearer " + r;

        response.setHeader("Authorization", accessToken);
        response.setHeader("Refresh", refreshToken);
        response.setHeader("userId", String.valueOf(user.getUserId()));

        response.getWriter().write("추가 정보 기입 완료");
    }

    private void createRole( User user ){
        List<String> roles = authUtils.createRoles();
        user.setRoles(roles);
    }

    private void encodePassword( User user ){
        String encodedPwd = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPwd);
    }

    public User getLoginUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        log.info("회원 이메일 = {}", name);
        Optional<User> user = userRepository.findByEmail(name);
        return user.orElseThrow(() -> new AuthenticationServiceException("Authentication exception"));
    }

    public Long getUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        Optional<User> user = userRepository.findByEmail(name);
        if(user.isPresent()) return user.get().getUserId();
        throw new AuthenticationServiceException("Authentication exception");
    }

    private void makeCart( User user ){
        Long cartId = itemServiceClient.createCart(user.getUserId());
        user.setCartId(cartId);
        System.out.println(cartId + " " + user.getUserId());
        userRepository.save(user);
    }

    public User findUserById(Long userId) {
        return userRepository.findById(userId).orElseThrow(
                () -> new AuthenticationServiceException("Authentication exception")
        );
    }
}
