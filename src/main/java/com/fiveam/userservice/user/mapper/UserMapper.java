package com.fiveam.userservice.user.mapper;

import org.mapstruct.Mapper;
import org.springframework.http.HttpMethod;
import com.fiveam.userservice.user.dto.UserDto;
import com.fiveam.userservice.user.entity.User;
import com.fiveam.userservice.user.entity.UserStatus;

import java.time.ZoneId;
import java.time.ZonedDateTime;

@Mapper(componentModel = "spring")
public interface UserMapper {
     String DEFAULT_OAUTH2_ID = "default";
    default User dtoToUser( UserDto.Post userDto ){
        User.UserBuilder user = User.builder();

        user.email(userDto.getEmail());
        user.displayName(userDto.getDisplayName());
        user.password(userDto.getPassword());
        user.address(userDto.getAddress());
        user.detailAddress(userDto.getDetailAddress());
        user.realName(userDto.getRealName());
        user.phone(userDto.getPhone());
        user.oauthId(DEFAULT_OAUTH2_ID);
        user.userStatus(UserStatus.USER_ACTIVE);
        user.provider(DEFAULT_OAUTH2_ID);
        user.providerId(DEFAULT_OAUTH2_ID);

        return user.build();
    }

    default UserDto.Response userToDto( User user , HttpMethod method){

        return UserDto.Response.builder()
                .id(user.getUserId())
                .address(user.getAddress())
                .phone(user.getPhone())
                .displayName(user.getDisplayName())
                .address(user.getAddress())
                .detailAddress(user.getDetailAddress())
                .email(user.getEmail())
                .realName(user.getRealName())
                .cartId(user.getCartId())
                .sid(user.getSid())
                .updatedAt(method == HttpMethod.GET ? user.getCreatedAt() : ZonedDateTime.now(ZoneId.of("Asia/Seoul")))
                .password("정보가 변경되었습니다.")
                .social(!user.getProviderId().equals("default"))
                .build();
    }
}
