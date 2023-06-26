package com.fiveam.userservice.user.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.ZonedDateTime;


public class UserDto {
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Post {
        @NotBlank
        private String email;
        @NotBlank
        private String password;
        @NotBlank
        private String displayName;
        @NotBlank
        private String address;
        @NotBlank
        private String detailAddress;
        @NotBlank
        private String realName;
        @NotBlank
        private String phone;
    }

    @Getter
    @Setter
    @Builder
    public static class PostMoreInfo {
        @NotNull
        private String email;
        @NotNull
        private String displayName;
        @NotNull
        private String address;
        @NotNull
        private String detailAddress;
        @NotNull
        private String realName;
        @NotNull
        private String phone;
    }
    @Getter
    @Builder
    public static class Get {

        private String email;

        private String displayName;

        private String address;

        private String detailAddress;

        private String realName;

        private String phone;

        private boolean social;
    }

    @Getter
    @Builder
    public static class Response implements Serializable {

        private String email;

        private String displayName;

        private String address;

        private String detailAddress;

        private String realName;

        private String phone;

        private String password;

        private boolean social;

        private Long cartId;

        private ZonedDateTime updatedAt;

        private String sid;
    }
}
