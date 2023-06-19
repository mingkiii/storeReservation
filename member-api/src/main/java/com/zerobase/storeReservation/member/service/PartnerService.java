package com.zerobase.storeReservation.member.service;

import static com.zerobase.storeReservation.member.exception.ErrorCode.ALREADY_REGISTER_USER;
import static com.zerobase.storeReservation.member.exception.ErrorCode.LOGIN_CHECK_FAIL;

import com.zerobase.storeReservation.common.type.MemberType;
import com.zerobase.storeReservation.common.config.JwtAuthenticationProvider;
import com.zerobase.storeReservation.member.domain.Form.SignInForm;
import com.zerobase.storeReservation.member.domain.Form.SignUpForm;
import com.zerobase.storeReservation.member.domain.model.Partner;
import com.zerobase.storeReservation.member.domain.repository.PartnerRepository;
import com.zerobase.storeReservation.member.exception.CustomException;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PartnerService {
    private final PartnerRepository partnerRepository;
    private final JwtAuthenticationProvider provider;

    // 회원가입 기능
    public String signUp(SignUpForm form) {
        // 이미 회원 가입된 이메일인지 검사
        if (isEmailExist(form.getEmail())) {
            throw new CustomException(ALREADY_REGISTER_USER);
        } else {
            partnerRepository.save(Partner.from(form));
            return "회원 가입에 성공하였습니다.";
        }
    }

    // 로그인 성공 시 토큰 생성
    public String partnerLoginToken(SignInForm form) {
        Partner partner = findValidPartner(form.getEmail(), form.getPassword())
            .orElseThrow(() -> new CustomException(LOGIN_CHECK_FAIL));

        return provider.createToken(partner.getEmail(), partner.getId(), MemberType.PARTNER);
    }

    public Optional<Partner> findByIdAndEmail(Long id, String email) {
        return partnerRepository.findByIdAndEmail(id, email);
    }

    // 로그인 시 회원가입 된 이메일, 비밀번호인지 검사
    private Optional<Partner> findValidPartner(String email, String password) {
        return partnerRepository.findByEmailAndPassword(email, password);
    }

    private boolean isEmailExist(String email) {
        return partnerRepository.findByEmail(email).isPresent();
    }
}
