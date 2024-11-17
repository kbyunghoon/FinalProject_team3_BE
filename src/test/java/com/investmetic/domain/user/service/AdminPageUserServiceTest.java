package com.investmetic.domain.user.service;


import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import com.investmetic.domain.user.dto.object.ColumnCondition;
import com.investmetic.domain.user.dto.object.RoleCondition;
import com.investmetic.domain.user.dto.request.UserAdminPageRequestDto;
import com.investmetic.domain.user.dto.response.UserProfileDto;
import com.investmetic.domain.user.model.Role;
import com.investmetic.domain.user.model.entity.User;
import com.investmetic.domain.user.repository.UserHistoryRepository;
import com.investmetic.domain.user.repository.UserRepository;
import com.investmetic.global.exception.BusinessException;
import com.investmetic.global.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class AdminPageUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserHistoryRepository userHistoryRepository;


    @InjectMocks
    private UserAdminService userAdminService;


    @Nested
    @DisplayName("회원 목록 조회")
    class AdminUserLists{

        @ParameterizedTest
        @DisplayName("role이 정상값일 경우")
        @EnumSource(value = RoleCondition.class, names = {"ALL", "TRADER","INVESTOR","ADMIN"})
        void adminUserLists1(RoleCondition roleCondition){

            // given
            UserAdminPageRequestDto requestDto = UserAdminPageRequestDto.createDto(null, null, roleCondition);
            Pageable pageable = PageRequest.of(0, 9);

            List<UserProfileDto> list = new ArrayList<>();
            list.add(new UserProfileDto());

            when(userRepository.getAdminUsersPage(requestDto, pageable)).thenReturn(new PageImpl<UserProfileDto>(list));

            // when, then
            assertThatCode(()->userAdminService.getUserList(requestDto, pageable)).doesNotThrowAnyException();
        }

        @ParameterizedTest
        @DisplayName("role이 오류값일 경우")
        @EnumSource(value = RoleCondition.class, names = {"TRADER_ADMIN","INVESTOR_ADMIN"})
        void adminUserLists2(RoleCondition roleCondition){

            // given
            UserAdminPageRequestDto requestDto = UserAdminPageRequestDto.createDto(null, null, roleCondition);
            Pageable pageable = PageRequest.of(0, 9);

            // when, then
            assertThatThrownBy(()->userAdminService.getUserList(requestDto, pageable))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.HANDLE_ACCESS_DENIED.getMessage());

        }

        @ParameterizedTest
        @DisplayName("condition이 정상값일 경우")
        @EnumSource(value = ColumnCondition.class, names = {"NICKNAME","NAME","EMAIL","PHONE"})
        void adminUserLists3(ColumnCondition roleCondition){

            // given
            UserAdminPageRequestDto requestDto = UserAdminPageRequestDto.createDto(null, roleCondition, RoleCondition.ALL);
            Pageable pageable = PageRequest.of(0, 9);

            List<UserProfileDto> list = new ArrayList<>();
            list.add(new UserProfileDto());

            when(userRepository.getAdminUsersPage(requestDto, pageable)).thenReturn(new PageImpl<UserProfileDto>(list));


            // when, then
            assertThatCode(()->userAdminService.getUserList(requestDto, pageable)).doesNotThrowAnyException();

        }


        @ParameterizedTest
        @DisplayName("condition이 오류값일 경우")
        @EnumSource(value = ColumnCondition.class, names = {"ID"})
        void adminUserLists4(ColumnCondition columnCondition){

            // given
            UserAdminPageRequestDto requestDto = UserAdminPageRequestDto.createDto(null, columnCondition, RoleCondition.ALL);
            Pageable pageable = PageRequest.of(0, 9);

            // when, then
            assertThatThrownBy(()->userAdminService.getUserList(requestDto, pageable))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.HANDLE_ACCESS_DENIED.getMessage());
        }

        @Test
        @DisplayName("content가 null인 경우.")
        void adminUserLists5(){
            // given
            UserAdminPageRequestDto requestDto = UserAdminPageRequestDto.createDto(null, null, RoleCondition.ALL);
            Pageable pageable = PageRequest.of(10000, 9);

            // content가 null 인 경우.
            when(userRepository.getAdminUsersPage(requestDto, pageable)).thenReturn(new PageImpl<UserProfileDto>(new ArrayList<UserProfileDto>()));

            assertThatThrownBy(()->userAdminService.getUserList(requestDto, pageable))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.USERS_NOT_FOUND.getMessage());
        }

    }


    @Nested
    @DisplayName("회원 둥급 변경")
    class RoleChange{

        private User createOneUser(Role role) {
            User.UserBuilder userBuilder = User.builder()
                    .userName("testUser")
                    .nickname("testNickname")
                    .phone("01012345678")
                    .birthDate("19900101")
                    .password("password")
                    .email("test@example.com")
                    .infoAgreement(true);

            userBuilder.role(Objects.requireNonNullElse(role, Role.INVESTOR));

            User user = userBuilder.build();

            userRepository.save(user);
            return user;
        }

        @ParameterizedTest
        @MethodSource("normalRoleProvider")
        @DisplayName("{0}")
        void roleChangeTest1(String testName, RoleCondition roleCondition, Role previousRole){

            //given
            User user = createOneUser(previousRole);
            when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

            // when, then 유저가 있다고 가정하고 userId는 값을 넣어줌.
            assertThatCode( () -> userAdminService.modifyRole(1L, roleCondition)).doesNotThrowAnyException();


        }

        static Stream<Arguments> normalRoleProvider() {
            return Stream.of(
                    arguments("INVESTOR -> ADMIN", RoleCondition.ADMIN, Role.INVESTOR),
                    arguments("TRADER->ADMIN", RoleCondition.ADMIN, Role.TRADER),
                    arguments("INVESTOR_ADMIN->INVESTOR", RoleCondition.INVESTOR, Role.INVESTOR_ADMIN),
                    arguments("TRADER_ADMIN->TRADER", RoleCondition.TRADER, Role.TRADER_ADMIN)
            );
        }


        @ParameterizedTest
        @MethodSource("abnormalRoleProvider")
        @DisplayName("{0}")
        void roleChangeTest2(String testName, RoleCondition roleCondition, Role previousRole){

            //given
            User user = createOneUser(previousRole);
            when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));

            // when, then 유저가 있다고 가정하고 userId는 값을 넣어줌.
            assertThatThrownBy( () -> userAdminService.modifyRole(1L, roleCondition))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.INVALID_TYPE_VALUE.getMessage());

        }

        static Stream<Arguments> abnormalRoleProvider() {
            return Stream.of(
                    arguments("INVESTOR_ADMIN -> ADMIN으로", RoleCondition.ADMIN, Role.INVESTOR_ADMIN),
                    arguments("TRADER_ADMIN -> ADMIN으로", RoleCondition.ADMIN, Role.TRADER_ADMIN),
                    arguments("TRADER -> INVESTOR", RoleCondition.INVESTOR, Role.TRADER),
                    arguments("TRADER_ADMIN -> INVESTOR", RoleCondition.INVESTOR, Role.TRADER_ADMIN),
                    arguments("INVESTOR -> TRADER", RoleCondition.TRADER, Role.INVESTOR),
                    arguments("INVESTOR_ADMIN -> TRADER", RoleCondition.TRADER, Role.INVESTOR_ADMIN)
            );
        }



    }


}
