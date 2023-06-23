package com.zerobase.storeReservation.member.service;

import static com.zerobase.storeReservation.member.exception.ErrorCode.ALREADY_REGISTER_USER;
import static com.zerobase.storeReservation.member.exception.ErrorCode.EMAIL_CHECK_FAIL;
import static com.zerobase.storeReservation.member.exception.ErrorCode.PASSWORD_CHECK_FAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.zerobase.storeReservation.common.config.JwtAuthenticationProvider;
import com.zerobase.storeReservation.common.type.MemberType;
import com.zerobase.storeReservation.member.domain.Form.SignInForm;
import com.zerobase.storeReservation.member.domain.Form.SignUpForm;
import com.zerobase.storeReservation.member.domain.model.Partner;
import com.zerobase.storeReservation.member.domain.repository.PartnerRepository;
import com.zerobase.storeReservation.member.exception.CustomException;
import com.zerobase.storeReservation.member.util.PasswordEncoderUtil;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootTest
class PartnerServiceTest {
    @InjectMocks
    PartnerService partnerService;
    @Mock
    PartnerRepository partnerRepository;
    @Mock
    private JwtAuthenticationProvider provider;
    @Mock
    private PasswordEncoderUtil encoderUtil;
    @Mock
    private BCryptPasswordEncoder encoder;

    @Test
    @DisplayName("회원 가입 성공")
    void signUp() {
        //given
        SignUpForm form = SignUpForm.builder()
            .email("test1@example.com")
            .password("string123!")
            .build();

        when(partnerRepository.findByEmail(form.getEmail())).thenReturn(Optional.empty());
        when(encoderUtil.encodePassword(anyString(), any(BCryptPasswordEncoder.class))).thenReturn("encodedPassword");
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
            .password("string123!")
            .build();

        when(encoderUtil.encodePassword(anyString(), any(BCryptPasswordEncoder.class)))
            .thenReturn("encodedPassword");
        when(partnerRepository.findByEmail(form.getEmail()))
            .thenReturn(Optional.of(new Partner()));

        // When
        CustomException exception = assertThrows(CustomException.class,
            () -> partnerService.signUp(form));

        // Then
        assertEquals(ALREADY_REGISTER_USER, exception.getErrorCode());
        verify(partnerRepository, times(1)).findByEmail(form.getEmail());
        verify(partnerRepository, never()).save(any(Partner.class));
    }

    @Test
    @DisplayName("로그인 성공")
    public void partnerLoginToken() {
        // Given
        SignInForm form = SignInForm.builder()
            .email("test@example.com")
            .password("string123!")
            .build();

        Partner partner = new Partner();
        partner.setEmail("test@example.com");
        String encodedPassword = encoderUtil.encodePassword("string123!", encoder);
        partner.setPassword(encodedPassword);

        when(partnerRepository.findByEmail(anyString())).thenReturn(Optional.of(partner));
        when(encoderUtil.matchPassword(form.getPassword(), partner.getPassword(), encoder))
            .thenReturn(true);
        when(provider.createToken(partner.getEmail(), partner.getId(), MemberType.PARTNER))
            .thenReturn("testToken");

        // When
        String token = partnerService.partnerLoginToken(form);

        // Then
        assertEquals("testToken", token);
    }

    @Test
    @DisplayName("로그인 실패-이메일 찾을 수 없음")
    void partnerLoginToken_fail_email() {
        //Given
        SignInForm form = SignInForm.builder()
            .email("test@example.com")
            .password("string123!")
            .build();

        when(partnerRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        //When
        CustomException exception = assertThrows(CustomException.class,
            () -> partnerService.partnerLoginToken(form));
        //Then
        assertEquals(EMAIL_CHECK_FAIL, exception.getErrorCode());
        verify(provider, never()).createToken(anyString(), anyLong(), any(
            MemberType.class));
    }

    @Test
    @DisplayName("로그인 실패-비밀번호 일치하지 않음")
    void partnerLoginToken_fail_password() {
        //Given
        SignInForm form = SignInForm.builder()
            .email("test@example.com")
            .password("string123!")
            .build();

        Partner partner = new Partner();
        partner.setEmail("test@example.com");
        partner.setPassword("encodedPassword");

        when(partnerRepository.findByEmail(anyString())).thenReturn(Optional.of(partner));
        when(encoderUtil.matchPassword(any(String.class), any(String.class), any(BCryptPasswordEncoder.class)))
            .thenReturn(false);
        //When
        CustomException exception = assertThrows(CustomException.class,
            () -> partnerService.partnerLoginToken(form));
        //Then
        assertEquals(PASSWORD_CHECK_FAIL, exception.getErrorCode());
        verify(provider, never()).createToken(anyString(), anyLong(), any(
            MemberType.class));
    }
}