package com.zerobase.storeReservation.user.service;

import static com.zerobase.storeReservation.user.exception.ErrorCode.ALREADY_REGISTER_USER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.storeReservation.user.domain.Form.SignUpForm;
import com.zerobase.storeReservation.user.domain.model.Partner;
import com.zerobase.storeReservation.user.domain.repository.PartnerRepository;
import com.zerobase.storeReservation.user.exception.CustomException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PartnerServiceTest {
    @InjectMocks
    PartnerService partnerService;
    @Mock
    PartnerRepository partnerRepository;

    @Test
    @DisplayName("회원 가입 성공")
    void signUp() {
        //given
        SignUpForm form = SignUpForm.builder()
            .email("test1@example.com")
            .build();

        when(partnerRepository.existsByEmail(anyString())).thenReturn(false);
        //when
        String result = partnerService.signUp(form);
        //then
        assertEquals("회원 가입에 성공하였습니다.", result);
        verify(partnerRepository, times(1)).existsByEmail(form.getEmail());
        verify(partnerRepository, times(1)).save(any(Partner.class));
    }

    @Test
    @DisplayName("회원 가입 실패 - 이미 가입된 이메일")
    void signUp_ExistingEmail_ThrowsCustomException() {
        // Given
        SignUpForm form = SignUpForm.builder()
            .email("test1@example.com")
            .build();

        when(partnerRepository.existsByEmail(form.getEmail())).thenReturn(true);

        // When
        CustomException exception = assertThrows(CustomException.class, () -> partnerService.signUp(form));

        // Then
        assertEquals(ALREADY_REGISTER_USER, exception.getErrorCode());
        verify(partnerRepository, times(1)).existsByEmail(form.getEmail());
        verify(partnerRepository, never()).save(any(Partner.class));
    }
}