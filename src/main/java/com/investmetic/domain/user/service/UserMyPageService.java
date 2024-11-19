package com.investmetic.domain.user.service;

import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserMyPageService {

    private final UserRepository userRepository;


    /**
     * 개인 정보 제공
     */
    public UserProfileDto provideUserInfo(String email) {

        //BaseResponse.fail를 사용할 만한 것들은 일단 다 예외로 던지기.
        return userRepository.findByEmailUserInfo(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_INFO_NOT_FOUND));
    }

}
