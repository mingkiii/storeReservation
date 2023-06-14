package com.zerobase.storeReservation.member.service;

import static com.zerobase.storeReservation.member.exception.ErrorCode.ALREADY_REGISTER_USER;
import static com.zerobase.storeReservation.member.exception.ErrorCode.LOGIN_CHECK_FAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.storeReservation.common.config.JwtAuthenticationProvider;
import com.zerobase.storeReservation.common.type.MemberType;
import com.zerobase.storeReservation.member.domain.Form.SignInForm;
import com.zerobase.storeReservation.member.domain.Form.SignUpForm;
import com.zerobase.storeReservation.member.domain.model.User;
import com.zerobase.storeReservation.member.domain.repository.UserRepository;
import com.zerobase.storeReservation.member.exception.CustomException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {
    @InjectMocks
    UserService userService;
    @Mock
    UserRepository userRepository;
    @Mock
    private JwtAuthenticationProvider provider;

    @Test
    @DisplayName("회원 가입 성공")
    void userSignUp() {
        // Given
        SignUpForm form = SignUpForm.builder()
            .email("test@example.com")
            .build();

        when(userRepository.findByEmail(form.getEmail()))
            .thenReturn(Optional.empty());
        // When
        String result = userService.signUp(form);
        // Then
        assertEquals("회원 가입에 성공하였습니다.", result);
        verify(userRepository, times(1)).findByEmail(form.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("회원 가입 실패 - 이미 가입된 이메일")
    void signUp_ExistingEmail_ThrowsCustomException() {
        // Given
        SignUpForm form = SignUpForm.builder()
            .email("test@example.com")
            .build();

        when(userRepository.findByEmail(form.getEmail()))
            .thenReturn(Optional.of(User.from(form)));

        // When
        CustomException exception = assertThrows(CustomException.class, () -> userService.signUp(form));

        // Then
        assertEquals(ALREADY_REGISTER_USER, exception.getErrorCode());
        verify(userRepository, times(1)).findByEmail(form.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("로그인 성공")
    public void userLoginToken() {
        // Given
        SignInForm form = SignInForm.builder()
            .email("test@example.com")
            .password("aaaa123!")
            .build();

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("aaaa123!");

        given(userRepository.findByEmailAndPassword(form.getEmail(), form.getPassword()))
            .willReturn(Optional.of(user));
        given(provider.createToken(user.getEmail(), user.getId(), MemberType.USER))
            .willReturn("testToken");

        // When
        String token = userService.userLoginToken(form);

        // Then
        assertEquals("testToken", token);
    }

    @Test
    @DisplayName("로그인 실패-아이디, 패스워드 불일치")
    void userLoginToken_fail() {
        //Given
        SignInForm form = SignInForm.builder()
            .email("test@example.com")
            .password("aaaa123!")
            .build();

        given(userRepository.findByEmailAndPassword(form.getEmail(), form.getPassword()))
            .willReturn(Optional.empty());
        //When
        CustomException exception = assertThrows(CustomException.class, () -> userService.userLoginToken(form));
        //Then
        assertEquals(LOGIN_CHECK_FAIL, exception.getErrorCode());
        verify(userRepository, times(1))
            .findByEmailAndPassword(form.getEmail(), form.getPassword());
        verify(provider, never()).createToken(anyString(), anyLong(), any(
            MemberType.class));
    }
}