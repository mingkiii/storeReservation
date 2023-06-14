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

import com.zerobase.storeReservation.domain.common.MemberType;
import com.zerobase.storeReservation.domain.config.JwtAuthenticationProvider;
import com.zerobase.storeReservation.member.domain.Form.SignInForm;
import com.zerobase.storeReservation.member.domain.Form.SignUpForm;
import com.zerobase.storeReservation.member.domain.model.Partner;
import com.zerobase.storeReservation.member.domain.repository.PartnerRepository;
import com.zerobase.storeReservation.member.exception.CustomException;
import java.util.Optional;
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
    @Mock
    private JwtAuthenticationProvider provider;

    @Test
    @DisplayName("회원 가입 성공")
    void signUp() {
        //given
        SignUpForm form = SignUpForm.builder()
            .email("test1@example.com")
            .build();

        when(partnerRepository.findByEmail(form.getEmail()))
            .thenReturn(Optional.empty());
        //when
        String result = partnerService.signUp(form);
        //then
        assertEquals("회원 가입에 성공하였습니다.", result);
        verify(partnerRepository, times(1)).findByEmail(form.getEmail());
        verify(partnerRepository, times(1)).save(any(Partner.class));
    }

    @Test
    @DisplayName("회원 가입 실패 - 이미 가입된 이메일")
    void signUp_ExistingEmail_ThrowsCustomException() {
        // Given
        SignUpForm form = SignUpForm.builder()
            .email("test1@example.com")
            .build();

        when(partnerRepository.findByEmail(form.getEmail()))
            .thenReturn(Optional.of(Partner.from(form)));

        // When
        CustomException exception = assertThrows(CustomException.class, () -> partnerService.signUp(form));

        // Then
        assertEquals(ALREADY_REGISTER_USER, exception.getErrorCode());
        verify(partnerRepository, times(1)).findByEmail(form.getEmail());
        verify(partnerRepository, never()).save(any(Partner.class));
    }

    @Test
    @DisplayName("로그인 성공")
    public void userLoginToken() {
        // Given
        SignInForm form = SignInForm.builder()
            .email("test@example.com")
            .password("aaaa123!")
            .build();

        Partner partner = new Partner();
        partner.setEmail("test@example.com");
        partner.setPassword("aaaa123!");

        given(partnerRepository.findByEmailAndPassword(form.getEmail(), form.getPassword()))
            .willReturn(Optional.of(partner));
        given(provider.createToken(partner.getEmail(), partner.getId(), MemberType.PARTNER))
            .willReturn("testToken");

        // When
        String token = partnerService.partnerLoginToken(form);

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

        given(partnerRepository.findByEmailAndPassword(form.getEmail(), form.getPassword()))
            .willReturn(Optional.empty());
        //When
        CustomException exception = assertThrows(CustomException.class, () -> partnerService.partnerLoginToken(form));
        //Then
        assertEquals(LOGIN_CHECK_FAIL, exception.getErrorCode());
        verify(partnerRepository, times(1))
            .findByEmailAndPassword(form.getEmail(), form.getPassword());
        verify(provider, never()).createToken(anyString(), anyLong(), any(
            MemberType.class));
    }
}