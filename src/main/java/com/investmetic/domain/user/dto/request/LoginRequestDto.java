package com.investmetic.domain.user.dto.request;

import lombok.Getter;

@Getter
public class LoginRequestDto {
    String email;
    String password;
}