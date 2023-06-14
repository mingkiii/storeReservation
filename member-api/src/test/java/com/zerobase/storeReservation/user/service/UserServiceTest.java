package com.zerobase.storeReservation.user.service;

import static com.zerobase.storeReservation.user.exception.ErrorCode.ALREADY_REGISTER_USER;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.storeReservation.user.domain.Form.SignUpForm;
import com.zerobase.storeReservation.user.domain.model.User;
import com.zerobase.storeReservation.user.domain.repository.UserRepository;
import com.zerobase.storeReservation.user.exception.CustomException;
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

    @Test
    @DisplayName("회원 가입 성공")
    void userSignUp() {
        //given
        SignUpForm form = SignUpForm.builder()
            .email("test@example.com")
            .build();

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        //when
        String result = userService.signUp(form);
        //then
        assertEquals("회원 가입에 성공하였습니다.", result);
        verify(userRepository, times(1)).existsByEmail(form.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    @DisplayName("회원 가입 실패 - 이미 가입된 이메일")
    void signUp_ExistingEmail_ThrowsCustomException() {
        // Given
        SignUpForm form = SignUpForm.builder()
            .email("test@example.com")
            .build();

        when(userRepository.existsByEmail(form.getEmail())).thenReturn(true);

        // When
        CustomException exception = assertThrows(CustomException.class, () -> userService.signUp(form));

        // Then
        assertEquals(ALREADY_REGISTER_USER, exception.getErrorCode());
        verify(userRepository, times(1)).existsByEmail(form.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }
}