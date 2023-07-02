package com.fiveam.userservice.response;

import com.fiveam.userservice.user.entity.User;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Getter
@Setter
@Builder
@ToString
public class UserInfoResponseDto implements Serializable {
    private Long id;

    private String email;

    private String displayName;

    private String address;

    private String detailAddress;

    private String realName;

    private String phone;

    private String password;

    private Long cartId;

    private Boolean social;

    private String sid;

    private ZonedDateTime updatedAt;

    public static UserInfoResponseDto fromEntity(User user) {
        return UserInfoResponseDto.builder()
                .id(user.getUserId())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .address(user.getAddress())
                .detailAddress(user.getDetailAddress())
                .realName(user.getRealName())
                .phone(user.getPhone())
                .password(user.getPassword())
                .cartId(user.getCartId())
                .social(user.getProvider().equals("default") ? false : true)
                .sid(user.getSid())
                .updatedAt(user.getUpdatedAt())
                .build();

    }
}
