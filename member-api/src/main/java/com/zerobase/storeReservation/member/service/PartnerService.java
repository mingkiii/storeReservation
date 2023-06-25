package com.zerobase.storeReservation.member.service;

import static com.zerobase.storeReservation.member.exception.ErrorCode.ALREADY_REGISTER_USER;
import static com.zerobase.storeReservation.member.exception.ErrorCode.LOGIN_FAIL;

import com.zerobase.storeReservation.common.config.JwtAuthenticationProvider;
import com.zerobase.storeReservation.common.type.MemberType;
import com.zerobase.storeReservation.member.domain.Form.SignInForm;
import com.zerobase.storeReservation.member.domain.Form.SignUpForm;
import com.zerobase.storeReservation.member.domain.model.Partner;
import com.zerobase.storeReservation.member.domain.repository.PartnerRepository;
import com.zerobase.storeReservation.member.exception.CustomException;
import com.zerobase.storeReservation.member.util.PasswordEncoderUtil;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartnerService {
    private final PartnerRepository partnerRepository;
    private final JwtAuthenticationProvider provider;
    private final PasswordEncoderUtil encoderUtil;
    private final BCryptPasswordEncoder encoder;

    // 회원가입 기능
    public String signUp(SignUpForm form) {
        // 이미 회원 가입된 이메일인지 검사
        if (partnerRepository.findByEmail(form.getEmail()).isPresent()) {
            throw new CustomException(ALREADY_REGISTER_USER);
        } else {
            String encodePassword = encoderUtil.encodePassword(form.getPassword(), encoder);
            partnerRepository.save(Partner.of(form, encodePassword));
            return "회원 가입에 성공하였습니다.";
        }
    }

    // 로그인 성공 시 토큰 생성
    public String partnerLoginToken(SignInForm form) {
        // 이메일이 존재하지 않으면 예외 발생
        Partner partner = partnerRepository.findByEmail(form.getEmail())
            .orElseThrow(() -> new CustomException(LOGIN_FAIL));
        // 비밀번호 일치하지 않으면 예외 발생
        if (!encoderUtil.matchPassword(form.getPassword(), partner.getPassword(), encoder)) {
            throw new CustomException(LOGIN_FAIL);
        }

        return provider.createToken(partner.getEmail(), partner.getId(), MemberType.PARTNER);
    }

    public Optional<Partner> findByIdAndEmail(Long id, String email) {
        return partnerRepository.findByIdAndEmail(id, email);
    }
}
