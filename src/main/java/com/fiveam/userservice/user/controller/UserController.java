package com.fiveam.userservice.user.controller;

import com.fiveam.userservice.response.UserInfoResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import com.fiveam.userservice.logout.Logout;
import com.fiveam.userservice.user.dto.UserDto;
import com.fiveam.userservice.user.entity.User;
import com.fiveam.userservice.user.mapper.UserMapper;
import com.fiveam.userservice.user.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final Logout logout;
    private final UserMapper mapper;
    private final UserService userService;

    @PostMapping
    public ResponseEntity singUpUser( @Valid @RequestBody UserDto.Post userDto ){
        User user = mapper.dtoToUser(userDto);
        userService.joinUser(user);
        String response = "회원가입이 완료되었습니다.";
        return new ResponseEntity(response, HttpStatus.CREATED);
    }
    @PostMapping("/more-info")
    public ResponseEntity moreInfo( @Valid @RequestBody UserDto.PostMoreInfo userDto, HttpServletResponse response ) throws IOException{
        User user = userService.updateOAuthInfo(userDto);
        userService.giveToken(user, response);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    @PatchMapping
    public ResponseEntity updateInfo( @Valid @RequestBody UserDto.Post userDto ){
        log.info("유저 정보 수정 진입: " + userDto.getRealName());
        User user = userService.updateUser(userDto);
        UserDto.Response userInfo = mapper.userToDto(user, HttpMethod.PATCH);
        return new ResponseEntity<>(userInfo, HttpStatus.ACCEPTED);
    }
    @GetMapping
    public ResponseEntity getUserInfo(){
        User loginUser = userService.getLoginUser();
        if(loginUser.getProvider() != null){

        }
        UserDto.Response userInfo = mapper.userToDto(loginUser, HttpMethod.GET);
        return new ResponseEntity<>(userInfo, HttpStatus.ACCEPTED);
    }

    @GetMapping("/logout")
    public String handleLogout( HttpServletRequest request ){
        logout.doLogout(request);
        return "로그아웃이 되었습니다!";
    }

    @DeleteMapping
    public ResponseEntity deleteUser( HttpServletRequest request ){
        User user = userService.deleteUser();
        logout.doLogout(request);
        return new ResponseEntity<>(user.getUserStatus().getStatus(), HttpStatus.ACCEPTED);
    }

    @GetMapping("/{userId}")
    public ResponseEntity findUserById(@PathVariable Long userId) {
        return new ResponseEntity<>(
                UserInfoResponseDto.fromEntity(userService.findUserById(userId)),
                HttpStatus.ACCEPTED
        );
    }

}
